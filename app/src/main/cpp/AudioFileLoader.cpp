// app/src/main/cpp/AudioFileLoader.cpp
// Purpose: WAV decode to float PCM on native heap using dr_wav and Android AssetManager.
// Dependencies: dr_wav.h, <android/asset_manager.h>

#include <android/asset_manager.h>
#include "dr_wav.h"

namespace babyscratch {

// Loads a WAV file from the Android AssetManager and decodes it into a newly allocated float array.
// The caller is responsible for freeing the allocated memory using delete[].
bool loadAudioFile(AAssetManager* assetManager, const char* filename, float** outData, int* outFrameCount, int* outChannels) {
    if (!assetManager || !outData || !outFrameCount || !outChannels) {
        return false;
    }

    // Open the asset as a buffer
    AAsset* asset = AAssetManager_open(assetManager, filename, AASSET_MODE_BUFFER);
    if (!asset) {
        return false;
    }

    // Get a pointer to the asset memory
    const void* assetData = AAsset_getBuffer(asset);
    off_t assetLength = AAsset_getLength(asset);

    // Initialize dr_wav from memory
    drwav wav;
    if (!drwav_init_memory(&wav, assetData, assetLength, nullptr)) {
        AAsset_close(asset);
        return false;
    }

    // Allocate native heap memory for the PCM float data
    *outFrameCount = wav.totalPCMFrameCount;
    *outChannels = wav.channels;
    *outData = new float[wav.totalPCMFrameCount * wav.channels];

    // Decode the WAV data directly into the float buffer
    drwav_read_pcm_frames_f32(&wav, wav.totalPCMFrameCount, *outData);

    // Cleanup
    drwav_uninit(&wav);
    AAsset_close(asset);

    return true;
}

} // namespace babyscratch
