#!/bin/bash

# Screenshot Capture Script for E1RM Calculator
# Run this after the app is installed and running in the simulator

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCREENSHOTS_DIR="$SCRIPT_DIR/Screenshots"

mkdir -p "$SCREENSHOTS_DIR"

echo "Screenshot Capture for E1RM Calculator"
echo "======================================="
echo ""

# iPhone screenshot sizes needed for App Store:
# - 6.7" (iPhone 15 Pro Max): 1290 x 2796
# - 6.5" (iPhone 11 Pro Max): 1284 x 2778
# - 5.5" (iPhone 8 Plus): 1242 x 2208

# Simulator device types for screenshots
declare -A DEVICES
DEVICES["iPhone-6.7"]="iPhone 15 Pro Max"
DEVICES["iPhone-6.5"]="iPhone 14 Plus"
DEVICES["iPhone-5.5"]="iPhone 8 Plus"

echo "Available simulators:"
xcrun simctl list devices available | grep -E "iPhone (8 Plus|14 Plus|15 Pro Max)" | head -10
echo ""

# Function to capture screenshot
capture_screenshot() {
    local device_name="$1"
    local screenshot_name="$2"
    local output_file="$SCREENSHOTS_DIR/${device_name}_${screenshot_name}.png"

    xcrun simctl io booted screenshot "$output_file"
    echo "  ✓ Captured: $output_file"
}

echo "Instructions:"
echo "1. Open Xcode and run the app in a simulator"
echo "2. The script will capture screenshots of the current state"
echo ""

read -p "Is the app running in a simulator? (y/n): " response
if [[ "$response" != "y" ]]; then
    echo "Please start the app in a simulator first."
    exit 1
fi

# Get the current booted device
BOOTED_DEVICE=$(xcrun simctl list devices booted | grep -oE "iPhone [^(]+" | head -1 | xargs)
echo "Current device: $BOOTED_DEVICE"
echo ""

# Screenshot workflow
echo "Screenshot Capture Workflow:"
echo "============================"
echo ""
echo "We'll capture 4 screenshots showing the app's features."
echo "Follow the prompts to set up each screenshot."
echo ""

# Screenshot 1: Empty state / Clean UI
echo "Screenshot 1: Clean Input Screen"
echo "---------------------------------"
echo "Show the app with empty inputs (initial state)"
read -p "Press Enter when ready to capture..."
capture_screenshot "$BOOTED_DEVICE" "01_input_screen"

# Screenshot 2: Filled inputs
echo ""
echo "Screenshot 2: Inputs Filled"
echo "---------------------------"
echo "Enter some values: Weight=225, Reps=5, RPE=8"
read -p "Press Enter when ready to capture..."
capture_screenshot "$BOOTED_DEVICE" "02_inputs_filled"

# Screenshot 3: Results shown
echo ""
echo "Screenshot 3: Results Display"
echo "-----------------------------"
echo "Tap Calculate to show the results"
read -p "Press Enter when ready to capture..."
capture_screenshot "$BOOTED_DEVICE" "03_results"

# Screenshot 4: Training percentages
echo ""
echo "Screenshot 4: Training Percentages"
echo "-----------------------------------"
echo "Scroll down to show the training percentages table"
read -p "Press Enter when ready to capture..."
capture_screenshot "$BOOTED_DEVICE" "04_percentages"

# Screenshot 5 (optional): Custom percentage
echo ""
echo "Screenshot 5: Custom Percentage (Optional)"
echo "-------------------------------------------"
echo "Scroll to show custom percentage calculator"
read -p "Press Enter when ready to capture (or 's' to skip)..."
if [[ "$REPLY" != "s" ]]; then
    capture_screenshot "$BOOTED_DEVICE" "05_custom_percentage"
fi

echo ""
echo "======================================="
echo "Screenshot capture complete!"
echo "Screenshots saved to: $SCREENSHOTS_DIR"
echo ""
echo "Next steps:"
echo "1. Review screenshots and retake if needed"
echo "2. For App Store, you may need multiple device sizes"
echo "3. Consider adding device frames using tools like:"
echo "   - https://screenshots.pro"
echo "   - https://mockuphone.com"
echo "   - Fastlane's frameit"
