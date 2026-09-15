cmake_minimum_required(VERSION 3.16)

project(hello_fory_cpp LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

if(NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Release CACHE STRING "Build type" FORCE)
endif()

if(MSVC)
    # Fory's FORY_STRUCT macro needs the conforming preprocessor
    add_compile_options(/Zc:preprocessor)
endif()

# Apache Fory C++ is source-only: there is no released binary package, so the
# library is fetched and built from the tagged source tree.
include(FetchContent)
FetchContent_Declare(
    fory
    URL https://github.com/apache/fory/archive/refs/tags/v1.7.2.tar.gz
    SOURCE_SUBDIR cpp
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE
)
set(FORY_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(FORY_BUILD_SHARED OFF CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(fory)

add_executable(hello_fory hello_fory.cc)
target_link_libraries(hello_fory PRIVATE fory::serialization)
