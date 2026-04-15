# Play Store Screenshot Guide

## How to Take Screenshots

### Option 1: Using the Shell Script (macOS)

1. **Build and install the app** on your device/emulator
2. **Run the app**
3. Run `./take-screenshots.sh` from this directory
4. If multiple devices are connected, select your device when prompted
5. Follow the on-screen prompts for each of the 7 screenshots
6. Screenshots will be saved in the `screenshots/` folder

### Option 2: Using Android Studio

1. **Run the app** on an emulator or connected device
2. Open `View` → `Tool Windows` → `Running Devices`
3. Click the camera icon 📷 to capture screenshots
4. Screenshots are automatically saved

### Option 3: Manual ADB Commands

```bash
# Take a screenshot
adb shell screencap -p /sdcard/screenshot.png

# Pull it to your computer
adb pull /sdcard/screenshot.png ./screenshot.png

# Clean up
adb shell rm /sdcard/screenshot.png
```

---

## Recommended Screenshots for Play Store

Google Play requires at least 2 screenshots. Here are 7 recommended ones:

### 1. Main Screen (Empty)
- Show the app title and empty input fields
- Demonstrates clean, simple interface
- Filename: `01-main-screen.png`

### 2. Inputs Filled
- Weight: 225, Reps: 5, RPE: 8.0
- Shows how to use the calculator
- Filename: `02-inputs-filled.png`

### 3. Results Displayed
- After tapping Calculate
- Shows the estimated 1RM result
- Filename: `03-results.png`

### 4. Training Percentages Table
- Scroll down to show the percentage reference table (60%–95%)
- Filename: `04-percentages.png`

### 5. Custom Percentage Feature
- Enter a custom percentage like 79
- Shows the calculated weight for that percentage
- Filename: `05-custom-percentage.png`

### 6. Sets Planner
- Open via FAB → Sets Planner
- Fill in a top set and at least one additional set, then tap Generate
- Shows the generated sets output
- Filename: `06-sets-planner.png`

### 7. Settings Screen
- Open via FAB → Settings
- Shows units and rounding options
- Filename: `07-settings.png`

---

## Screenshot Requirements

### Google Play Store Requirements:
- **Format:** PNG or JPEG
- **Minimum dimensions:** 320px on shortest side
- **Maximum dimensions:** 3840px on longest side
- **Aspect ratio:** Between 16:9 and 9:16
- **Recommended phone size:** 1080 x 1920 pixels

### Tips for Great Screenshots:
1. Use a clean device with a tidy status bar (full battery, good signal)
2. Take screenshots in portrait mode (app is portrait only)
3. Show real, realistic data
4. Make sure all text is readable

---

## Example Test Data

Use these for consistent, realistic screenshots:

**1RM Calculator — Squat:**
- Weight: 140 kg / 225 lbs
- Reps: 5
- RPE: 8.0

**Sets Planner — Squat session:**
- Top set: 140 kg / 225 lbs, 5 reps, RPE 8
- Set 2: RPE 7.5, 5 reps
- Set 3: % −10, 3 sets, 5 reps

---

## After Taking Screenshots

1. Review all screenshots for quality
2. Crop if needed to remove unwanted elements
3. Confirm they meet the size requirements
4. Upload to Google Play Console under the Phone Screenshots section
5. Add captions if desired (optional but recommended)
