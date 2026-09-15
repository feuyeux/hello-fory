# Hello fory (C#)

This example demonstrates using Apache fory with C# / .NET.

## Prerequisites

- .NET SDK 8.0+

## Run Instructions

```bash
dotnet run
```

## Package Information

- Package: [Apache.Fory](https://www.nuget.org/packages/Apache.Fory) 1.7.2 (runtime plus the
  source generator for `[ForyStruct]` types)

## Notes

- The C# implementation is xlang-only, so there is no `Xlang` switch on the builder; only
  `TrackRef(true)` and `Compatible(true)` are set to match the other samples.
- `int?` keeps `Age`/`Scores` on the 32-bit wire type and `byte[]` is a BINARY blob.
- The struct is registered as `org.feuyeux.fory.UserInfo`, the same name every other language
  sample uses. Note the CLR namespace is deliberately **not** `Org.Feuyeux.Fory`, because that
  would shadow the `Apache.Fory.Fory` type.
