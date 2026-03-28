// app/src/main/cpp/ScratchEngine.cpp
// Purpose: Oboe audio callback, atomics for lock-free state, and mixing logic.
// Dependencies: oboe, Resampler.h, AudioFileLoader.cpp

#include <jni.h>
#include <oboe/Oboe.h>
#include <atomic>
#include <mutex>
#include <android/asset_manager_jni.h>
#include "Resampler.h"

namespace babyscratch {
    // Declared in AudioFileLoader.cpp
    bool loadAudioFile(AAssetManager* assetManager, const char* filename, float** outData, int* outFrameCount, int* outChannels);
}

class ScratchEngine : public oboe::AudioStreamDataCallback {
public:
    ScratchEngine() = default;
    ~ScratchEngine() {
        stop();
        std::lock_guard<std::mutex> lock(bufferMutex_);
        delete[] scratchBuffer_;
        delete[] loopBuffer_;
    }

    void start(AAssetManager* assetManager) {
        // Load Scratch Record outside lock to avoid blocking audio thread
        float* rawScratch = nullptr;
        int scratchFrames = 0, scratchChannels = 0;
        float* newScratchBuffer = nullptr;
        if (babyscratch::loadAudioFile(assetManager, "scratch_record.wav", &rawScratch, &scratchFrames, &scratchChannels)) {
            newScratchBuffer = new float[scratchFrames];
            for (int i = 0; i < scratchFrames; ++i) {
                if (scratchChannels == 2) {
                    newScratchBuffer[i] = (rawScratch[i * 2] + rawScratch[i * 2 + 1]) * 0.5f; // Mix to mono
                } else {
                    newScratchBuffer[i] = rawScratch[i];
                }
            }
            delete[] rawScratch;
        }

        // Load Beat Loop outside lock
        float* rawLoop = nullptr;
        int loopFrames = 0, loopChannels = 0;
        float* newLoopBuffer = nullptr;
        if (babyscratch::loadAudioFile(assetManager, "beat_loop.wav", &rawLoop, &loopFrames, &loopChannels)) {
            newLoopBuffer = new float[loopFrames];
            for (int i = 0; i < loopFrames; ++i) {
                if (loopChannels == 2) {
                    newLoopBuffer[i] = (rawLoop[i * 2] + rawLoop[i * 2 + 1]) * 0.5f; // Mix to mono
                } else {
                    newLoopBuffer[i] = rawLoop[i];
                }
            }
            delete[] rawLoop;
        }

        {
            std::lock_guard<std::mutex> lock(bufferMutex_);
            if (newScratchBuffer) {
                delete[] scratchBuffer_;
                scratchBuffer_ = newScratchBuffer;
                scratchFrames_ = scratchFrames;
            }
            if (newLoopBuffer) {
                delete[] loopBuffer_;
                loopBuffer_ = newLoopBuffer;
                loopFrames_ = loopFrames;
            }
        }

        oboe::AudioStreamBuilder builder;
        builder.setPerformanceMode(oboe::PerformanceMode::LowLatency)
               .setSharingMode(oboe::SharingMode::Exclusive)
               .setFormat(oboe::AudioFormat::Float)
               .setChannelCount(oboe::ChannelCount::Stereo)
               .setSampleRate(48000)
               .setDataCallback(this);

        oboe::Result result = builder.openStream(stream_);
        if (result == oboe::Result::OK && stream_) {
            stream_->requestStart();
        }
    }

    void stop() {
        if (stream_) {
            stream_->requestStop();
            stream_->close();
            stream_.reset();
        }
    }

    void setVelocity(float velocity) { velocity_.store(velocity, std::memory_order_relaxed); }
    void setPitch(float pitch) { pitch_.store(pitch, std::memory_order_relaxed); }
    void setMuted(bool muted) { muted_.store(muted, std::memory_order_relaxed); }
    void setVolume(float volume) { volume_.store(volume, std::memory_order_relaxed); }
    void setPlaying(bool playing) { playing_.store(playing, std::memory_order_relaxed); }
    void setLoopPlaying(bool playing) { loopPlaying_.store(playing, std::memory_order_relaxed); }

