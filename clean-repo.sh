#!/bin/bash -e

# Clean local Maven repository for contract stubs

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
rm -rf "$SCRIPT_DIR/build/repo"
