# 📚 Threads Clone Android — Complete Documentation Guide

> A comprehensive guide to understand every aspect of the Threads Clone Android application, from setup to source code — explained for beginners.

---

## Table of Contents

| #   | Chapter                                                   | Description                                                                |
| --- | --------------------------------------------------------- | -------------------------------------------------------------------------- |
| 01  | [Project Overview & Architecture](01_PROJECT_OVERVIEW.md) | What the project is, its features, tech stack, and high-level architecture |
| 02  | [Project Setup Guide](02_SETUP_GUIDE.md)                  | How to clone, configure Firebase, and run the project on your machine      |
| 03  | [Project File Structure](03_FILE_STRUCTURE.md)            | Complete folder-by-folder, file-by-file breakdown                          |
| 04  | [Dependencies Explained](04_DEPENDENCIES.md)              | Every library used — what it does and why it's needed                      |
| 05  | [Data Models](05_DATA_MODELS.md)                          | All model classes (`UserModel`, `ThreadModel`, `CommentsModel`, etc.)      |
| 06  | [Base Classes](06_BASE_CLASSES.md)                        | `BaseActivity` and `BaseApplication` — the foundation of the app           |
| 07  | [Activities Deep Dive](07_ACTIVITIES.md)                  | Every Activity explained with its methods and flow                         |
| 08  | [Fragments Deep Dive](08_FRAGMENTS.md)                    | All Fragments (Home, Search, Profile, Notifications) explained             |
| 09  | [Utilities, Services & Database](09_UTILS_SERVICES_DB.md) | Helper classes, FCM service, Appwrite storage                              |
| 10  | [Firebase Integration](10_FIREBASE.md)                    | How Firebase Auth, Realtime Database, and FCM are used                     |
| 11  | [UI & Layouts](11_UI_LAYOUTS.md)                          | XML layouts, drawables, themes, and custom views                           |
| 12  | [Viva Q&A Reference](12_VIVA_QA.md)                       | 50+ probable viva questions with answers                                   |

---

## How to Use This Guide

1. **Start with Chapter 01** to get the big picture
2. **Follow Chapter 02** to set up the project on your machine
3. **Read Chapters 03–04** to understand the structure and libraries
4. **Dive into Chapters 05–09** for code-level understanding
5. **Chapter 10** covers the Firebase backend in depth
6. **Chapter 12** is your quick revision sheet for viva questions

---

## Quick Reference — App Screens

| Screen           | Activity / Fragment            | Purpose                               |
| ---------------- | ------------------------------ | ------------------------------------- |
| Splash           | `SplashActivity`               | App entry, logo animation, auth check |
| Login / Register | `AuthActivity`                 | Email/password & Google Sign-In       |
| Home Feed        | `HomeFragment`                 | View all threads with pull-to-refresh |
| Search           | `SearchFragment`               | Search users (placeholder UI)         |
| Create Thread    | `NewThreadActivity`            | Compose new post with images          |
| Thread Detail    | `ThreadViewActivity`           | View thread, comments, and likes      |
| Reply            | `ReplyToThreadActivity`        | Add a comment to a thread             |
| Profile          | `ProfileFragment`              | View user profile, threads tabs       |
| Edit Profile     | `EditProfileActivity`          | Update bio, link, privacy             |
| Settings         | `SettingsActivity`             | Privacy, account, logout              |
| Notifications    | `ActivityNotificationFragment` | Activity feed with chip filters       |
