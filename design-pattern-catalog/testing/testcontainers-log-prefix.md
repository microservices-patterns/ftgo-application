# Testcontainers Log Prefix Convention

## Problem

When multiple containers run during a test, their log output is interleaved. Without a consistent prefix, it's difficult to identify which container produced a given log line — especially when reading test result XML files from CI artifacts.

## Example

### Before

Log prefixes were inconsistent — some used bare names, some had colons, some had `SVC` prefix:

- `"cdc:"`
- `"order-history:"`
- `"SVC order-service:"`

### After

All containers use a consistent `"SVC <name>"` prefix:

- `"SVC cdc"`
- `"SVC order-history"`
- `"SVC order-service"`

This produces log lines like:

```
[SVC order-service] STDOUT: 2026-03-13T00:20:32.538Z ERROR 1 --- Application run failed
```

## When to Apply

- Any Testcontainers-based test that starts service containers (component tests, end-to-end tests)
- When adding a new service container to an existing test suite
- When the test captures container logs via `Slf4jLogConsumer`

## Key Principles

- **Use `SVC` prefix for all containers** — this makes container logs searchable with a single pattern (`SVC <name>`) across all test types
- **No trailing colon in the prefix** — `Slf4jLogConsumer` already formats the output with brackets and separators; adding a colon is redundant
- **Name matches the service directory** — use the same name as the service directory (e.g., `ftgo-order-service`) or a recognizable short name (e.g., `kafka`, `cdc`)
- **Apply consistently across all test types** — component tests and end-to-end tests should use the same convention so CI debugging workflows don't need to account for different formats
