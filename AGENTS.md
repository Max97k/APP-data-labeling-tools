# AGENTS.md — AI Agent Guidance for APP-data-labeling-tools

> **Repository:** `Max97k/APP-data-labeling-tools`  
> **Default Branch:** `main`  
> **Primary Technology Stack:** Kotlin 1.3.72, Android SDK 30, Gradle 7.0.2 / AGP 7.0.0, Google MLKit Object Detection 16.2.3, Firebase Realtime Database & Storage  
> **Visibility:** Public  

---

## 1. Project Overview & Architecture

### 1.1 Purpose & Mission
`APP-data-labeling-tools` is an Android mobile application designed for on-device object labeling and computer vision dataset collection. The app combines Google MLKit's on-device object detection model with interactive bounding box labeling, allowing users to capture images, detect objects in real time, apply multi-class annotation labels (up to 3 labels per bounding box), and synchronize labeled datasets (image assets and structured metadata JSON) directly to Firebase Cloud Storage and Firebase Realtime Database.

### 1.2 System Architecture & Component Diagram
The application follows standard Android Jetpack architecture with ViewBinding:

```
+-------------------------------------------------------------------+
|                        Android Application                        |
+---------------------------------+---------------------------------+
                                  |
            [1. Image Acquisition: Camera / Gallery]
                                  v
            +------------------------------------+
            |          MainActivity.kt           |
            | - Input Image Acquisition          |
            | - MLKit ObjectDetector Inference   |
            | - Bounding Box Coordinate Mapping  |
            +-----------------+------------------+
                              |
                     [Navigate with URI]
                              v
            +------------------------------------+
            |          LabelActivity.kt          |
            | - Custom Canvas / Box Overlay      |
            | - Multi-Tag Annotation Input       |
            | - PostFeature Model Serialization  |
            +--------+------------------+--------+
                     |                  |
   [Upload Image]    |                  |    [Push Metadata JSON]
                     v                  v
+-----------------------------+  +----------------------------------+
|   Firebase Cloud Storage    |  |    Firebase Realtime Database    |
|   (JPEG Image Artifacts)    |  |    (/images, /<box_id> schema)   |
+-----------------------------+  +----------------------------------+
```

### 1.3 Key File & Directory Map
| Path | Purpose / Description |
|---|---|
| `app/src/main/AndroidManifest.xml` | Application manifest declaring camera/storage permissions, file providers, and activity entries. |
| `app/src/main/java/com/google/mlkit/codelab/objectdetection/MainActivity.kt` | Main launcher activity. Handles image capture/selection and initiates Google MLKit object detection. |
| `app/src/main/java/com/google/mlkit/codelab/objectdetection/LabelActivity.kt` | Primary labeling workflow. Displays detected bounding boxes, manages user label inputs, and executes Firebase push tasks. |
| `app/src/main/java/models/PostFeature.kt` | Data model representing object bounding box metadata (`top`, `bottom`, `left`, `right`, `label1`, `label2`, `label3`, `uid`). |
| `app/src/main/res/layout/activity_main.xml` | Main activity layout with camera capture button, gallery selector, and preview surface. |
| `app/src/main/res/layout/activity_label.xml` | Labeling screen layout containing interactive canvas, tag inputs, and submission controls. |
| `app/src/main/res/layout/object_card.xml` | Card layout for displaying individual labeled object items. |
| `build.gradle` | Root project Gradle build script declaring repository configurations and AGP 7.0.0. |
| `app/build.gradle` | App module Gradle build script with Kotlin 1.3.72, Android SDK 30, MLKit 16.2.3, and Firebase BOM 28.4.1. |

---

## 2. Development, Build & Verification Commands

### 2.1 Prerequisites & Environment Setup
- **Java Development Kit (JDK):** JDK 8 or JDK 11 (`JavaVersion.VERSION_1_8`, `jvmTarget = '1.8'`).
- **Android SDK:** Compile SDK `30`, Min SDK `23` (Android 6.0 Marshmallow), Target SDK `30`, Build Tools `30.0.3`.
- **Firebase Configuration:** Valid `google-services.json` placed inside `app/` with active Firebase project bindings.
- **Gradle Wrapper:** Gradle 7.0.2 wrapper (`gradle/wrapper/gradle-wrapper.properties`).