    void swapBuffer(float* newBuffer, int numFrames) {
        float* oldBuffer = nullptr;
        {
            std::lock_guard<std::mutex> lock(bufferMutex_);
            oldBuffer = scratchBuffer_;
            scratchBuffer_ = newBuffer;
            scratchFrames_ = numFrames;
            scratchPlayhead_ = 0.0f;
        }
        delete[] oldBuffer; // Delete outside the lock to prevent blocking the audio thread!
    }

    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) override {
        float* out = static_cast<float*>(audioData);
        
        float velocity = velocity_.load(std::memory_order_relaxed);
        float pitch = pitch_.load(std::memory_order_relaxed);
        bool muted = muted_.load(std::memory_order_relaxed);
        float volume = volume_.load(std::memory_order_relaxed);
        bool playing = playing_.load(std::memory_order_relaxed);
        bool loopPlaying = loopPlaying_.load(std::memory_order_relaxed);

        // Try to lock. If we can't (because swapBuffer is holding it), output silence to avoid blocking the audio thread.
        bool locked = bufferMutex_.try_lock();

        for (int i = 0; i < numFrames; ++i) {
            float scratchSample = 0.0f;
            if (locked && scratchBuffer_ != nullptr && scratchFrames_ > 0 && playing) {
                scratchSample = babyscratch::resampleAt(scratchBuffer_, scratchFrames_, scratchPlayhead_);
                scratchPlayhead_ += velocity * pitch;
                
                // Wrap around logic
                while (scratchPlayhead_ >= scratchFrames_) scratchPlayhead_ -= scratchFrames_;
                while (scratchPlayhead_ < 0) scratchPlayhead_ += scratchFrames_;
            }
            
            if (muted) {
                scratchSample = 0.0f;
            }
            scratchSample *= volume;

            float loopSample = 0.0f;
            if (locked && loopBuffer_ != nullptr && loopFrames_ > 0 && loopPlaying) {
                loopSample = babyscratch::resampleAt(loopBuffer_, loopFrames_, loopPlayhead_);
                loopPlayhead_ += 1.0f; // Beat loop always plays at 1.0x speed
                
                while (loopPlayhead_ >= loopFrames_) loopPlayhead_ -= loopFrames_;
            }

            // Output stereo with hard clipping to prevent digital distortion
            float mixed = scratchSample + loopSample;
            if (mixed > 1.0f) mixed = 1.0f;
            else if (mixed < -1.0f) mixed = -1.0f;
            
            out[i * 2] = mixed;
            out[i * 2 + 1] = mixed;
        }

        if (locked) {
            bufferMutex_.unlock();
        } else {
            // Fill with silence if we couldn't acquire the lock
            for (int i = 0; i < numFrames * 2; ++i) {
                out[i] = 0.0f;
            }
        }

        return oboe::DataCallbackResult::Continue;
    }

private:
    std::shared_ptr<oboe::AudioStream> stream_;
    std::mutex bufferMutex_;

    std::atomic<float> velocity_{0.0f};
    std::atomic<float> pitch_{1.0f};
    std::atomic<bool> muted_{false};
    std::atomic<float> volume_{1.0f};
    std::atomic<bool> playing_{false};
    std::atomic<bool> loopPlaying_{false};

    float* scratchBuffer_ = nullptr;
    int scratchFrames_ = 0;
    float scratchPlayhead_ = 0.0f;

    float* loopBuffer_ = nullptr;
    int loopFrames_ = 0;
    float loopPlayhead_ = 0.0f;
};

// Global engine instance
static ScratchEngine* gEngine = nullptr;

extern "C" {

JNIEXPORT void JNICALL
Java_com_babyscratch_audio_ScratchEngine_startEngine(JNIEnv* env, jobject /* thiz */, jobject assetManager) {
    if (!gEngine) {
        gEngine = new ScratchEngine();
    }
    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    gEngine->start(mgr);
}

JNIEXPORT void JNICALL
Java_com_babyscratch_audio_ScratchEngine_stopEngine(JNIEnv* /* env */, jobject /* thiz */) {
    if (gEngine) {
        gEngine->stop();
        delete gEngine;
        gEngine = nullptr;
    }
}

JNIEXPORT void JNICALL
Java_com_babyscratch_audio_ScratchEngine_setVelocity(JNIEnv* /* env */, jobject /* thiz */, jfloat velocity) {
    if (gEngine) gEngine->setVelocity(velocity);
}

JNIEXPORT void JNICALL
Java_com_babyscratch_audio_ScratchEngine_setPitch(JNIEnv* /* env */, jobject /* thiz */, jfloat pitch) {
    if (gEngine) gEngine->setPitch(pitch);
}

JNIEXPORT void JNICALL
Java_com_babyscratch_audio_ScratchEngine_setMuted(JNIEnv* /* env */, jobject /* thiz */, jboolean muted) {
    if (gEngine) gEngine->setMuted(muted);
}

JNIEXPORT void JNICALL
Java_com_babyscratch_audio_ScratchEngine_setVolume(JNIEnv* /* env */, jobject /* thiz */, jfloat volume) {
    if (gEngine) gEngine->setVolume(volume);
}

JNIEXPORT void JNICALL
Java_com_babyscratch_audio_ScratchEngine_setPlaying(JNIEnv* /* env */, jobject /* thiz */, jboolean playing) {
    if (gEngine) gEngine->setPlaying(playing);
}

JNIEXPORT void JNICALL
Java_com_babyscratch_audio_ScratchEngine_setLoopPlaying(JNIEnv* /* env */, jobject /* thiz */, jboolean playing) {
    if (gEngine) gEngine->setLoopPlaying(playing);
}

JNIEXPORT void JNICALL
Java_com_babyscratch_audio_ScratchEngine_swapBuffer(JNIEnv* env, jobject /* thiz */, jfloatArray buffer) {
    if (gEngine && buffer != nullptr) {
        jsize length = env->GetArrayLength(buffer);
        float* newBuffer = new float[length];
        env->GetFloatArrayRegion(buffer, 0, length, newBuffer);
        gEngine->swapBuffer(newBuffer, length);
    }
}

} // extern "C"
