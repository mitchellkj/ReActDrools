#!/usr/bin/env bash
# =============================================================================
# Launch script for ReAct Drools (Spring Boot Backend + Angular Frontend)
# =============================================================================

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "========================================================================"
echo "🎯 ReAct Drools Local Development Orchestrator"
echo "   Stack: Java 17 + Spring Boot 2.7 + Drools 7.74 & Angular 17"
echo "========================================================================"
echo ""
echo "To run both services in separate terminal windows:"
echo ""
echo "  Terminal 1 (Backend - Spring Boot / Drools on port 8080):"
echo "    cd $DIR/backend && ./start_backend.sh"
echo ""
echo "  Terminal 2 (Frontend - Angular 17 on port 4200):"
echo "    cd $DIR/frontend && ./start_frontend.sh"
echo ""
echo "========================================================================"
