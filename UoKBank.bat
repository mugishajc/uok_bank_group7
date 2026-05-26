@echo off
setlocal enabledelayedexpansion
title UoK Bank - University of Kigali

echo =====================================================
echo   UoK Bank - University of Kigali
echo   MSc.IT - Advanced Programming with Java
echo   Dr. Josbert Nteziriza  ^|  Group 7  ^|  2026
echo =====================================================
echo.

:: Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed on this computer.
    echo.
    echo Please download Java 17 from:
    echo   https://adoptium.net/temurin/releases/
    echo.
    echo After installing, re-run this file.
    pause
    exit /b 1
)

:: Check Java version (needs 17+)
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VER=%%g
)

echo Java found. Starting UoK Bank...
echo.

:: Run the JAR from its own directory so the DB path is consistent
cd /d "%~dp0"
start "" java -jar UoKBank.jar

endlocal
