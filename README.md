# NearMate

NearMate is a native Android app built to help students and newcomers discover and chat with nearby people. Users can set an availability status (e.g., “Open to Chat,” “Looking for Study Buddy”), see who’s around on a map or list, and start one‑to‑one conversations.

- Course: CS2063 – Introduction to Mobile Application Development (UNB), Fall 2025
- Team: Edung Divinefavour, Eleanya Ude David, Jonathan Eddie

## Features

- User registration & login
  - Email/password auth with Firebase Authentication.
  - Onboarding flow for collecting basic user details.
- Profiles
  - Editable profile: first name, last name, username, status, presence, photo
  - Option to display real name or username publicly
  - Profile photo from camera or gallery (Firebase Storage)
- Nearby users
  - Discover people within a radius via Google Play Services Location
  - Uses a **map view** and **list view** to display nearby users
  - Distance and country information via utility helpers.
- Real‑time chat
  - One‑to‑one chat rooms backed by Firebase Cloud Firestore
  - Chat list with last message and timestamp
  - In‑chat bubbles with sent/received timestamps
- Presence & status
  - Toggle online/offline and select a short status from the Profile screen
- Privacy
  - Visibility controls and conservative defaults for location usage

## Screens & navigation

- Splash → Landing → Login / Register / Forgot Password → User Details (onboarding)
- Home (main navigation)
  - Map (nearby users), Users (list), Chats (chat list), Profile
- Chat Room (one‑to‑one conversation)

Activities visible in the app:
- `SplashActivity` (launcher), `LandingActivity`, `LoginActivity`, `RegisterActivity`, `ForgotPasswordActivity`,
- `UserDetailsActivity`, `HomeActivity`, `ChatRoomActivity`

## Tech stack

- Language: Kotlin
- Min SDK: 28 | Target/Compile SDK: 36
- UI: Activities + Fragments, ViewBinding
- Architecture: Activity + Fragment based UI with service classes

Key Gradle dependencies (see [app/build.gradle](app/build.gradle)):
- AndroidX Core/AppCompat/ConstraintLayout/Activity, Material
- Firebase Auth, Firestore, Storage
- Google Play Services: Location, Maps
- Glide, ViewPager2

## Project structure (high level)

```
app/
  src/main/java/ca/unb/mobiledev/nearmate/
    features/
      splash/
      landing/
      login/
      register/
      forgotpassword/
      userdetails/
      home/           # hosts map, users list, chats, profile
      chatroom/
    models/           # User, ChatMessage, ChatRoom, etc.
    services/         # UserService, ChatService, LocationService, ...
    utils/            # Distance utils, image helpers, etc.
  src/main/AndroidManifest.xml
```

## Getting started

### Prerequisites
- Android Studio (Giraffe or newer recommended)
- A Firebase project configured for:
  - Email/Password Authentication
  - Cloud Firestore (Native mode)
  - Firebase Storage
- A Google Maps API key

### Setup

1) Clone the repository
```bash
git clone https://github.com/EdungDivinefavour/Nearmate.git
cd Nearmate
```

2) Firebase configuration
- In the Firebase console, create a project and add an Android app with package name `ca.unb.mobiledev.nearmate`.
- Download `google-services.json` and place it at:
  - `app/google-services.json`
- Ensure Authentication (Email/Password), Firestore (Native), and Storage are enabled.

3) Add `google-services.json`

   - Place `google-services.json` into:
     - `app/google-services.json`

4) Run the project
- Open the project in Android Studio and let Gradle sync.
- Select the `app` run configuration and click Run/Debug.
- Sign up with a test account, complete onboarding, and try starting a chat with a nearby test user.

### Firestore data model (expected)

- Collection: `users` (document id = Firebase `uid`)
  - Fields: `id`, `firstName`, `lastName`, `userName`, `prefersToShowUserName`,
    `email`, `country`, `status`, `presence`, `profilePhoto`, `lat`, `lng`
- Collection: `chats` (each document is one chat room)
  - Fields: `participants` (array of user ids), `lastMessage`, `lastMessageTimestamp`
  - Subcollection: `messages`
    - Fields: `id`, `chatId`, `senderId`, `receiverId`, `text`, `timestamp`

Tip: Start Firestore in test mode during development and add restrictive security rules before any production deployment.

## Permissions

Declared in the manifest:
- Internet/Network state
- Fine/Coarse location, background location
- Read media images, Camera (with `FileProvider`)

When testing on Android 10+ you may be prompted for background location; choose “Allow all the time” for continuous presence updates.

## Environment & configuration notes

- Location and nearby user logic is handled in `LocationService` and `UserService`.
- Presence and status fields are stored on the `User` document and updated from the Profile screen.
- Chat IDs are deterministic (based on user ID pairs) so that the same two users always reuse their existing chat room.

## Contributing

- Issues & bugs: Please open a GitHub issue with steps to reproduce and device/emulator details.
- Pull requests: Fork the repo, create a feature branch, and open a PR with a clear description of your changes.
  
## License
