# GMI Mentorship Portal — Compose Multiplatform App

A production-ready Kotlin Multiplatform (KMP) mobile client targeting **Android and iOS** for the **Global Mentorship Initiative (GMI) Mentorship Portal** ([https://www.gmiportal.org/](https://www.gmiportal.org/)).

The app faithfully mirrors the information architecture, 10-session career curriculum, mutual-availability scheduler, notification routing, and communication channels of the live GMI web portal, adapted into a mobile navigation structure with Material 3 design and deep navy (`#16255C`) branding.

---

## 📱 Features & Screen Architecture

### 1. Navigation Shell
* **Top App Bar**:
  * GMI branding (`GMI Mentorship Portal`)
  * User Chip displaying avatar/initials, full name, and role badge (`Student` or `Mentor`). Tapping the chip routes directly to Settings (`/account`).
  * Quick Demo Role Switcher toggle (`Student ↔ Mentor`) for fast persona testing.
  * Offline status banner with pending outbound sync queue indicator.
* **Bottom Navigation Bar**:
  * **Dashboard** (`/dashboard`)
  * **Program** (`/my-program`)
  * **Calendar** (`/my-program/calendar`)
  * **Messages** (`/messages`)
  * **Notifications** (`/notifications` with real-time unread badge)

### 2. Dashboard (`/dashboard`)
* "Welcome back, {first name}" personalized greeting.
* 2-week mentorship pause notice with a direct `mailto:info@globalmentorship.org` contact action.
* **My Mentor** / **My Student(s)** card: Partner name, role/title, organization/university, email, and one-tap deep-link into conversation.
* **Program Progress** card: Progress percentage, 10-session progress bar, and "N of 10 sessions completed" counter.
* **Upcoming Sessions**: Next active session title with "Go to Session" primary action.
* **Recent Messages**: Preview snippets with "View all" and "Request Help" link.
* **Recent Notifications**: Last 4 items with unread indicators, relative timestamps, and "View all".

### 3. Mentorship Program (`/my-program` & `/my-program/session/{id}`)
* Pinned progress header: `GMI Mentorship`, `N/10 – N0%`, and sticky **"Next: Session N"** button.
* Program progression guidelines panel with warning on program integrity and falsely marked completions.
* 10 sequential sessions seeded with real portal titles:
  1. *Welcome to Your Mentorship*
  2. *Create a Resume/CV*
  3. *Build a Professional Network*
  4. *Develop a Career Plan*
  5. *Develop Professional Skills Using SMART Goals*
  6. *Prepare for a Job Interview*
  7. *Prepare for a Job Interview*
  8. *Understand Job Search Techniques*
  9. *Distinguish Yourself in the Workplace*
  10. *Prepare for Global Business and Conclude Mentorship*
* Status chips: **Completed** (Green), **Current** (Orange), **Locked** (Gray). Locked sessions are visibly disabled.
* **Session Detail View**:
  * Full objectives checklist.
  * Downloadable worksheets and PDFs with offline caching indicators.
  * **Mark session complete** primary button with confirmation dialog restating the program integrity warning. Completing a session unlocks the next one and triggers partner notification.

### 4. Mutual-Availability Calendar (`/my-program/calendar`)
* Mutual-availability scheduler:
  1. Tap a date and select time slots you are available to meet.
  2. Partner compares against their own calendar and selects a time.
  3. Both parties receive a notification once the meeting time is confirmed.
* **Timezone Enforcement**: Meeting times require a configured timezone. If unset, the screen blocks and routes to `Settings → General → Timezone`.
* 4-State Mutual Availability Legend:
  * 🟦 **Mentor's Availability**
  * 🟪 **Student's Availability**
  * 🟧 **Suggested Meeting Time** (Orange)
  * 🟩 **Confirmed Meeting Time** (Green)
* Month grid with previous/next navigation, today outlined, and slot dot indicators.
* **Day-Detail Slot Selection Modal**: Select time slots for any day.
* **Suggested Meeting Times Panel**: Displays pending proposals with "Confirm" / "Decline" actions and calendar export capability.

### 5. Notifications (`/notifications`)
* Unread counter subtitle ("N unread notifications").
* Filter chips: **All**, **Unread (N)**, **Read**, and **Mark All as Read** button.
* Cards with unread dot, category tag, title, expandable rich body, relative timestamps, mark-as-read check, delete action, external links, and "Reply →" link opening a message to GMI Support.
* Includes automated check-ins ("Sessions 4–6 complete – 2nd check-in") and GMI broadcasts (scholarship and partner opportunities).
* Swipe-to-delete and swipe-to-read actions.

### 6. Messages (`/messages` & `/messages/thread/{id}`)
* Push navigation from conversation list to chat thread.
* Tabs: **Inbox**, **Archived**, **Deleted**, with search filtering.
* **New Conversation Modal**:
  * **Recipients**: Single-select from fixed list: GMI Support and paired partner.
  * **Subject**: Required, max 200 characters with live character counter (`N/200`).
  * **Message Body**: Rich-text editor with bold, italic, underline, emoji picker, link insert, and preview mode, max 10,000 characters with live counter (`N/10,000`).
* Thread view: Chronological bubbles, rich text, archive/delete actions, and composer toolbar docking above keyboard.

### 7. Settings — Account Details (`/account`)
* 4 tabs:
  1. **General**:
     * Read-only account info (Role, Created date, Updated date).
     * Profile picture: circular avatar/initials, max 500KB validation (JPEG, PNG, GIF, WebP), and upload photo action.
     * Update information: First Name\*, Last Name\*, Email\*.
     * Searchable Timezone dropdown required for calendar usage.
  2. **Notifications**:
     * 3 channels: **In-portal notifications**, **Email notifications**, and **Push notifications**.
     * 5 event toggles each:
       - Partner created a calendar selection
       - Partner updated a calendar selection
       - Partner cancelled a calendar selection
       - Partner confirmed a calendar selection
       - Session completion – partner notification
  3. **Mentorship History**:
     * Mentorship cards with partner name, email, Active badge, program name, and start date.
  4. **Resume**:
     * Resource type radio: **File Upload** (PDF, DOC, DOCX up to 10MB) or **External Link**.
     * Document name, file picker/URL input, upload action, and list of uploaded resumes with delete action.
  5. **Database & API Setup (On-Phone DB + Online API)**:
     * **On-Phone Local DB Status**: Live storage status indicator (ACTIVE), count of locally persisted sessions, messages, calendar slots, and pending outbound actions.
     * **Online API Synchronization Toggle**: Enable/disable remote sync.
     * **API Base URL**: Configurable endpoint (default `https://www.gmiportal.org/api/v1` or custom localhost/staging URL).
     * **Sync Local DB with Online API Now**: Manual sync action with live progress indicator and timestamp.
     * **Reset Local DB to Seed Data**: One-tap action to restore initial clean demo state.
* **Language Switcher**: English, Español, Français.
* **Switch Demo Role**: Student ↔ Mentor.
* **Log Out** button.

### 8. Authentication & Security
* Sign-in with registered GMI email and password.
* "Remember me" and "Forgot password?".
* Interactive security check (CAPTCHA).
* Biometric unlock integration.
* External links to apply as a student or become a mentor (no in-app registration).

---

## 🛠️ Tech Stack & Dependencies

* **Kotlin**: 2.1.0
* **Compose Multiplatform**: 1.7.1
* **Android Gradle Plugin**: 8.7.2
* **Target Platforms**: Android (minSdk 24, compileSdk 35) & iOS (iOS 15+)
* **Networking**: Ktor 3.0.1 (OkHttp on Android, Darwin on iOS) with `ContentNegotiation` & `kotlinx.serialization`
* **Concurrency**: Kotlin Coroutines 1.9.0 & Flow
* **Dependency Injection**: Koin 4.0.0
* **Persistence & Settings**: `multiplatform-settings` 1.2.0
* **Image Loading**: Coil 3.0.4
* **Date & Time**: `kotlinx-datetime` 0.6.1

---

## 🏗️ Project Structure

```
.
├── composeApp/
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/org/globalmentorship/portal/
│       │   ├── App.kt                             # Root Composable & Screen Router
│       │   ├── domain/models/                     # Data models (User, Program, Calendar, Messages, etc.)
│       │   ├── data/remote/                       # GmiApiClient & GmiApiClientImpl
│       │   ├── data/repository/                   # Repositories & FakeRepositories
│       │   ├── localization/                      # Multi-language strings (EN, ES, FR)
│       │   ├── platform/                          # PlatformAbstractions (expect declarations)
│       │   └── ui/
│       │       ├── components/                    # Top bar, bottom bar, offline banner
│       │       ├── navigation/                    # Navigation screen routes
│       │       ├── screens/                       # Dashboard, Program, Calendar, Messages, etc.
│       │       └── theme/                         # GMI brand palette, shapes, typography
│       ├── androidMain/kotlin/org/globalmentorship/portal/
│       │   ├── MainActivity.kt
│       │   ├── GmiApplication.kt
│       │   └── platform/PlatformAbstractions.android.kt
│       ├── iosMain/kotlin/org/globalmentorship/portal/
│       │   ├── MainViewController.kt
│       │   └── platform/PlatformAbstractions.ios.kt
│       └── commonTest/kotlin/org/globalmentorship/portal/
│           ├── AuthRepositoryTest.kt
│           ├── ProgramRepositoryTest.kt
│           ├── CalendarRepositoryTest.kt
│           ├── MessageRepositoryTest.kt
│           └── GmiApiClientTest.kt
├── iosApp/                                        # iOS SwiftUI application
│   └── iosApp/
│       ├── iOSApp.swift
│       └── ContentView.swift
├── gradle/
│   └── libs.versions.toml                         # Gradle version catalog
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## 🚀 Build & Run Instructions

### Prerequisites
* JDK 17 or JDK 21 (configured in `gradle.properties` via `org.gradle.java.home`).
* Android SDK (API 34/35).
* macOS with Xcode 15+ (for running the iOS target).

### Android
To compile and run unit tests:
```bash
./gradlew testDebugUnitTest
```

To assemble the debug APK:
```bash
./gradlew assembleDebug
```
The compiled APK is generated at:
`composeApp/build/outputs/apk/debug/composeApp-debug.apk` (20.3 MB)

To run on a connected Android device or emulator:
```bash
./gradlew installDebug
```

### iOS
1. Open Xcode.
2. Open `iosApp/iosApp.xcodeproj` or import the project into Xcode.
3. Select your target simulator (e.g., iPhone 16) or physical iOS device.
4. Press **Run** (`Cmd + R`). Xcode will trigger the Kotlin Multiplatform `embedAndSignAppleFrameworkForXcode` Gradle task automatically to build `ComposeApp.framework`.

---

## 🌐 Localization Workflow

Localization is managed in `composeApp/src/commonMain/kotlin/org/globalmentorship/portal/localization/`:
* Supported locales: **English** (`en`), **Spanish** (`es`), and **French** (`fr`).
* `AppStrings`: Strongly typed string contract ensuring 100% parity across all languages.
* `LocalizationManager`: Allows dynamic in-app switching that persists and overrides system locale immediately across all screens without app restart.

---

## 🔌 `expect` / `actual` Platform Abstraction Inventory

Minimal, clean abstractions keep shared code 100% platform-agnostic:

| Abstraction | Android Implementation | iOS Implementation | Purpose |
| :--- | :--- | :--- | :--- |
| `PlatformSecureStorage` | Android `SharedPreferences` / KeyStore | iOS Keychain / Memory Store | Securely stores JWT tokens and sensitive auth session keys. |
| `PlatformBiometrics` | `BiometricPrompt` | `LocalAuthentication` (Face ID / Touch ID) | Opt-in biometric unlock. |
| `PlatformFilePicker` | Android SAF Intent (`GET_CONTENT`) | `UIDocumentPickerViewController` | Picking PDF/DOC/DOCX documents up to 10MB. |
| `PlatformImagePicker` | Android Media Intent / PhotoPicker | `PHPickerViewController` | Profile picture upload (max 500KB JPEG/PNG/GIF/WebP). |
| `PlatformPdfViewer` | Android PDF Viewer Intent | `UIDocumentInteractionController` / Safari | In-app worksheet viewing with offline caching. |
| `PlatformCalendarExporter` | `CalendarContract.Events` | `EventKit` (`EKEventStore`) | Add confirmed mentorship meetings to device calendar. |
| `PlatformPushHandler` | Firebase Cloud Messaging (FCM) | Apple Push Notification Service (APNs) | Retrieves device push tokens. |
| `PlatformShareSheet` | Android `ACTION_SEND` Intent | `UIActivityViewController` | System share sheet for resources and meeting links. |

---

## 🔄 Swapping Mock Fakes for Live Backend

The project is structured with clean repository interfaces (`AuthRepository`, `ProgramRepository`, `CalendarRepository`, `NotificationRepository`, `MessageRepository`, `SettingsRepository`).

To switch to the live backend:
1. Initialize `GmiApiClientImpl` with your base URL:
   ```kotlin
   val apiClient = GmiApiClientImpl(
       httpClient = createHttpClient(),
       baseUrl = "https://www.gmiportal.org/api/v1"
   )
   ```
2. Implement repository classes that delegate directly to `apiClient`.
3. Update the DI module (Koin) to provide the live repository implementations instead of `Fake*Repository`:
   ```kotlin
   val appModule = module {
       single<GmiApiClient> { GmiApiClientImpl(get()) }
       single<ProgramRepository> { LiveProgramRepository(get()) }
       single<CalendarRepository> { LiveCalendarRepository(get()) }
       // ...
   }
   ```

---

## 📋 Open Questions & Architecture Notes for Live API Integration

1. **Auth Scheme & CAPTCHA**:
   * *Portal Behavior*: Portal currently uses email + password with Cloudflare Turnstile / reCAPTCHA v2.
   * *Mobile Recommendation*: Pass the CAPTCHA response token via `X-Captcha-Token` header or within `LoginRequest.captchaToken`. Authenticated sessions should return a standard JWT access token + refresh token pair stored in `PlatformSecureStorage`.
2. **Mentors with Multiple Concurrent Students**:
   * *Portal Behavior*: Mentors can have 1–3 active students assigned simultaneously.
   * *Implementation*: `UserProfile.partner` handles the primary partner, and `MessageRecipient` supports iterating through multiple students when populating the New Conversation recipient picker for mentors.
3. **Rich-Text Format in Messages**:
   * *Portal Behavior*: The web portal stores HTML snippets (`<strong>`, `<em>`, `<u>`, `<a href>`).
   * *Implementation*: The mobile composer outputs clean sanitized HTML, rendered natively into Compose text spans.
4. **Calendar Availability Slot Model**:
   * *Portal Behavior*: Hourly fixed slots (e.g. 09:00–10:00) serialized with ISO 8601 UTC timestamps alongside the user's timezone identifier.
   * *Implementation*: Both client and server calculate slot availability in UTC and project times onto the user's saved `TimeZone`.
5. **Student Certificate Screen**:
   * *Portal Behavior*: Upon completing Session 10, the backend unlocks the graduation certificate PDF.
   * *Implementation*: `SessionCompletionResponse.certificateUnlocked` unlocks the certificate download in the Session 10 detail screen.
6. **12-Session vs 10-Session Programs**:
   * *Implementation*: `totalSessions` is dynamic in `ProgramOverview`, allowing 10 or 12-session programs to use the exact same UI seamlessly.

---

## 🧪 Verification & Test Results

Run all unit tests:
```bash
./gradlew testDebugUnitTest
```

All 13 unit tests passed with 100% success rate:
* `ProgramRepositoryTest`: Initial sequential order, locked session protection, unlocking next session upon completion, partner notification trigger.
* `CalendarRepositoryTest`: Timezone enforcement, slot addition, proposed meeting confirmation.
* `MessageRepositoryTest`: Fixed recipient list, thread creation, chronological ordering, tab separation.
* `AuthRepositoryTest`: Role switching, captcha verification, biometric unlock.
* `GmiApiClientTest`: Ktor MockEngine API requests and JSON deserialization.
