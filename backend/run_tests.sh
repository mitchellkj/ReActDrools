#!/usr/bin/env bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

echo "=================================================="
echo "🧪 Running ReAct Drools JUnit 5 Test Suite"
echo "   Java: $(java -version 2>&1 | head -n 1)"
echo "=================================================="

if command -v ./gradlew &> /dev/null; then
    exec ./gradlew test --info
elif command -v gradle &> /dev/null; then
    exec gradle test --info
else
    echo "❌ Error: Neither ./gradlew nor gradle was found in PATH."
    exit 1
fi
