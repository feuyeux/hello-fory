# Hello fory (Java)

This example demonstrates using Apache fory with Java.

## Prerequisites

- JDK 21+
- Maven 3.8+

## Run Instructions

```bash
mvn clean package

mvn exec:java -Dexec.mainClass="org.feuyeux.io.HelloFory"
```

## Package Information

- Dependency: `org.apache.fory:fory-core` 1.7.2
- The Fory instance is built with `withXlang(true)` (the 1.x replacement for
  `withLanguage(Language.XLANG)`), and `UserInfo` is registered as
  `org.feuyeux.fory.UserInfo` — the same name every other language sample uses,
  so the payloads are interchangeable.
