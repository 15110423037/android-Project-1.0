# Chapter 12: Viva Q&A Reference

A comprehensive list of probable viva questions with concise answers, organized by topic.

---

## 📌 Project Overview

### Q1: What is this project about?

This is a **Threads Clone** — a social media app built for Android that replicates Meta's Threads app. Users can sign up, create text posts (threads), attach images, like/comment on posts, follow users, and receive push notifications.

### Q2: What technologies are used?

- **Language:** Java
- **IDE:** Android Studio
- **Backend:** Firebase (Auth, Realtime Database, Cloud Messaging)
- **File Storage:** Appwrite Cloud Storage
- **Image Loading:** Glide + Picasso
- **UI:** Material Design Components, SwipeRefreshLayout

### Q3: What architecture pattern does this project follow?

It follows an **Activity-Fragment architecture** where a `BaseActivity` serves as the parent for all activities, providing shared functionality (Firebase init, auth, utilities). Fragments are used inside `MainActivity` for tab-based navigation.

### Q4: Why not use MVVM or MVC?

The project uses a simpler architecture suitable for its scope. Activities handle both UI and logic directly. For larger production apps, MVVM (Model-View-ViewModel) with LiveData/ViewModel would be more appropriate for separation of concerns.

---

## 🔐 Authentication

### Q5: How does user login work?

Two methods:

1. **Email + Password** — Uses `FirebaseAuth.signInWithEmailAndPassword()`
2. **Google Sign-In** — Uses Google Sign-In SDK → gets ID token → passes to `FirebaseAuth.signInWithCredential()`

### Q6: Can users log in with a username?

Yes. If the input doesn't contain `@`, it's treated as a username. The app looks up the corresponding email from `/users/{username}` in Firebase DB, then logs in with that email + password.

### Q7: What validation is done on registration?

- **Username:** 6–20 chars, lowercase + numbers only, no special chars (except `.` and `_`), must be unique
- **Email:** Must contain `@`
- **Password:** Minimum 6 characters

### Q8: How is Google Sign-In configured?

- Uses `GoogleSignInOptions` with `requestIdToken(Constants.webApplicationID)`
- The Web Client ID comes from Firebase Console (not the Android client ID)
- Uses `GoogleSignInClient.getSignInIntent()` to launch the account picker

### Q9: Where is user authentication data stored?

Firebase Auth handles the login credentials. User profile data (bio, followers, etc.) is stored in Firebase Realtime Database under `/users/{username}`.

---

## 📦 Firebase & Database

### Q10: What is Firebase Realtime Database?

A cloud-hosted NoSQL JSON database. Data is stored as a JSON tree and synced in **real-time** — when one user posts, all connected clients get the update instantly.

### Q11: How is data structured in Firebase?

Three main nodes:

- `/users/{username}` — User profiles (UserModel)
- `/threads/{threadId}` — Thread posts (ThreadModel)
- `/gusernames` — Username lookup (for uniqueness)

### Q12: What is the difference between `addValueEventListener` and `addListenerForSingleValueEvent`?

- `addValueEventListener` — Listens **continuously** for changes (used for real-time updates)
- `addListenerForSingleValueEvent` — Reads data **once** and stops (used for one-time lookups)

### Q13: What is `ChildEventListener` and why is it used in the feed?

`ChildEventListener` listens for individual child additions, changes, and removals. In the home feed, it's more efficient than `ValueEventListener` because it only sends the specific thread that changed, not the entire list every time.

### Q14: How are threads stored?

Each thread is stored as a child of `/threads` with a unique push key. The `ThreadModel` maps to: text, images, likes (list of UIDs), comments, poll options, etc.

### Q15: How does the like feature work?

Each thread's `likes` field is a `List<String>` of user UIDs. When a user taps like:

- If their UID is in the list → remove it (unlike)
- If not → add it (like)
- Save the updated thread back to Firebase

---

## 📱 Activities & Fragments

### Q16: What is an Activity?

An Activity represents a single screen with a user interface. For example, `AuthActivity` shows the login screen, `SettingsActivity` shows the settings screen.

### Q17: What is a Fragment?

A Fragment is a reusable portion of UI that lives inside an Activity. It has its own lifecycle but exists within a parent Activity. Fragments allow you to build multi-pane UIs and reuse components.

### Q18: What is BaseActivity and why is it used?

`BaseActivity` is the parent class for all activities. It contains shared code like Firebase initialization, Google Sign-In, progress dialogs, keyboard handling, and user data management. All activities **extend** it to inherit this functionality.

### Q19: How does the bottom navigation work?

`MainActivity` has a bottom bar with 5 icons. When an icon is tapped, `setFragment(position)` is called, which uses `FragmentManager` and `FragmentTransaction` to replace the current fragment in the `fragmentContainerView`.

