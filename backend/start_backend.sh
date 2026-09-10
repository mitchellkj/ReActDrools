#!/usr/bin/env bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

echo "=================================================="
echo "🚀 Starting ReAct Drools Spring Boot Backend"
echo "   Java: $(java -version 2>&1 | head -n 1)"
echo "   Port: 8080"
echo "=================================================="

if command -v ./gradlew &> /dev/null; then
    exec ./gradlew bootRun
elif command -v gradle &> /dev/null; then
    exec gradle bootRun
else
    echo "❌ Error: Neither ./gradlew nor gradle was found in PATH."
    exit 1
fi
