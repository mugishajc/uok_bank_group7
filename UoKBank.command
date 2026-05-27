#!/bin/bash
# UoK Bank — Double-click launcher for macOS
# This file opens in Terminal automatically when double-clicked in Finder.

echo "====================================================="
echo "  UoK Bank — University of Kigali"
echo "  MSc.IT — Advanced Programming with Java"
echo "  Dr. Josbert Nteziriza | Group 7 | 2026"
echo "====================================================="
echo

# Move to the folder where this script lives (works from any location)
cd "$(dirname "$0")"

# Check Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed on this computer."
    echo
    echo "Download Java 17 (free) from:"
    echo "  https://adoptium.net/"
    echo
    echo "Install it, then double-click this file again."
    read -p "Press Enter to close..."
    exit 1
fi

echo "Java found. Launching UoK Bank..."
echo
java -jar UoKBank.jar
