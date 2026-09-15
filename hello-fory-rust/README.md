# Hello fory (Rust)

This example demonstrates using Apache fory with Rust.

## Prerequisites

- Rust 1.70+ (`fory` 1.7.2 sets `rust-version = "1.70"`)

## Run Instructions

```bash
cargo run
```

## Package Information

- Crate: [fory](https://crates.io/crates/fory) 1.7.2 (re-exports the derive macros, so
  `fory-core` is not needed directly)
- The struct derives `ForyStruct` and is registered as `org.feuyeux.fory.UserInfo`,
  the same name every other language sample uses.
