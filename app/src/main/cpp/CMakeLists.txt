# Minimum CMake version required for modern Android NDK toolchains
cmake_minimum_required(VERSION 3.22.1)

# Define the name of your native library project
project("aethersight")

# Create a shared library from your C++ source processor
add_library(
    aethersight
    SHARED
    aether-processor.cpp
)

# Find the built-in Android system logging library so __android_log_print works
find_library(
    log-lib
    log
)

# Link your shared library against the Android system libraries
target_link_libraries(
    aethersight
    ${log-lib}
)

