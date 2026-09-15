# Hello fory (C++)

This example demonstrates using Apache fory with C++17.

## Prerequisites

- CMake 3.16+
- A C++17 compiler (GCC 13 / Clang 16 or newer)

## Run Instructions

```bash
cmake -S . -B build -DCMAKE_BUILD_TYPE=Release
cmake --build build -j

# run from the sample directory so ../workspace resolves like in the other samples
./build/hello_fory
```

## Package Information

- Apache Fory C++ has **no released binary package**: the library is source-only. `CMakeLists.txt`
  pulls the `v1.7.2` source tarball with `FetchContent` (`SOURCE_SUBDIR cpp`) and links
  `fory::serialization`.
- Upstream also supports Bazel; CMake is used here to keep the sample self-contained.

## Notes

- `FORY_STRUCT(Type, fields...)` must come after all fields (and inside a `public:` section when
  used in a class).
- `int32_t` keeps `age`/`scores` on the 32-bit wire type and `std::vector<uint8_t>` is a BINARY
  blob.
- The struct is registered as `org.feuyeux.fory.UserInfo`, the same name every other language
  sample uses.
