# Hello Fory

This repository contains examples of using Apache fory serialization library in various programming languages.

## Languages

1. [Java](./hello-fory-java/README.md)
1. [Kotlin](./hello-fory-kotlin/README.md)
1. [Scala](./hello-fory-scala/README.md)
1. [Python](./hello-fory-python/README.md)
1. [Go](./hello-fory-go/README.md)
1. [JavaScript](./hello-fory-js/README.md)
1. [Rust](./hello-fory-rust/README.md)
1. [C#](./hello-fory-csharp/README.md)
1. [Dart](./hello-fory-dart/README.md)
1. [C++](./hello-fory-cpp/README.md)
1. [Swift](./hello-fory-swift/README.md)

## Reference

- <https://fory.apache.org>
- <https://github.com/apache/fory>

## Versions

All samples target **Apache Fory 1.7.2** and register the same cross-language type
name `org.feuyeux.fory.UserInfo`, so a payload written by one sample can be read by
the others.

| Language   | Artifact                              | Version | Build tool          |
| ---------- | ------------------------------------- | ------- | ------------------- |
| Java       | `org.apache.fory:fory-core`           | 1.7.2   | Maven               |
| Kotlin     | `org.apache.fory:fory-kotlin` (+ ksp) | 1.7.2   | Maven               |
| Scala 3    | `org.apache.fory:fory-scala_3`        | 1.7.2   | Maven               |
| Python     | `pyfory`                              | 1.7.2   | pip / uv            |
| Go         | `github.com/apache/fory/go/fory`      | v1.7.2  | go modules          |
| JavaScript | `@apache-fory/core`                   | 1.7.2   | npm                 |
| Rust       | `fory`                                | 1.7.2   | Cargo               |
| C#         | `Apache.Fory`                         | 1.7.2   | dotnet / NuGet      |
| Dart       | `fory`                                | 1.7.2   | pub + build_runner  |
| C++        | source-only (`FetchContent` v1.7.2)   | 1.7.2   | CMake               |
| Swift      | `Fory` (SwiftPM, tag v1.7.2)          | 1.7.2   | SwiftPM             |

Each sample writes `workspace/userinfo_<lang>.fory` and then tries to read every
other sample's payload back. All 121 reader/writer combinations round-trip.

For that to work, every sample has to agree on more than the version:

- **Same registered name** — `org.feuyeux.fory.UserInfo` (Java/Kotlin/Scala register it with an
  explicit namespace instead of relying on the JVM package name; the C# sample keeps its CLR
  namespace out of the way too).
- **Same Fory config** — xlang + reference tracking + compatible mode. C#, Dart and Swift are
  xlang-only, and Dart takes reference tracking as a per-call flag (`serialize(value, trackRef: true)`).
- **Same wire types per field** — notably:
  - `age` / `scores` are 32-bit (`Int32` annotations in Python, `Type.int32()` in JS,
    `@ForyField(type: Int32Type())` / `@ListField(element: Int32Type())` in Dart).
  - `avatar` is a BINARY blob, which needs `#[fory(bytes)]` in Rust,
    `fory:"type=bytes"` in Go and `Data` in Swift; without them those emit a dense uint8 array
    that the others cannot read.
  - `userId` / `createdAt` are 64-bit, so they are `BigInt` in JavaScript.
- **Static serializers where the runtime demands them** — Kotlin needs `@ForyStruct` plus
  `fory-kotlin-ksp`, Dart needs `@ForyStruct` plus `build_runner`, C# gets them from the
  `Apache.Fory` source generator, C++ needs the `FORY_STRUCT` macro, and Swift needs
  `@ForyStruct`, which expands to the serializer at compile time through a Swift macro.
