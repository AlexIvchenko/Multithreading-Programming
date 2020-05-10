cmake_minimum_required(VERSION 3.15)
project(openmp)

set(CMAKE_CXX_STANDARD 17)
add_subdirectory(lib/googletest)

include_directories(lib/googletest/googletest/include)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fopenmp")
add_executable(openmp src/main.cpp src/Matrix.cpp src/Matrix.h src/Multiplier.cpp src/Multiplier.h src/SequentialMultiplier.cpp src/SequentialMultiplier.h src/OmpMultiplier.cpp src/OmpMultiplier.h)
add_executable(openmp_test src/Matrix.cpp src/Matrix.h src/Multiplier.cpp src/Multiplier.h src/SequentialMultiplier.cpp src/SequentialMultiplier.h src/OmpMultiplier.cpp src/OmpMultiplier.h tests/MultiplicationTests.cpp tests/MatrixIoTests.cpp)
target_link_libraries(openmp_test gtest gtest_main)