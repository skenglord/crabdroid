# app/src/main/cpp/CMakeLists.txt
# Purpose: CMake build script to compile the native audio engine and link the Oboe submodule.
# Dependencies: oboe (git submodule), ScratchEngine.cpp, AudioFileLoader.cpp

cmake_minimum_required(VERSION 3.22.1)

project("babyscratch")

# Include the Oboe library from the local submodule
add_subdirectory(oboe)

# Create the shared library for the JNI bridge
add_library(babyscratch SHARED
            ScratchEngine.cpp
            AudioFileLoader.cpp)

# Link the required libraries: Oboe, Android (for AssetManager), and Log
target_link_libraries(babyscratch
                      oboe::oboe
                      android
                      log)
