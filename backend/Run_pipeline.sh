#!/usr/bin/env bash
#
# Minimal orchestration for: OCR text -> Regex Engine -> Compliance Engine.
#
# This does NOT modify the Regex Engine, OCR Engine, or compliance rule
# logic. It only automates the handoff: run the Regex Engine, then pass
# its real output JSON straight into the Compliance Engine.
#
# Usage:
#   ./run_pipeline.sh
#
# Prerequisites (one-time):
#   - backend/regex-engine/input/ocr-output.txt must exist
#     (produced by the OCR Engine).
#   - Compliance Engine dependencies (Jackson) resolved locally, e.g.:
#       cd backend
#       mvn -q dependency:copy-dependencies -DoutputDirectory=lib
#
# No absolute/machine-specific paths are used - everything is relative
# to this script's own location, so it works after a fresh clone.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR"
REGEX_DIR="$BACKEND_DIR/regex-engine"
COMPLIANCE_DIR="$BACKEND_DIR/compliance-engine"
LIB_DIR="$BACKEND_DIR/lib"

echo "=================================================="
echo " STEP 1/2: Regex Engine (TXT -> JSON)"
echo "=================================================="

mkdir -p "$REGEX_DIR/out"
javac -d "$REGEX_DIR/out" "$REGEX_DIR"/src/*.java

# The Regex Engine resolves "input/ocr-output.txt" and
# "output/extracted-product.json" relative to the JVM's actual working
# directory (java.nio.file.Path - unaffected by -Duser.dir), so we cd
# into regex-engine/ in a subshell before running it.
( cd "$REGEX_DIR" && java -cp out main )

REGEX_OUTPUT="$REGEX_DIR/output/extracted-product.json"

if [ ! -f "$REGEX_OUTPUT" ]; then
    echo "ERROR: Regex Engine did not produce $REGEX_OUTPUT"
    exit 1
fi

echo
echo "=================================================="
echo " STEP 2/2: Compliance Engine (JSON -> Report)"
echo "=================================================="

if [ ! -d "$LIB_DIR" ] || [ -z "$(ls -A "$LIB_DIR" 2>/dev/null)" ]; then
    echo "ERROR: $LIB_DIR is empty. Resolve dependencies first:"
    echo "    cd $BACKEND_DIR && mvn -q dependency:copy-dependencies -DoutputDirectory=lib"
    exit 1
fi

mkdir -p "$COMPLIANCE_DIR/out"
javac -cp "$LIB_DIR/*" -d "$COMPLIANCE_DIR/out" "$COMPLIANCE_DIR"/*.java
java -cp "$COMPLIANCE_DIR/out:$LIB_DIR/*" Main "$REGEX_OUTPUT"