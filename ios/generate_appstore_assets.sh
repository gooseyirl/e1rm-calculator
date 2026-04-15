#!/bin/bash

# App Store Asset Generator for E1RM Calculator
# This script generates all assets needed for App Store submission

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUTPUT_DIR="$SCRIPT_DIR/AppStoreAssets"
SCREENSHOTS_DIR="$OUTPUT_DIR/Screenshots"
ICON_DIR="$OUTPUT_DIR/AppIcon"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  E1RM Calculator - App Store Assets   ${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Create output directories
mkdir -p "$OUTPUT_DIR"
mkdir -p "$SCREENSHOTS_DIR"
mkdir -p "$ICON_DIR"

# ============================================
# STEP 1: Generate App Store Metadata
# ============================================

echo -e "${GREEN}Step 1: Generating App Store Metadata...${NC}"

cat > "$OUTPUT_DIR/metadata.txt" << 'EOF'
================================================================================
E1RM CALCULATOR - APP STORE METADATA
================================================================================

APP NAME (30 characters max):
E1RM Calculator - RPE Based

SUBTITLE (30 characters max):
One Rep Max Estimator

--------------------------------------------------------------------------------
DESCRIPTION (4000 characters max):
--------------------------------------------------------------------------------
E1RM Calculator is your essential tool for estimating your One Rep Max using the scientifically-backed RPE (Rate of Perceived Exertion) method.

Whether you're a powerlifter, bodybuilder, or fitness enthusiast, knowing your estimated 1RM helps you program your training more effectively and track your strength progress over time.

KEY FEATURES:

• RPE-Based Calculations
Uses proven methodology based on Mike Tuchscherer's RPE chart and Barbell Medicine research. Simply enter your weight lifted, reps performed, and RPE level to get an accurate 1RM estimate.

• Training Percentages Table
Instantly see calculated weights at common training percentages (95%, 90%, 85%, 80%, 75%, 70%, 65%, 60%) to help you plan your workout sets.

• Custom Percentage Calculator
Need a specific percentage? Enter any value from 1-100% to calculate the exact weight for your programming needs.

• Privacy First
No account required. No data collection. All calculations happen on your device. Your training data stays yours.

• Clean, Native Design
Built with SwiftUI for a smooth, responsive experience that follows iOS design guidelines.

HOW RPE WORKS:

RPE (Rate of Perceived Exertion) measures how many reps you had "left in the tank" after a set:
- RPE 10: Maximum effort, no reps left
- RPE 9: Could have done 1 more rep
- RPE 8: Could have done 2 more reps
- And so on...

This method accounts for daily performance variations, making it more accurate than traditional percentage-based calculations alone.

PERFECT FOR:
- Strength athletes and powerlifters
- Bodybuilders tracking progressive overload
- Coaches programming for clients
- Anyone wanting to train smarter

Download E1RM Calculator today and take the guesswork out of your strength training!

--------------------------------------------------------------------------------
KEYWORDS (100 characters max, comma-separated):
--------------------------------------------------------------------------------
1rm,one rep max,rpe,calculator,strength,weightlifting,powerlifting,gym,fitness,barbell,training

--------------------------------------------------------------------------------
PROMOTIONAL TEXT (170 characters max - can be updated without new app version):
--------------------------------------------------------------------------------
Calculate your One Rep Max instantly using RPE methodology. Perfect for planning your training cycles and tracking strength progress!

--------------------------------------------------------------------------------
WHAT'S NEW (Release Notes for version 1.0):
--------------------------------------------------------------------------------
Initial release of E1RM Calculator featuring:
• RPE-based one rep max estimation
• Training percentages table
• Custom percentage calculator
• Privacy-focused design

--------------------------------------------------------------------------------
CATEGORY:
--------------------------------------------------------------------------------
Primary: Health & Fitness
Secondary: Utilities

--------------------------------------------------------------------------------
AGE RATING:
--------------------------------------------------------------------------------
4+ (No objectionable content)

--------------------------------------------------------------------------------
COPYRIGHT:
--------------------------------------------------------------------------------
© 2024 [Your Name or Company]

================================================================================
EOF

echo -e "  ✓ Metadata saved to $OUTPUT_DIR/metadata.txt"

# ============================================
# STEP 2: Generate Privacy Policy
# ============================================

echo -e "${GREEN}Step 2: Generating Privacy Policy...${NC}"

cat > "$OUTPUT_DIR/privacy_policy.html" << 'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E1RM Calculator - Privacy Policy</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            line-height: 1.6;
            color: #333;
        }
        h1 { color: #007AFF; }
        h2 { color: #555; margin-top: 30px; }
        .last-updated { color: #888; font-style: italic; }
    </style>
</head>
<body>
    <h1>Privacy Policy for E1RM Calculator</h1>
    <p class="last-updated">Last updated: [DATE]</p>

    <h2>Overview</h2>
    <p>E1RM Calculator ("the App") is designed with your privacy as a priority. This privacy policy explains how we handle information when you use our app.</p>

    <h2>Information We Collect</h2>
    <p><strong>We do not collect any personal information.</strong></p>
    <p>The App performs all calculations locally on your device. We do not:</p>
    <ul>
        <li>Collect or store your workout data</li>
        <li>Require account creation or login</li>
        <li>Track your usage or behavior</li>
        <li>Use analytics or tracking services</li>
        <li>Share any data with third parties</li>
    </ul>

    <h2>Data Storage</h2>
    <p>All data you enter into the App (weights, reps, RPE values) exists only temporarily in your device's memory while the app is running. This data is not saved, stored, or transmitted anywhere. When you close the app, this information is cleared.</p>

    <h2>Third-Party Services</h2>
    <p>The App does not integrate with any third-party services, analytics platforms, or advertising networks.</p>

    <h2>Children's Privacy</h2>
    <p>The App does not collect information from anyone, including children under the age of 13.</p>

    <h2>Changes to This Policy</h2>
    <p>We may update this privacy policy from time to time. Any changes will be posted on this page with an updated revision date.</p>

    <h2>Contact Us</h2>
    <p>If you have questions about this privacy policy, please contact us at:</p>
    <p>[YOUR EMAIL ADDRESS]</p>

    <h2>Your Consent</h2>
    <p>By using the App, you consent to this privacy policy.</p>
</body>
</html>
EOF

# Also create a plain text version
cat > "$OUTPUT_DIR/privacy_policy.txt" << 'EOF'
PRIVACY POLICY FOR E1RM CALCULATOR
==================================
Last updated: [DATE]

OVERVIEW
--------
E1RM Calculator ("the App") is designed with your privacy as a priority. This privacy policy explains how we handle information when you use our app.

INFORMATION WE COLLECT
----------------------
We do not collect any personal information.

The App performs all calculations locally on your device. We do not:
- Collect or store your workout data
- Require account creation or login
- Track your usage or behavior
- Use analytics or tracking services
- Share any data with third parties

DATA STORAGE
------------
All data you enter into the App (weights, reps, RPE values) exists only temporarily in your device's memory while the app is running. This data is not saved, stored, or transmitted anywhere. When you close the app, this information is cleared.

THIRD-PARTY SERVICES
--------------------
The App does not integrate with any third-party services, analytics platforms, or advertising networks.

CHILDREN'S PRIVACY
------------------
The App does not collect information from anyone, including children under the age of 13.

CHANGES TO THIS POLICY
----------------------
We may update this privacy policy from time to time. Any changes will be posted on this page with an updated revision date.

CONTACT US
----------
If you have questions about this privacy policy, please contact us at:
[YOUR EMAIL ADDRESS]

YOUR CONSENT
------------
By using the App, you consent to this privacy policy.
EOF

echo -e "  ✓ Privacy policy saved to $OUTPUT_DIR/privacy_policy.html"
echo -e "  ✓ Privacy policy (text) saved to $OUTPUT_DIR/privacy_policy.txt"
echo -e "  ${YELLOW}⚠ Remember to host this file and add the URL to App Store Connect${NC}"

# ============================================
# STEP 3: Generate App Icon (from Android app)
# ============================================

echo -e "${GREEN}Step 3: Generating App Icon...${NC}"

# Path to Android app icon (largest available: xxxhdpi = 192x192)
ANDROID_ICON="$SCRIPT_DIR/../e1rm-calculator/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"

if [ -f "$ANDROID_ICON" ]; then
    echo -e "  Using icon from Android app..."

    # Copy the source icon
    cp "$ANDROID_ICON" "$ICON_DIR/ic_launcher_source.png"

    # Generate icons at required sizes using sips (built into macOS)
    # Note: Scaling up from 192px will cause some quality loss

    declare -a ICON_SIZES=("1024" "180" "167" "152" "120" "76")

    for size in "${ICON_SIZES[@]}"; do
        cp "$ANDROID_ICON" "$ICON_DIR/AppIcon-${size}.png"
        sips -z "$size" "$size" "$ICON_DIR/AppIcon-${size}.png" --out "$ICON_DIR/AppIcon-${size}.png" > /dev/null 2>&1
        echo -e "  ✓ Generated AppIcon-${size}.png (${size}x${size})"
    done

    echo -e "\n  ${YELLOW}⚠ Note: Icon scaled from 192x192. For best quality, provide a 1024x1024 source.${NC}"
    echo -e "  App icons saved to $ICON_DIR/"

    # Copy the 1024 icon to the asset catalog
    if [ -f "$ICON_DIR/AppIcon-1024.png" ]; then
        cp "$ICON_DIR/AppIcon-1024.png" "$SCRIPT_DIR/E1RMCalculator/Assets.xcassets/AppIcon.appiconset/AppIcon-1024.png"

        # Update the Contents.json for the app icon
        cat > "$SCRIPT_DIR/E1RMCalculator/Assets.xcassets/AppIcon.appiconset/Contents.json" << 'EOF'
{
  "images" : [
    {
      "filename" : "AppIcon-1024.png",
      "idiom" : "universal",
      "platform" : "ios",
      "size" : "1024x1024"
    }
  ],
  "info" : {
    "author" : "xcode",
    "version" : 1
  }
}
EOF
        echo -e "  ✓ Updated Xcode asset catalog with new app icon"
    fi
else
    echo -e "  ${RED}✗ Android icon not found at: $ANDROID_ICON${NC}"
    echo -e "  ${YELLOW}  Expected path: ../e1rm-calculator/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png${NC}"
    echo -e "  ${YELLOW}  Please copy your app icon manually to: $ICON_DIR/AppIcon-1024.png${NC}"
fi

# ============================================
# STEP 4: Capture Screenshots
# ============================================

echo -e "${GREEN}Step 4: Preparing Screenshot Capture...${NC}"

# Create screenshot capture script
cat > "$OUTPUT_DIR/capture_screenshots.sh" << 'SCREENSHOT_SCRIPT'
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
SCREENSHOT_SCRIPT

chmod +x "$OUTPUT_DIR/capture_screenshots.sh"
echo -e "  ✓ Screenshot capture script created: $OUTPUT_DIR/capture_screenshots.sh"

# ============================================
# STEP 5: Create submission checklist
# ============================================

echo -e "${GREEN}Step 5: Creating Submission Checklist...${NC}"

cat > "$OUTPUT_DIR/SUBMISSION_CHECKLIST.md" << 'EOF'
# App Store Submission Checklist for E1RM Calculator

## Before Submission

### Assets Prepared
- [ ] App Icon (1024x1024) - Generated in `AppIcon/`
- [ ] Screenshots for iPhone 6.7" (1290 x 2796)
- [ ] Screenshots for iPhone 6.5" (1284 x 2778)
- [ ] Screenshots for iPhone 5.5" (1242 x 2208)

### Metadata Ready
- [ ] App Name (30 chars max)
- [ ] Subtitle (30 chars max)
- [ ] Description (4000 chars max)
- [ ] Keywords (100 chars max)
- [ ] Promotional Text (170 chars max)
- [ ] What's New / Release Notes
- [ ] Support URL
- [ ] Privacy Policy URL (hosted online)
- [ ] Copyright text

### App Store Connect Setup
- [ ] Create new app in App Store Connect
- [ ] Set Bundle ID: `com.e1rm.calculator`
- [ ] Select primary category: Health & Fitness
- [ ] Select secondary category: Utilities
- [ ] Set age rating: 4+
- [ ] Configure pricing (Free)

### Privacy & Legal
- [ ] Host privacy policy HTML file online
- [ ] Update privacy policy with your contact email
- [ ] Update privacy policy with current date
- [ ] Fill in App Privacy questionnaire in App Store Connect
  - Select "Data Not Collected"

### Xcode Preparation
- [ ] Set version number (1.0)
- [ ] Set build number (1)
- [ ] Verify Bundle Identifier matches App Store Connect
- [ ] Select "Any iOS Device" as build target
- [ ] Archive the app (Product > Archive)
- [ ] Validate the archive
- [ ] Upload to App Store Connect

### Final Review
- [ ] Test app on real device
- [ ] Verify all features work correctly
- [ ] Check app launches without issues
- [ ] Review all screenshots are accurate
- [ ] Proofread all text content

## Screenshot Guidelines

App Store requires these screenshot sizes for iPhone:

| Display Size | Resolution | Required |
|-------------|------------|----------|
| 6.7" | 1290 x 2796 | Yes |
| 6.5" | 1284 x 2778 | Yes (or 6.7") |
| 5.5" | 1242 x 2208 | Yes |

### Recommended Screenshots:
1. **Input Screen** - Clean UI showing weight/reps/RPE inputs
2. **Results View** - Calculated 1RM prominently displayed
3. **Training Percentages** - The percentage table feature
4. **Custom Calculator** - Custom percentage feature
5. **RPE Info** - Educational content about RPE

## Quick Commands

```bash
# Run screenshot capture
./AppStoreAssets/capture_screenshots.sh

# Archive app in Xcode
xcodebuild -project E1RMCalculator.xcodeproj -scheme E1RMCalculator -archivePath build/E1RMCalculator.xcarchive archive

# Validate archive
xcrun altool --validate-app -f build/E1RMCalculator.ipa -t ios
```

## Useful Links

- [App Store Connect](https://appstoreconnect.apple.com)
- [App Store Review Guidelines](https://developer.apple.com/app-store/review/guidelines/)
- [Screenshot Specifications](https://developer.apple.com/help/app-store-connect/reference/screenshot-specifications)
- [Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/)
EOF

echo -e "  ✓ Submission checklist created: $OUTPUT_DIR/SUBMISSION_CHECKLIST.md"

# ============================================
# Summary
# ============================================

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}           Asset Generation Complete    ${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo -e "Generated files in ${GREEN}$OUTPUT_DIR/${NC}:"
echo ""
echo "  📄 metadata.txt           - App Store text content"
echo "  📄 privacy_policy.html    - Privacy policy (host this online)"
echo "  📄 privacy_policy.txt     - Privacy policy (plain text)"
echo "  🖼️  AppIcon/               - App icons (all sizes)"
echo "  📸 capture_screenshots.sh - Run this to capture screenshots"
echo "  ✅ SUBMISSION_CHECKLIST.md - Step-by-step submission guide"
echo ""
echo -e "${YELLOW}Next Steps:${NC}"
echo "1. Review and customize metadata.txt"
echo "2. Host privacy_policy.html on your GitHub repo or website"
echo "3. Run ./AppStoreAssets/capture_screenshots.sh with simulator"
echo "4. Follow SUBMISSION_CHECKLIST.md for App Store submission"
echo ""
echo -e "${GREEN}The app icon has been added to your Xcode project automatically!${NC}"
