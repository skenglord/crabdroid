#!/bin/bash

# Exit on error
set -e

echo "Scaffolding Baby Scratch directory structure..."

# 1. Create the full directory structure
mkdir -p app/src/main/cpp
mkdir -p app/src/main/kotlin/com/babyscratch/audio
mkdir -p app/src/main/kotlin/com/babyscratch/ui/controls
mkdir -p app/src/main/kotlin/com/babyscratch/viewmodel
mkdir -p app/src/main/assets/beat
mkdir -p app/src/main/assets/scratch

# 2. Initialize git and add the Oboe submodule
if [ ! -d ".git" ]; then
    echo "Initializing git repository..."
    git init
fi

echo "Adding Oboe git submodule..."
# Add oboe to the exact path expected by CMakeLists.txt
git submodule add https://github.com/google/oboe.git app/src/main/cpp/oboe
git submodule update --init --recursive

# 3. Print success message
echo ""
echo "✅ Ready to open in Android Studio"
echo "⚠️  Don't forget to add your audio files:"
echo "    - app/src/main/assets/scratch/scratch_record.wav"
echo "    - app/src/main/assets/beat/beat_loop.wav"
