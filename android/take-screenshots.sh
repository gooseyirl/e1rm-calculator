#!/bin/zsh
# Script to capture screenshots from connected Android device/emulator
# Make sure your device is connected and the app is running

ADB=$(which adb 2>/dev/null)

if [ -z "$ADB" ]; then
    # Try common Android SDK locations on macOS
    for candidate in \
        "$HOME/Library/Android/sdk/platform-tools/adb" \
        "/usr/local/bin/adb" \
        "/opt/homebrew/bin/adb"; do
        if [ -f "$candidate" ]; then
            ADB="$candidate"
            break
        fi
    done
fi

if [ -z "$ADB" ]; then
    echo "ERROR: adb not found. Make sure Android SDK platform-tools is installed and on your PATH."
    exit 1
fi

echo "Using ADB from: $ADB"
echo ""

# Check a device is connected and pick one if multiple
DEVICES=()
while IFS= read -r line; do
    DEVICES+=("$line")
done < <("$ADB" devices | tail -n +2 | awk '/device$/ {print $1}')

if [ ${#DEVICES[@]} -eq 0 ]; then
    echo "ERROR: No device/emulator connected. Connect your device and enable USB debugging."
    exit 1
elif [ ${#DEVICES[@]} -eq 1 ]; then
    DEVICE="${DEVICES[0]}"
else
    echo "Multiple devices found:"
    for i in "${!DEVICES[@]}"; do
        echo "  $((i+1))) ${DEVICES[$i]}"
    done
    printf "Select device [1-%d]: " "${#DEVICES[@]}"
    read -r choice
    DEVICE="${DEVICES[$((choice-1))]}"
fi

echo "Using device: $DEVICE"
echo ""

mkdir -p screenshots

capture() {
    local num=$1
    local name=$2
    "$ADB" -s "$DEVICE" shell screencap -p /sdcard/screenshot_tmp.png
    "$ADB" -s "$DEVICE" pull /sdcard/screenshot_tmp.png "screenshots/$name.png"
    "$ADB" -s "$DEVICE" shell rm /sdcard/screenshot_tmp.png
    echo "  ✓ Saved screenshots/$name.png"
    echo ""
}

echo "Taking screenshots for Play Store submission..."
echo "Make sure the E1RM Calculator app is running on your device!"
echo ""

echo "1/7 — Navigate to the main screen (empty form), then press Enter"
read -r
capture 1 "01-main-screen"

echo "2/7 — Fill in: Weight=225, Reps=5, RPE=8.0, then press Enter"
read -r
capture 2 "02-inputs-filled"

echo "3/7 — Tap Calculate to show the 1RM result, then press Enter"
read -r
capture 3 "03-results"

echo "4/7 — Scroll down to show the training percentages table, then press Enter"
read -r
capture 4 "04-percentages"

echo "5/7 — Enter a custom percentage (e.g. 79), then press Enter"
read -r
capture 5 "05-custom-percentage"

echo "6/7 — Open the Sets Planner (FAB → Sets Planner), fill in a top set and at least one additional set, tap Generate, then press Enter"
read -r
capture 6 "06-sets-planner"

echo "7/7 — Open Settings (FAB → Settings) to show the settings screen, then press Enter"
read -r
capture 7 "07-settings"

echo "All screenshots saved to the 'screenshots/' folder!"
