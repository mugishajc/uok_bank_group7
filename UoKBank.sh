#!/bin/bash
# UoK Bank - University of Kigali
# Mac / Linux launcher

echo "====================================================="
echo "  UoK Bank - University of Kigali"
echo "  MSc.IT - Advanced Programming with Java"
echo "  Dr. Josbert Nteziriza | Group 7 | 2026"
echo "====================================================="
echo

# Check Java
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed."
    echo "Download Java 17 from: https://adoptium.net/"
    exit 1
fi

# Run from the script's own directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo "Starting UoK Bank..."
java -jar UoKBank.jar
