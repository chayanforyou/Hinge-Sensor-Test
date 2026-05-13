# Android Foldable Detector

A lightweight Android app that detects foldable device capabilities using multiple Android APIs and hardware checks.

This project is useful for developers testing:
- Foldable device support
- Hinge sensors
- Flex mode
- Fold posture
- WindowManager APIs

---

## Features

- Detect foldable devices
- Detect hinge angle sensor availability
- Read real-time hinge angle values
- Detect fold posture using Jetpack WindowManager
- Display all detection results in a simple UI
- Test on Samsung Fold, Pixel Fold, OnePlus Open, and more

---

## Detection Methods

### Hardware Detection
- `Sensor.TYPE_HINGE_ANGLE`

### System Feature Detection
- `PackageManager.FEATURE_SENSOR_HINGE_ANGLE`

### Jetpack WindowManager
- `FoldingFeature`
- Fold state detection
- Fold orientation detection
- Flex mode detection

### Additional Heuristics
- Manufacturer detection
- Device model detection
- Screen ratio detection
- `smallestScreenWidthDp`

---

## Tech Stack

- Kotlin
- Android SDK
- Jetpack WindowManager

---

## Dependency

```gradle
implementation("androidx.window:window:1.3.0")
```

## Purpose

This app was created for experimenting with Android foldable APIs and understanding how different OEMs expose foldable hardware support.
