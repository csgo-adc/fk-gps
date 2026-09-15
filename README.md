# Location Assistant / 定位助手

[English](#english) · [简体中文](#简体中文)

---

## English

### Overview

Location Assistant is an Android learning project for choosing coordinates on a map, saving frequently used locations, and exercising Android's mock-location APIs during development and testing.

The installed Android package is `com.csgoadc.locationassistant`.

### Features

- Choose a point on a Baidu map or enter latitude and longitude manually.
- Validate coordinate input before opening the map.
- Save, rename, reuse, and remove recent locations.
- Switch between standard, satellite, and blank map modes.
- Follow the device language: English is the default and Simplified Chinese is included.

### Requirements

- Android Studio with an Android SDK capable of building API 34.
- A physical Android device running Android 8.0 (API 26) or newer.
- A Baidu Maps API key configured in `app/src/main/AndroidManifest.xml` for map features.

### Build

```bash
./gradlew assembleDebug
```

The debug APK is created under `app/build/outputs/apk/debug/`.

### Testing mock location

1. Enable Developer options on the test device.
2. In **Developer options → Select mock location app**, choose Location Assistant.
3. Allow location and overlay permissions when prompted.
4. Select a point on the map, then choose **Set location**.

### Responsible use

This project is for learning, development, and authorized testing only. Do not use it to misrepresent your location, bypass workplace/school requirements, or violate an application's terms or local law.

---

## 简体中文

### 简介

定位助手是一个 Android 学习项目，用于在地图上选择坐标、保存常用位置，并在开发和测试中使用 Android 模拟定位 API。

安装后的 Android 包名为 `com.csgoadc.locationassistant`。

### 功能

- 在百度地图选点，或手动输入经纬度。
- 打开地图前校验坐标范围。
- 保存、重命名、复用和删除最近的位置。
- 支持普通、卫星和空白地图模式。
- 跟随设备语言：默认英文，并内置简体中文。

### 环境要求

- Android Studio，以及可构建 API 34 的 Android SDK。
- Android 8.0（API 26）及以上的实体测试设备。
- 如需地图功能，请在 `app/src/main/AndroidManifest.xml` 配置有效的百度地图 API Key。

### 构建

```bash
./gradlew assembleDebug
```

调试 APK 会生成在 `app/build/outputs/apk/debug/` 目录中。

### 模拟定位测试

1. 在测试设备上开启开发者选项。
2. 进入“开发者选项 → 选择模拟位置信息应用”，选择定位助手。
3. 按提示授予定位和悬浮窗权限。
4. 在地图上选择地点，然后点击“设置位置”。

### 负责任地使用

本项目仅用于学习、开发和获得授权的测试。请勿用它伪造位置、规避工作或学校要求，或违反应用服务条款及当地法律。

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=csgo-adc/fk-gps&type=Date)](https://star-history.com/#csgo-adc/fk-gps&Date)
