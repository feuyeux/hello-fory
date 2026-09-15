# Hello fory (Dart)

This example demonstrates using Apache fory with Dart.

## Prerequisites

- Dart SDK 3.7+

## Setup Instructions

Fory Dart generates serializers at build time (Flutter forbids `dart:mirrors`):

```bash
dart pub get
dart run build_runner build
```

## Run Instructions

```bash
dart run bin/hello_fory.dart
```

## Package Information

- Package: [fory](https://pub.dev/packages/fory) 1.7.2
- Generated code: `bin/hello_fory.fory.dart`, produced by `build_runner`. The generated
  registration class is named after the source file, so `hello_fory.dart` yields
  `HelloForyForyModule`.

## Notes

- `build.yaml` declares `bin/**` as a code-generation root; `bin/` is not scanned by default.
- `@ForyField(type: Int32Type())` and `@ListField(element: Int32Type())` keep `age`/`scores` on
  the 32-bit wire type; `Uint8List` is a BINARY blob.
- Reference tracking is a per-call flag in Dart: `fory.serialize(value, trackRef: true)`.
- The struct is registered as `org.feuyeux.fory.UserInfo`, the same name every other language
  sample uses.
