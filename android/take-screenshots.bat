@echo off
REM Script to capture screenshots from connected Android device/emulator
REM Make sure your device is connected and the app is running

REM Set ADB path - Update this if your Android SDK is in a different location
set ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe

REM Check if ADB exists
if not exist "%ADB%" (
    echo ERROR: ADB not found at %ADB%
    echo.
    echo Please update the ADB path in this script to point to your Android SDK location.
    echo Common locations:
    echo   %LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe
    echo   C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools\adb.exe
    echo.
    pause
    exit /b 1
)

echo Using ADB from: %ADB%
echo.
echo Taking screenshots for Play Store submission...
echo.
echo Make sure the E1RM Calculator app is running on your device!
echo.

mkdir screenshots 2>nul

echo 1. Please navigate to the main screen (empty form)
pause
"%ADB%" shell screencap -p /sdcard/screenshot1.png
"%ADB%" pull /sdcard/screenshot1.png screenshots/01-main-screen.png
"%ADB%" shell rm /sdcard/screenshot1.png
echo Screenshot 1 saved!

echo.
echo 2. Please fill in: Weight=225, Reps=5, RPE=8.0
pause
"%ADB%" shell screencap -p /sdcard/screenshot2.png
"%ADB%" pull /sdcard/screenshot2.png screenshots/02-inputs-filled.png
"%ADB%" shell rm /sdcard/screenshot2.png
echo Screenshot 2 saved!

echo.
echo 3. Please click Calculate button to show results
pause
"%ADB%" shell screencap -p /sdcard/screenshot3.png
"%ADB%" pull /sdcard/screenshot3.png screenshots/03-results-displayed.png
"%ADB%" shell rm /sdcard/screenshot3.png
echo Screenshot 3 saved!

echo.
echo 4. Please scroll down to show training percentages
pause
"%ADB%" shell screencap -p /sdcard/screenshot4.png
"%ADB%" pull /sdcard/screenshot4.png screenshots/04-percentages-table.png
"%ADB%" shell rm /sdcard/screenshot4.png
echo Screenshot 4 saved!

echo.
echo 5. Please enter a custom percentage like 79
pause
"%ADB%" shell screencap -p /sdcard/screenshot5.png
"%ADB%" pull /sdcard/screenshot5.png screenshots/05-custom-percentage.png
"%ADB%" shell rm /sdcard/screenshot5.png
echo Screenshot 5 saved!

echo.
echo All screenshots saved to 'screenshots' folder!
echo.
pause
