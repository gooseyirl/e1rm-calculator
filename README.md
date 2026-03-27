# E1RM Calculator - One Rep Max Calculator for Android

An Android application that calculates your estimated one-rep max (1RM) for weightlifting exercises using the RPE (Rate of Perceived Exertion) method, similar to Barbell Medicine's approach.

## Features

- **RPE-Based Calculations**: Uses Rate of Perceived Exertion (RPE 6.0-10.0) for accurate 1RM estimates
- **Support for 1-10 Reps**: Calculate your 1RM based on any rep range from 1 to 10
- **Training Percentages**: Automatically displays training weights at various percentages (60%-95%)
- **Custom Percentage Calculator**: Enter any percentage to instantly see the corresponding working weight
- **Sets Planner**: Plan your full session — set a top set then build additional sets using RPE targets, percentage reductions/increases, or specific weights. Output is grouped and copyable to clipboard
- **Settings**: Configure units (kg/lbs) and weight rounding (increment: 2.5 or 0.5; direction: default, always up, always down)
- **Clean Material Design UI**: Modern Jetpack Compose interface following Material Design 3

## How It Works

The calculator uses an RPE-based percentage table that maps:
- **Weight lifted**
- **Number of reps performed (1-10)**
- **RPE (Rate of Perceived Exertion)**

To estimate your one-rep max. This method is more accurate than traditional formulas because it accounts for how hard the set felt, not just the weight and reps.

### RPE Scale

- **RPE 10**: Maximum effort - no reps left in the tank
- **RPE 9.5**: Could do 1 more rep, maybe
- **RPE 9**: Could definitely do 1 more rep
- **RPE 8.5**: Could do 1-2 more reps
- **RPE 8**: Could definitely do 2 more reps
- **RPE 7.5**: Could do 2-3 more reps
- **RPE 7**: Could definitely do 3 more reps
- **RPE 6.5**: Could do 3-4 more reps
- **RPE 6**: Could definitely do 4 more reps

### Sets Planner

Access via the floating action button (bottom-right) → Sets Planner.

1. Enter your **top set** (weight, reps, RPE)
2. Add **additional sets** — choose from three types:
   - **RPE**: target a specific RPE and rep count (weight calculated from your 1RM)
   - **%**: reduce or increase by a percentage relative to the previous set
   - **Weight**: specify an exact weight
3. Tap **Generate** to see all planned sets, then **Copy to Clipboard** to share

## Building the App

### Prerequisites

- Android Studio (latest version recommended)
- JDK 8 or higher
- Android SDK with API level 24 or higher

### Steps

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to this directory and select it
4. Wait for Gradle to sync
5. Connect an Android device or start an emulator
6. Click "Run" or press Shift+F10

### Building APK

```bash
./gradlew assembleDebug
```

The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

## Usage

1. Enter the **weight** you lifted
2. Enter the **number of reps** you performed (1-10)
3. Select your **RPE** from the dropdown (how hard it felt)
4. Tap **Calculate 1RM**
5. View your estimated 1RM and training percentages
6. Use the **FAB** (bottom-right) to open the Sets Planner or Settings

## Technical Details

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)

## Formula Reference

The calculator uses Mike Tuchscherer's RPE-to-percentage chart, which provides more accurate estimates than traditional formulas like Epley or Brzycki by incorporating the subjective difficulty of the lift.

## License

This project is open source and available for personal use.
