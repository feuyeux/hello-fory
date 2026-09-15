# Hello fory (Scala)

This example demonstrates using Apache fory with Scala 3.

## Prerequisites

- JDK 21+
- Maven 3.8+

## Run Instructions

```bash
mvn clean package

mvn exec:java
```

## Package Information

- Dependency: `org.apache.fory:fory-scala_3` 1.7.2 (Scala type support on top of `fory-core`)
- Scala 3.3.8, the version `fory-scala_3` 1.7.2 is published against

## Notes

- The model uses `java.util.List` / `java.util.Map` / boxed `java.lang.Integer` so the xlang wire
  types line up with the other samples. Scala collections would serialize as Scala types.
- The struct is registered as `org.feuyeux.fory.UserInfo`, the same name every other language
  sample uses.
- If your Maven mirror does not carry `fory-scala_3` yet, build with a settings file that has no
  mirror (`mvn -s <settings-without-mirrors.xml> ...`) so Maven Central is used directly.
