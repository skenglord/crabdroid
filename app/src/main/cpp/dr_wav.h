// app/src/main/cpp/dr_wav.h
// Minimal dr_wav.h implementation for memory-based WAV decoding.
// In a real project, this would be the full dr_wav.h from https://github.com/mackron/dr_libs

#ifndef DR_WAV_H
#define DR_WAV_H

#include <stddef.h>
#include <stdint.h>

typedef struct {
    uint64_t totalPCMFrameCount;
    uint32_t channels;
    uint32_t sampleRate;
    // ... other fields ...
} drwav;

inline bool drwav_init_memory(drwav* pWav, const void* pData, size_t dataSize, void* pAllocationCallbacks) {
    // Mock implementation for compilation
    pWav->totalPCMFrameCount = 0;
    pWav->channels = 1;
    pWav->sampleRate = 48000;
    return true;
}

inline uint64_t drwav_read_pcm_frames_f32(drwav* pWav, uint64_t framesToRead, float* pBufferOut) {
    // Mock implementation for compilation
    return 0;
}

inline void drwav_uninit(drwav* pWav) {
    // Mock implementation
}

#endif
