# 🍽️ TheVillageBite — Food Ordering Android App

A native **Android food/cafe ordering app** built entirely with **Kotlin + Jetpack Compose**, using **Firebase** for authentication & real-time data, **Supabase Storage** for media, and **Razorpay** for in-app payments.

> 📌 Note: This repository currently contains the **User-side app** (`TheVillageBiteUser`). An Admin-side app is planned/managed separately.

---

## 🧰 Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI Toolkit | Jetpack Compose (Material 3) |
| Navigation | Jetpack Navigation Compose |
| Auth | Firebase Authentication (Email/Password + Google Sign-In via Credential Manager) |
| Database | Firebase Firestore (real-time NoSQL) |
| File/Image Storage | Firebase Storage + Supabase Storage |
| Push Notifications | Firebase Cloud Messaging (FCM) |
| Payments | Razorpay Checkout SDK |
| Image Loading | Coil (Compose) |
| Networking | Volley, Ktor (via Supabase client) |
| Build System | Gradle (Kotlin DSL) |

---

## 📁 Project Structure

```
TheVillageBiteRajat6M2026/
├── README.md
├── .gitignore
└── TheVillageBiteUser/                       # 📱 User-facing Android app module
    ├── build.gradle.kts                      # App-level dependencies (Firebase, Supabase, Razorpay, Coil...)
    ├── settings.gradle.kts
    ├── gradle/                               # Gradle wrapper & version catalog
    └── app/src/main/
        ├── AndroidManifest.xml
        ├── java/com/example/thevillagebiteuser/
        │   ├── MainActivity.kt                # (currently a placeholder / unused entry activity)
        │   ├── SplashScreen.kt                # 🚀 App launch screen → checks auth state
        │   ├── LogInActivity.kt               # 🔐 Login (Email/Password + Google Sign-In)
        │   ├── SignUpActivity.kt              # 📝 New user registration
        │   ├── DashBoardActivity.kt           # 🏠 Main app shell — bottom nav + NavHost + Razorpay callback
        │   │
        │   ├── HomeScreen.kt                  # 🏡 Home tab UI
        │   ├── CategoryScreen.kt              # 📂 Browse food categories (from Firestore)
        │   ├── ProductScreen.kt               # 🍔 List products within a category
        │   ├── ProductDetailScreen.kt         # 🔍 Single product details
        │   │
        │   ├── CartModel.kt                   # 📦 Data classes: CartModel, ProductClass, CategoryClass, OrderModel
        │   ├── CartScreen.kt                  # 🛒 Cart UI + Razorpay payment trigger (CartPaymentHelper)
        │   ├── OrderDetailsScreen.kt          # 🧾 View placed order status/details
        │   ├── ProfileScreen.kt               # 👤 User profile, edit info, Supabase image upload
        │   │
        │   ├── FCMNotification.kt             # 🔔 Firebase Cloud Messaging service (push notifications)
        │   ├── SupabaseObject.kt              # ☁️ Supabase client singleton
        │   ├── FontObj.kt                     # 🔤 Custom font references (Cause font family)
        │   └── ui/theme/                      # 🎨 Compose theme (Color, Type, Theme)
        └── res/
            ├── drawable/, mipmap-*/           # Icons, images, launcher assets
            └── font/                          # Custom .ttf font files (Cause family)
```

---

## 🏗️ Architecture Blueprint

```
                    ┌─────────────────────┐
                    │   SplashScreen.kt    │  ← checks FirebaseAuth.currentUser
                    └──────────┬──────────┘
                 not logged in │ logged in
              ┌────────────────┴────────────────┐
              ▼                                  ▼
   ┌─────────────────────┐          ┌─────────────────────────┐
   │  LogInActivity.kt    │          │   DashBoardActivity.kt   │
   │  SignUpActivity.kt   │─────────▶│  (Compose NavHost host)  │
   │  (Firebase Auth +    │  login   │  + Razorpay payment      │
   │   Google Sign-In)    │  success │    result listener       │
   └─────────────────────┘          └────────────┬────────────┘
                                                  │ NavHost routes between:
        ┌──────────────┬──────────────┬──────────┼───────────────┬────────────────┐
        ▼              ▼              ▼          ▼               ▼                ▼
  HomeScreen   CategoryScreen  ProductScreen ProductDetail   CartScreen      ProfileScreen
   .kt              .kt            .kt      Screen.kt          .kt              .kt
        │              │              │          │               │                │
        └──────────────┴──────┬───────┴──────────┘               │                │
                               ▼                                  ▼                ▼
                    Firebase Firestore                    Razorpay Checkout   Supabase Storage
                 (categories, products,                    (CartPaymentHelper) (profile images)
                    orders, users)                                │
                               │                                  ▼
                               │                         OrderDetailsScreen.kt
                               ▼
                    FCMNotification.kt (order status push alerts)
```

**Key design notes:**
- `CartPaymentHelper` (inside `CartScreen.kt`) acts as a bridge object holding success/error callbacks so `DashBoardActivity`'s Razorpay listener can notify the Compose UI after payment completes.
- `SupabaseObject.kt` is a singleton Supabase client (similar role to a Singleton DB connection) used specifically for storage/media uploads, while Firebase Firestore handles all structured app data.
- Google Sign-In uses the modern **Credential Manager API** (`androidx.credentials`) rather than the older deprecated Google Sign-In SDK.

---

## ✨ Features

- 🔐 **Authentication** — Email/Password signup & login + One-tap Google Sign-In
- 🏡 **Home & Categories** — Browse cafe/food categories fetched live from Firestore
- 🍔 **Product Catalog** — View products per category with detail screens
- 🛒 **Cart & Checkout** — Add to cart, view total, and pay via **Razorpay**
- 🧾 **Order Tracking** — View past/placed order details
- 🔔 **Push Notifications** — Real-time order updates via Firebase Cloud Messaging
- 👤 **Profile Management** — Edit profile & upload profile picture (Supabase Storage)
- 🎨 **Custom Theming** — Custom font family ("Cause") + Material 3 dynamic theme

---

## ⚙️ Setup & Run

### Prerequisites
- Android Studio (latest stable)
- JDK 11
- A Firebase project with `google-services.json` placed in `app/`
- A Supabase project (URL + anon key configured in `SupabaseObject.kt`)
- A Razorpay account (test/live API key)

### Steps
1. Clone the repo:
   ```bash
   git clone https://github.com/irajat111/TheVillageBite.git
   ```
2. Open `TheVillageBiteUser/` in **Android Studio**.
3. Add your own `google-services.json` file inside `TheVillageBiteUser/app/`.
4. Update Supabase URL/key in `SupabaseObject.kt` and Razorpay key in `CartScreen.kt` / `DashBoardActivity.kt`.
5. Sync Gradle and run on an emulator or physical device (minSdk 24+).

---

## 🚧 Known Limitations / TODO
- `MainActivity.kt` is currently an unused placeholder — `SplashActivity` is the real entry point (should be reflected in `AndroidManifest.xml`).
- API keys/config (Firebase, Supabase, Razorpay) should be moved out of source into `local.properties` / secrets management before making this repo public.
- Admin-side app (for managing menu/orders/staff) is maintained as a separate project.
- No automated UI/unit test coverage beyond default template tests.

---

## 👤 Author
**Rajat Singh** ([@irajat111](https://github.com/irajat111))
