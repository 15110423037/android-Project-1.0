# 🛠️ Project Setup Guide — Threads Clone Android

Follow these steps to set up and run the project locally.

---

## Prerequisites

- **Android Studio** (latest version recommended — Koala or newer)
- **Java 17+** (bundled with Android Studio)
- **Android device or emulator** (API 24+)
- A **Firebase account** ([console.firebase.google.com](https://console.firebase.google.com))
- An **Appwrite account** ([cloud.appwrite.io](https://cloud.appwrite.io))

---

## Step 1: Clone the Repository

```bash
git clone https://github.com/15110423037/android-Project-1.0.git
cd android-Project-1.0
```

---

## Step 2: Set Up Firebase

1. Go to [Firebase Console](https://console.firebase.google.com) → **Create a new project** (or use an existing one).
2. Click **Add App** → **Android**.
3. Enter the package name: `com.harsh.threads.clone`
4. Download the generated `google-services.json` file.
5. Place it inside the `app/` directory:
   ```
   app/google-services.json
   ```
6. In Firebase Console, enable the following services:
   - **Authentication** → Sign-in Method → Enable **Email/Password** and **Google**
   - **Realtime Database** → Create database (start in test mode for development)
   - **Cloud Messaging** → For push notifications

---

## Step 3: Set Up Appwrite (for Image Storage)

1. Go to [Appwrite Console](https://cloud.appwrite.io) → Create a new project.
2. Note down your **Project ID**.
3. Create a **Storage Bucket** and note down the **Bucket ID**.
4. In your Appwrite project settings, add an **Android platform** with package name `com.harsh.threads.clone`.

---

## Step 4: Configure Secrets

Open `app/src/main/java/com/harsh/threads/clone/Constants.java` and replace the placeholder values with your real credentials:

```java
public class Constants {
    // Google OAuth Web Client ID (from Firebase Console → Authentication → Google Sign-In)
    public static final String webApplicationID = "YOUR_WEB_CLIENT_ID_HERE";

    // Firebase Cloud Messaging auth key (leave empty if not using server-side push)
    public static final String FCM_AUTH_KEY = "";

    // Firebase Realtime Database references (no change needed)
    public static final String USERS_DB_REF = "users";
    public static final String USERNAMES_DB_REF = "gusernames";
    public static final String THREADS_DB_REF = "threads";

    // Appwrite credentials
    public static final String APPWRITE_STORAGE_BUCKET_ID = "YOUR_APPWRITE_BUCKET_ID_HERE";
    public static final String APPWRITE_PROJECT_ID = "YOUR_APPWRITE_PROJECT_ID_HERE";
}
```

Also update the Appwrite callback scheme in `app/src/main/AndroidManifest.xml` (line 85):

```xml
<data android:scheme="appwrite-callback-YOUR_APPWRITE_PROJECT_ID_HERE" />
```

Replace `YOUR_APPWRITE_PROJECT_ID_HERE` with your actual Appwrite Project ID.

> ⚠️ **IMPORTANT:** Never commit `Constants.java` with real API keys. Always push with placeholder values. For local development, you can keep your real values — just don't commit them.

---

## Step 5: Where to Find Your Credentials

| Credential                   | Where to Find It                                                            |
| ---------------------------- | --------------------------------------------------------------------------- |
| `webApplicationID`           | Firebase Console → Authentication → Sign-in Method → Google → Web Client ID |
| `google-services.json`       | Firebase Console → Project Settings → Your Apps → Download                  |
| `APPWRITE_PROJECT_ID`        | Appwrite Console → Your Project → Settings → Project ID                     |
| `APPWRITE_STORAGE_BUCKET_ID` | Appwrite Console → Storage → Your Bucket → Bucket ID                        |

---

## Step 6: Build & Run

1. Open the project in **Android Studio**.
2. Wait for Gradle sync to finish.
3. Click **Build → Clean Project** then **Build → Rebuild Project**.
4. Connect your Android device or start an emulator.
5. Click **Run** (▶️) to install and launch the app.

---

## Project Structure

```
app/
├── google-services.json          ← YOUR Firebase config (gitignored)
├── src/main/
│   ├── AndroidManifest.xml
│   └── java/com/harsh/threads/clone/
│       ├── Constants.java        ← API keys & project IDs (placeholders in repo)
│       ├── BaseActivity.java     ← Base class for all activities
│       ├── BaseApplication.java  ← Application class
│       ├── activities/           ← All screen activities
│       ├── fragments/            ← UI fragments (Home, Search, Profile, etc.)
│       ├── model/                ← Data models (User, Thread, Comment, etc.)
│       ├── database/             ← Appwrite storage helper
│       ├── services/             ← Firebase Cloud Messaging service
│       ├── utils/                ← Utility classes
│       └── views/                ← Custom views
├── build.gradle                  ← App-level dependencies
docs/                             ← Project documentation & screenshots
```

---

## Tech Stack

- **Language:** Java 17
- **Build System:** Gradle (Groovy DSL)
- **Backend:** Firebase (Auth, Realtime DB, Cloud Messaging)
- **Image Storage:** Appwrite Cloud
- **Libraries:** Glide, Picasso, Gson, OkHttp, PhotoView Dialog, IVCompressor

---

## Troubleshooting

| Issue                                  | Fix                                                                             |
| -------------------------------------- | ------------------------------------------------------------------------------- |
| `Unsupported class file major version` | Update Gradle wrapper — already set to 8.10.2 in this project                   |
| `google-services.json not found`       | Download from Firebase Console and place in `app/` folder                       |
| Google Sign-In not working             | Ensure `webApplicationID` in Constants.java matches your Firebase Web Client ID |
| Images not uploading                   | Verify Appwrite Project ID and Bucket ID in Constants.java                      |

---
