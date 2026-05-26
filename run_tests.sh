#!/bin/bash
# UoK Bank — Test Runner
# Compiles and runs all JUnit 4 tests against an isolated temp database.
# Usage: ./run_tests.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

CP_PROD="lib/sqlite-jdbc.jar"
CP_TEST="lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar"
OUT_PROD="out"
OUT_TEST="out/test"

echo "======================================================"
echo "  UoK Bank — Test Suite"
echo "======================================================"
echo

# 1. Compile production sources (including service/)
echo "[1/3] Compiling production sources..."
find src -name "*.java" > sources.txt
mkdir -p "$OUT_PROD"
javac -cp "$CP_PROD" -d "$OUT_PROD" @sources.txt
echo "      OK"

# 2. Compile test sources
echo "[2/3] Compiling test sources..."
mkdir -p "$OUT_TEST"
javac -cp "$CP_PROD:$CP_TEST:$OUT_PROD" -d "$OUT_TEST" test/*.java
echo "      OK"

# 3. Run tests
echo "[3/3] Running tests..."
echo
java -cp "$OUT_PROD:$OUT_TEST:$CP_PROD:$CP_TEST" \
     org.junit.runner.JUnitCore \
     AccountDAOTest \
     TransactionDAOTest \
     LoanDAOTest \
     BankingRulesTest

echo
echo "======================================================"
echo "  All tests completed."
echo "======================================================"