### Q20: How does the home feed load threads in real-time?

`HomeFragment` uses `addChildEventListener()` on the `/threads` reference. When a new thread is added to Firebase, `onChildAdded()` is called and the thread is inserted at position 0 in the adapter. When a thread is updated, `onChildChanged()` updates it in place.

### Q21: What is ViewPager2 and how is it used?

`ViewPager2` is a swipeable container for fragments. In `ProfileFragment`, it's used with a `TabLayout` to show 3 tabs: Threads, Replies, and Reposts. A `FragmentStateAdapter` provides the fragment for each tab.

---

## 🔧 Technical Concepts

### Q22: What is View Binding?

View Binding generates a class for each XML layout that provides direct references to all UI elements. It replaces `findViewById()` with type-safe access like `binding.username.setText()`.

### Q23: What is Parcelable?

`Parcelable` is an Android interface for serializing objects to pass between Activities via Intents. `ThreadModel` and `PollOptions` implement it so they can be passed as `intent.putExtra("key", parcelableObject)`.

### Q24: What is the Singleton pattern and where is it used?

Singleton ensures only one instance of a class exists. Used in:

- `StorageHelper.getInstance()` — Single Appwrite client
- `HomeFragment.getInstance()` — Single fragment instance
- `SearchFragment.getInstance()`

### Q25: What is RecyclerView?

RecyclerView is an efficient scrollable list that **recycles** (reuses) views as you scroll. It requires:

- `Adapter` — Binds data to views
- `ViewHolder` — Holds references to item views
- `LayoutManager` — Controls item arrangement (Linear, Grid)

### Q26: What is SwipeRefreshLayout?

A Material Design component that adds "pull-to-refresh" gesture. When the user swipes down at the top of the list, `onRefresh()` is called to reload data.

### Q27: What is a ChildEventListener vs ValueEventListener?

|                | ChildEventListener                                 | ValueEventListener            |
| -------------- | -------------------------------------------------- | ----------------------------- |
| **Listens to** | Individual child changes                           | Entire node value             |
| **Callbacks**  | `onChildAdded`, `onChildChanged`, `onChildRemoved` | `onDataChange`                |
| **Data sent**  | Only the changed child                             | Entire node + all children    |
| **Best for**   | Lists/feeds (threads, messages)                    | Single objects (user profile) |

### Q28: How is image loading handled?

- **Glide:** Used for post images and GIFs in thread feed. Handles caching, resizing, GIF animation automatically.
- **Picasso:** Used for profile pictures. Simpler API with `Picasso.get().load(url).into(imageView)`.

### Q29: What is ActivityResultLauncher?

A modern replacement for `startActivityForResult()`. Used in `NewThreadActivity` for photo picking:

```java
ActivityResultLauncher<PickVisualMediaRequest> launcher =
    registerForActivityResult(new PickMultipleVisualMedia(5), uris -> {
        // Handle selected images
    });
```

---

## ☁️ Cloud Services

### Q30: What is Appwrite and how is it used?

Appwrite is an open-source backend service. In this project, it's used for **file/image storage** via its SDK. `StorageHelper` handles upload, download, and delete operations through the Appwrite Storage API.

### Q31: How do push notifications work?

1. **Registration:** On first launch, FCM gives the device a unique token
2. **Token stored:** Token is saved to the user's profile in Firebase DB
3. **Sending:** When someone interacts with your content, the app sends an HTTP POST to FCM's endpoint with the target token
4. **Receiving:** `FirebaseMessagingService.onMessageReceived()` builds and shows the notification

### Q32: What is the `google-services.json` file?

A configuration file from Firebase Console that connects the app to the specific Firebase project. It contains project IDs, API keys, and OAuth client settings. It must be placed in the `app/` directory.

---

## 📊 Data Models

### Q33: What are the main models?

| Model                   | Purpose      | Key Fields                                      |
| ----------------------- | ------------ | ----------------------------------------------- |
| `UserModel`             | User profile | uid, username, email, bio, followers, following |
| `ThreadModel`           | Thread/post  | iD, text, images, likes, comments, isPoll       |
| `CommentsModel`         | Comment      | uid, text, images, likes, time                  |
| `PollOptions`           | Poll data    | option1–4 (each a PollOptionsItem)              |
| `NotificationItemModel` | Notification | senderUid, title, description, postId           |

### Q34: Why do models need an empty constructor?

Firebase Realtime Database requires a **no-argument constructor** to deserialize JSON data back into Java objects. Without it, `snapshot.getValue(UserModel.class)` would throw an error.

### Q35: Why does ThreadModel implement Parcelable?

