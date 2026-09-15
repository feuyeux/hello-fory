# Hello fory (Kotlin)

This example demonstrates using Apache fory with Kotlin.

## Prerequisites

- JDK 21+
- Maven 3.8+

## Run Instructions

```bash
mvn clean package

mvn exec:java
```

## Package Information

- Dependency: `org.apache.fory:fory-kotlin` 1.7.2 (Kotlin/JVM support on top of `fory-core`)
- Code generation: `org.apache.fory:fory-kotlin-ksp` 1.7.2, run through
  `me.kpavlov.ksp.maven:ksp-maven-plugin` (the same wiring Fory's own Kotlin build uses)

## Notes

- **`@ForyStruct` + KSP are mandatory.** In xlang mode `fory-kotlin` refuses reflective structs
  and fails with `Static generated serializer for Kotlin struct ... was not found`, so the
  serializer must be generated at build time. `build-helper-maven-plugin` adds
  `target/generated-sources/ksp` to the compile roots.
- **Use primary-constructor properties with non-null defaults.** Two KSP constraints bite here:
  mutable (`var`) properties require an explicit `@ForyField(id = N)`, which switches the field
  identity from name-based to tag-based and no longer matches the other samples; and a 9-field
  data class of *nullable* properties generates a `readCompatibleConstructor` that exceeds the
  JVM 64 KB method limit.
- Kotlin `Int` is int32 and `ByteArray` is a BINARY blob, which is what the other samples use.
  `@ArrayType ByteArray` would switch to a dense uint8 array and break cross-reads.
- The struct is registered as `org.feuyeux.fory.UserInfo`, the same name every other language
  sample uses.
