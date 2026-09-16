# Hello fory (Swift)

This example demonstrates using Apache fory with Swift 6.

## Prerequisites

- Swift 6.0+ toolchain
  ([install instructions](https://www.swift.org/install/linux/ubuntu))

## Run Instructions

```bash
swift build

# run from the sample directory so ../workspace resolves like in the other samples
./.build/debug/HelloFory
```

## Package Information

- Dependency: [apache/fory](https://github.com/apache/fory) 1.7.2, SwiftPM resolves the
  `Fory` product from the repository root `Package.swift`.
- The first build also fetches and compiles `swiftlang/swift-syntax` for the macro target,
  so it takes several minutes.

## Notes

- **`@ForyStruct` drives a macro, not runtime reflection.** Swift has no reflective
  serializer fallback here; the attribute expands into the `Serializer` conformance at
  compile time and needs no separate build step (unlike Kotlin KSP or Dart `build_runner`).
- Field names are used verbatim as the wire field names, so the Swift property names are
  camelCase to match the other samples. `@ForyField(id: N)` would switch a field to
  tag-based identity and break the other samples' name-based lookup.
- `Int32` keeps `age` and the `scores` elements on the 32-bit wire type, and `Data` maps to
  Fory's BINARY blob. A plain `[UInt8]` would become a dense uint8 array that the other
  samples cannot read.
- Reference tracking plus compatible mode come from `Fory(ref: true, compatible: true)`.
  The Swift implementation is xlang-only, so there is no xlang switch.
- `Fory` keeps one reusable read/write context pair and is not thread-safe; one instance
  per thread.
- The struct is registered as `org.feuyeux.fory.UserInfo`, the same name every other
  language sample uses.
