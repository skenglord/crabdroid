// app/src/main/cpp/Resampler.h
// Purpose: Hermite interpolation inline functions for high-quality variable-rate audio resampling.
// Dependencies: <algorithm>

#ifndef BABYSCRATCH_RESAMPLER_H
#define BABYSCRATCH_RESAMPLER_H

#include <algorithm>

namespace babyscratch {

// 4-point Hermite cubic interpolation
inline float hermite(float y0, float y1, float y2, float y3, float t) {
    float c0 = y1;
    float c1 = 0.5f * (y2 - y0);
    float c2 = y0 - 2.5f * y1 + 2.0f * y2 - 0.5f * y3;
    float c3 = 0.5f * (y3 - y0) + 1.5f * (y1 - y2);
    return ((c3 * t + c2) * t + c1) * t + c0;
}

// Resample a single channel buffer at a specific fractional position
// Assumes pos >= 0. Wraps around using modulo for looping.
inline float resampleAt(const float* buf, int len, float pos) {
    if (len <= 0 || buf == nullptr) return 0.0f;
    
    int i = static_cast<int>(pos);
    float t = pos - static_cast<float>(i);
    
    // OPTIMIZATION: Replaced slow modulo (%) with fast branching
    int i0 = i - 1; if (i0 < 0) i0 += len;
    int i1 = i;     if (i1 >= len) i1 -= len;
    int i2 = i + 1; if (i2 >= len) i2 -= len;
    int i3 = i + 2; if (i3 >= len) i3 -= len;
    
    return hermite(buf[i0], buf[i1], buf[i2], buf[i3], t);
}

} // namespace babyscratch

#endif // BABYSCRATCH_RESAMPLER_H
