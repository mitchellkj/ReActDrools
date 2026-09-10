#!/usr/bin/env bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

echo "=================================================="
echo "🅰️ Starting ReAct Drools Angular Frontend"
echo "   Node: $(node -v)"
echo "   Port: 4200 (Proxying /api to http://localhost:8080)"
echo "=================================================="

if [ ! -d "node_modules" ]; then
    echo "📦 Installing npm dependencies..."
    npm install
fi

exec npm start
