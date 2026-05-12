# 🎨 Channapatna Namma - Heritage Verified

<div align="center">

![Channapatna Namma](https://img.shields.io/badge/Channapatna-Namma-CC9A06?style=for-the-badge&logo=android)

**An Android Marketplace App for Authentic GI-Certified Channapatna Toys**

*Empowering Local Artisans | Preserving Heritage | Connecting Vendors*

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android)](https://www.android.com/)
[![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![IDE](https://img.shields.io/badge/IDE-Android%20Studio-3DDC84?style=flat&logo=android-studio)](https://developer.android.com/studio)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-blue?style=flat)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat)](LICENSE)

</div>

---

## 📖 Table of Contents

- [About The Project](#-about-the-project)
- [Key Features](#-key-features)
- [Screenshots](#-screenshots)
- [Tech Stack](#%EF%B8%8F-tech-stack)
- [Project Structure](#-project-structure)
- [Installation](#-installation--setup)
- [How to Use](#-how-to-use)
- [App Architecture](#%EF%B8%8F-app-architecture)
- [Color Palette](#-color-palette)
- [Future Enhancements](#-future-enhancements)
- [Contributing](#-contributing)
- [Developer](#-developer)
- [License](#-license)

---

## 🪆 About The Project

**Channapatna Namma** (meaning "Our Channapatna") is a comprehensive Android marketplace application designed to bridge the gap between traditional Channapatna toy artisans and modern-day vendors/buyers. 

### 🎯 Mission

Channapatna, a small town in Karnataka, India, is famous for its **GI-tagged wooden toys** crafted using traditional lacquerware techniques passed down through generations. However, many skilled artisans struggle to reach wider markets. This app solves that problem by:

- 🪆 Providing a **digital marketplace** for authentic GI-certified toys
- 🎨 Empowering artisans with **direct sales** capabilities
- 💰 Enabling **automatic earnings** credit on every sale
- 🏦 Facilitating **bank withdrawals** with zero hassle
- 📦 Offering **real-time order tracking** for vendors
- 🌟 Preserving **traditional craftsmanship** through digital documentation

### 🏛️ Heritage Significance

Channapatna toys received **Geographical Indication (GI) tag** in 2006, recognizing their unique cultural and traditional value. Each toy in this app comes with verified GI certification, ensuring authenticity.

---

## ✨ Key Features

### 🛒 For Vendors

| Feature | Description |
|---------|-------------|
| 🔐 **Secure Authentication** | Email/password login with role selection |
| 🔍 **Smart Search** | Find toys by name or GI number |
| 🪆 **Browse Catalog** | View all authentic Channapatna toys |
| 📸 **Photo Gallery** | 6+ photos per toy with swipe view |
| 🪪 **GI Verification** | View GI certificate details |
| 👨‍🎨 **Artisan Info** | Know who made each toy |
| 🛠️ **Materials Used** | See traditional materials |
| 🛒 **Shopping Cart** | Add multiple items |
| ⚡ **Buy Now** | Quick single-item purchase |
| 📦 **Order Tracking** | Real-time 5-stage delivery status |
| 👤 **Profile Management** | Update personal details |
| 🔐 **Change Password** | Secure password updates |
| 📍 **Address Book** | Save delivery addresses |

### 🎨 For Artisans

| Feature | Description |
|---------|-------------|
| 📊 **Workshop Dashboard** | Statistics overview |
| ➕ **Add Toy** | Create new toy listings |
| 🆔 **Auto GI ID** | Format: `CN-YYYYMMDD-XXXX` |
| 📸 **Photo Selection** | Choose from preset designs |
| 🌿 **Materials Logger** | Document traditional materials |
| 📖 **Crafting Story** | Share heritage process |
| 🪆 **My Toys** | View all created toys |
| 📦 **Stock Management** | +/- buttons for quick updates |
| 🪪 **Digital Profile** | Workshop details with GIS |
| 🌐 **GIS Coordinates** | Latitude/Longitude mapping |
| 💰 **Auto Earnings** | Money credited on each sale |
| 📈 **Sales Analytics** | Track total earnings |
| 🏦 **Bank Integration** | Save bank details securely |
| 💸 **Easy Withdrawal** | Transfer to bank account |
| 📜 **Transaction History** | View all earnings & withdrawals |

---

## 📸 Screenshots

### 🚀 Splash & Authentication

| Splash Screen | Login Screen | Register Screen |
|:---:|:---:|:---:|
| 🪆 Heritage Logo | 🔐 Role-based Login | 📝 Quick Registration |

### 🛒 Vendor Experience

| Dashboard | Toy Details | Shopping Cart |
|:---:|:---:|:---:|
| 🔍 Search & Browse | 📸 Photo Gallery | 🛒 Order Summary |

| My Orders | Order Tracking | Profile |
|:---:|:---:|:---:|
| 📦 Order History | 🚚 Live Status | 👤 Personal Info |

### 🎨 Artisan Experience

| Dashboard | Add Toy | My Toys |
|:---:|:---:|:---:|
| 📊 Statistics | ➕ New Listing | 🪆 Toy Catalog |

| Inventory | Earnings | Bank Details |
|:---:|:---:|:---:|
| 📦 Stock Control | 💰 Money Tracker | 🏦 Account Setup |

> **Note:** Add actual screenshots to a `screenshots/` folder and update image paths

---

## 🛠️ Tech Stack

### 🔧 Core Technologies
Language: Kotlin 1.9.22
Platform: Android (Native)
IDE: Android Studio Panda 2025.3.1
Build Tool: Gradle 8.9
Min SDK: API 24 (Android 7.0 Nougat)
Target SDK: API 34 (Android 14)
Java Version: 17

### 📦 Libraries Used

```kotlin
- androidx.core:core-ktx:1.13.1
- androidx.appcompat:appcompat:1.7.0
- androidx.constraintlayout:constraintlayout:2.1.4
- androidx.recyclerview:recyclerview:1.3.2
- androidx.drawerlayout:drawerlayout:1.2.0
- com.google.android.material:material:1.12.0
