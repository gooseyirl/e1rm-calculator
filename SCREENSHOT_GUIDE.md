# Play Store Screenshot Guide

## How to Take Screenshots

### Option 1: Using Android Studio (Easiest)

1. **Run the app** on an emulator or connected device
2. Open `View` → `Tool Windows` → `Running Devices`
3. Click the camera icon 📷 to capture screenshots
4. Screenshots are automatically saved

### Option 2: Using the Batch Script

1. **Build and install the app** on your device/emulator
2. **Run the app**
3. Run `take-screenshots.bat` from this directory
4. Follow the prompts to capture each screenshot
5. Screenshots will be saved in the `screenshots/` folder

### Option 3: Manual ADB Commands

```bash
# Take a screenshot
adb shell screencap -p /sdcard/screenshot.png

# Pull it to your computer
adb pull /sdcard/screenshot.png ./screenshot.png

# Clean up
adb shell rm /sdcard/screenshot.png
```

## Recommended Screenshots for Play Store

Google Play requires at least 2 screenshots. Here are 5 recommended ones:

### 1. Main Screen (Empty)
- Show the app title and empty input fields
- Demonstrates clean, simple interface
- Filename: `01-main-screen.png`

### 2. Inputs Filled
- Weight: 225 lbs
- Reps: 5
- RPE: 8.0
- Shows how to use the app
- Filename: `02-inputs-filled.png`

### 3. Results Displayed
- After clicking Calculate
- Shows the estimated 1RM (around 277 lbs)
- Filename: `03-results-displayed.png`

### 4. Training Percentages Table
- Scroll down to show the percentage reference table
- Shows 95%, 90%, 85%, 80%, 75%, 70%, 65%, 60%
- Filename: `04-percentages-table.png`

### 5. Custom Percentage Feature
- Enter custom percentage like 79%
- Shows the calculated weight
- Filename: `05-custom-percentage.png`

## Screenshot Requirements

### Google Play Store Requirements:
- **Format:** PNG or JPEG
- **Minimum dimensions:** 320px
- **Maximum dimensions:** 3840px
- **Aspect ratio:** Between 16:9 and 9:16
- **Recommended phone size:** 1080 x 1920 pixels (standard phone resolution)

### Tips for Great Screenshots:
1. Use a clean device with good status bar (full battery, good signal)
2. Take screenshots in portrait mode (app is locked to portrait)
3. Ensure good lighting/contrast
4. Show real, useful data
5. Make sure all text is readable

## Example Test Data

Use these examples for consistent, realistic screenshots:

**Example 1 - Bench Press:**
- Weight: 225 lbs
- Reps: 5
- RPE: 8.0
- Result: ~277 lbs 1RM

**Example 2 - Squat:**
- Weight: 315 lbs
- Reps: 3
- RPE: 9.0
- Result: ~343 lbs 1RM

**Example 3 - Deadlift:**
- Weight: 405 lbs
- Reps: 1
- RPE: 10.0
- Result: 405 lbs 1RM

## After Taking Screenshots

1. Review all screenshots for quality
2. Crop if needed to remove any unwanted elements
3. Ensure they meet size requirements
4. Upload to Google Play Console
5. Add captions if desired (optional but recommended)