To allow passing `ThreadModel` objects between Activities via `Intent`. For example, when navigating from the home feed to `ThreadViewActivity`, the thread data is passed as a Parcelable extra.

---

## ⚙️ Build & Configuration

### Q36: What is Gradle?

Gradle is the **build system** for Android projects. It compiles code, packages resources, manages dependencies, and creates the APK file.

### Q37: What is the difference between `build.gradle` (root) and `build.gradle` (app)?

- **Root `build.gradle`:** Declares plugins used across the project
- **App `build.gradle`:** Configuration specific to the app module — SDK versions, dependencies, build types

### Q38: What is `libs.versions.toml`?

A **version catalog** file that centralizes dependency version numbers. Instead of hardcoding versions in `build.gradle`, you reference them as `libs.firebase.auth`. This makes updating easier.

### Q39: What SDK versions are used?

- **minSdk: 24** — Lowest Android version supported (Android 7.0, Nougat)
- **targetSdk: 34** — Latest Android version targeted (Android 14)
- **compileSdk: 34** — SDK version used for compilation

### Q40: What is `proguard-rules.pro`?

ProGuard (now R8) is a tool that **obfuscates** (makes code unreadable) and **shrinks** your APK for release builds. The rules file tells it which code to keep untouched.

---

## 🛡️ Android Manifest

### Q41: What permissions are declared?

- `INTERNET` — Required for Firebase, Appwrite, and image loading
- `READ_MEDIA_VISUAL_USER_SELECTED` — Access to selected photos/videos (Android 14+)

### Q42: What is `exported` in activity declarations?

- `exported="true"` — Can be launched from outside the app (e.g., launcher icon, deep links)
- `exported="false"` — Can only be opened from within the app

### Q43: What is the launcher activity?

`SplashActivity` — it has the `MAIN` action and `LAUNCHER` category in its intent filter, making it the entry point when the app icon is tapped.

---

## 🐛 Known Limitations

### Q44: What features are incomplete?

- **SearchFragment** — Static placeholder, no actual search logic
- **Polls** — UI is built but the creation flow is disabled (`if (true) return`)
- **AccountActivity, FollowAndInviteFriendsActivity** — Empty placeholder screens
- **FCM server key** — `Constants.FCM_AUTH_KEY` is empty

### Q45: What is the potential crash issue?

`BaseActivity.mUser` can be `null` in some edge cases (e.g., if `NewThreadActivity` is opened before the user data finishes loading from Firebase). Some activities access `mUser` without null checks.

---

## 🏗️ Design Patterns Used

### Q46: What design patterns are used in this project?

| Pattern               | Where Used                                                  |
| --------------------- | ----------------------------------------------------------- |
| **Singleton**         | `StorageHelper`, Fragment `getInstance()`                   |
| **Inheritance**       | All activities extend `BaseActivity`                        |
| **Observer/Listener** | Firebase `ValueEventListener`, `ChildEventListener`         |
| **Callback**          | `AuthListener` interface, `dataChangeListener`              |
| **Adapter**           | RecyclerView adapters in HomeFragment, SearchFragment       |
| **Builder**           | `MaterialAlertDialogBuilder`, `GoogleSignInOptions.Builder` |
| **Compound View**     | `ProfileTaskView` (custom view with XML layout)             |

### Q47: What is the Observer pattern in this project?

Firebase listeners implement the Observer pattern: the app **registers** a listener on a database reference, and Firebase **notifies** it whenever data changes. The app doesn't need to poll for updates.

### Q48: How does the Singleton pattern work?

```java
private static StorageHelper instance;
public static StorageHelper getInstance(Context context) {
    if (instance == null) {
        instance = new StorageHelper(context);
    }
    return instance;
}
```

The first call creates the instance; all subsequent calls return the same one.

---

## 📋 Lifecycle & Navigation

### Q49: What is the Activity lifecycle?

`onCreate()` → `onStart()` → `onResume()` → **Running** → `onPause()` → `onStop()` → `onDestroy()`

This app primarily uses `onCreate()` (initialization) and `onResume()` (refresh data).

### Q50: How does the app handle back navigation?

- **In fragments:** Uses `FragmentManager.popBackStack()` to return to the previous fragment
- **At root fragment:** Shows an "Are you sure?" exit dialog
- **In activities:** `pressBack()` calls `finish()` with fade-out animation

### Q51: What is `Intent` and how is it used?

An `Intent` is a messaging object used to navigate between activities or pass data:

```java
Intent intent = new Intent(this, ThreadViewActivity.class);
intent.putExtra("thread", threadId);
startActivity(intent);
```

### Q52: How are fragment transitions animated?

```java
FragmentTransaction ft = fm.beginTransaction()
    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
```

This applies a fade effect when switching between fragments.
