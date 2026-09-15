# Hello fory (JavaScript)

This example demonstrates using Apache fory with JavaScript.

## Prerequisites

- Node.js 20+
- npm 10+

## Setup Instructions

Apache Fory™ is published on npm, so a plain install is enough:

```bash
npm install
```

## Run Instructions

```bash
# Run the example
node main.js
```

## Package Information

- Source: [Apache Fory JavaScript](https://github.com/apache/fory/tree/main/javascript)
- Package: [@apache-fory/core](https://www.npmjs.com/package/@apache-fory/core) 1.7.2
- Optional: `@apache-fory/hps` adds native string-detection acceleration on Node.js 20+

## Notes

- `Type.int64()` fields map to JavaScript `BigInt` (`12345n`), not `number`.
- The struct is registered as `org.feuyeux.fory.UserInfo`, the same name every other
  language sample uses, so the payloads are interchangeable.