### 2.2 Build & Compilation Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Complete build including verification tasks
./gradlew build
```

*On Windows PowerShell/CMD, use `gradlew.bat assembleDebug`.*

### 2.3 Verification & Testing Suite
```bash
# Run local JVM unit tests
./gradlew test

# Run specific debug unit tests
./gradlew testDebugUnitTest

# Run connected Android instrumentation tests (requires physical device or emulator)
./gradlew connectedAndroidTest

# Run Android Lint static code analysis
./gradlew lint
```

### 2.4 Clean & Reset
```bash
# Clean build outputs and temporary caches
./gradlew clean
```

---

## 3. Coding Standards & Conventions

### 3.1 Code Style & Idioms
- **Language:** Kotlin 1.3.72 following official Kotlin Android style guidelines.
- **ViewBinding:** ViewBinding is enabled (`viewBinding { enabled = true }`). Never use `findViewById` when binding references are available.
- **Coroutines & Asynchronous Handlers:** Ensure all network operations (Firebase Storage and Realtime Database) use non-blocking asynchronous callbacks (`addOnSuccessListener`, `addOnFailureListener`) or coroutine scopes.
- **Null Safety:** Leverage Kotlin's null-safe operators (`?.`, `?:`, `let`) and avoid force-unwrapping (`!!`).

### 3.2 File & Module Organization
- **Package Base:** `com.google.mlkit.codelab.objectdetection` for activities and core logic; `models` for data structures.
- **Model Separation:** Keep data models in `models/` with clear serialization mappings compatible with Firebase Realtime Database and Gson.
- **Layout Naming:** Activity layouts use `activity_<name>.xml`, list/item layouts use `item_<name>.xml` or `<name>_card.xml`.

### 3.3 State Management & Error Handling
- **Image Lifecycle:** Carefully manage image URIs and Bitmaps across activity lifecycle transitions (`onSaveInstanceState`, `onActivityResult` / Activity Result API).
- **Upload Failures:** Always catch and log network/timeout exceptions during Firebase uploads, providing user-visible feedback (e.g., `Toast` or `Snackbar`).
- **Data Integrity:** Bounding box coordinates must be validated as positive integers within the image bounds before database submission.

---

## 4. Safety, Security & Resource Constraints

### 4.1 Secrets & Environment Management
- **Firebase Configuration (`google-services.json`):**
  - Requires `google-services.json` in the `app/` directory.
  - Never commit production Google Services credentials or API keys with unrestricted billing/read-write permissions to public repositories.
  - In CI/CD pipelines, inject `google-services.json` from encrypted secrets before running `./gradlew assembleDebug`.

### 4.2 Resource, Hardware & Performance Constraints
- **Memory Management:** High-resolution camera images can quickly exceed Android heap limits. Always decode Bitmaps with appropriate `inSampleSize` downsampling before feeding them into MLKit or rendering on the Canvas.
- **Permissions:** The app requires `android.permission.INTERNET`, `android.permission.CAMERA`, and `android.permission.WRITE_EXTERNAL_STORAGE` (`maxSdkVersion=28` for Scoped Storage compatibility). Ensure runtime permission requests are properly handled on Android 6.0+ (API 23+).
- **Data Schema:** Metadata pushed to Firebase Realtime Database must adhere to the structured schema:
  ```json
  "<unique_box_id>": {
    "bottom": 480,
    "left": 120,
    "right": 360,
    "top": 200,
    "label1": "car",
    "label2": "vehicle",
    "label3": "sedan",
    "uid": "user_device_id"
  }
  ```

### 4.3 Git & Branch Workflow
- **Default Branch:** `main`. All pull requests and commits target `main`.
- **Commit Formatting:** Follow conventional commit messages (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`).
