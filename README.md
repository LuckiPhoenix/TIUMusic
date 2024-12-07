<div align="center">


<img src="app/src/main/res/drawable/tiumusicfulllogo.png" alt="TIU Music Logo" height="200"/>

[![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-purple.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Android Studio](https://img.shields.io/badge/Android_Studio-2024.11-green.svg?style=for-the-badge&logo=android-studio)](https://developer.android.com/studio)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Latest-blue.svg?style=for-the-badge&logo=jetpack-compose)](https://developer.android.com/jetpack/compose)

*A native Android music streaming application inspired by Apple Music, uses Youtube API, built with Kotlin and Jetpack Compose* 🎵

[Key Features](#-key-features) • [Tech Stack](#-tech-stack) • [Installation](#-installation) • [Team](#-team-members)

</div>

## 📖 Project Overview

TIU Music is an innovative Android music streaming application developed by students at UIT (University of Information Technology, Vietnam National University - Ho Chi Minh City). Inspired by Apple Music's design, this native Android app leverages YouTube Music's API to provide seamless music searching, storing, and streaming.


## 📚 Academic Project

This project was developed as part of a course requirement at UIT.

### Academic Context
- **Course:** IT008.P11
- **Objective:** Demonstrate proficiency in Android app development using Kotlin and Jetpack Compose

### Academic Integrity
- This project is for educational purposes
- Not intended for commercial distribution
- Developed as a practical application of mobile development principles

## ✨ Key Features

<div align="center">
  <table>
    <tr>
      <td width="33%" align="center">
        <img src="https://user-images.githubusercontent.com/74038190/212281780-0afd9616-8310-46e9-a898-c4f5269f1387.gif" width="40px" height="40px"/>
        <br/>
        <b>Top-Notch Design</b>
        <br/>
        <sub>State-Of-The-Art UI/UX philosophy inspired by Apple</sub>
      </td>
      <td width="33%" align="center">
        <img src=https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Musical%20Notes.png width="40px" height="40px"/>
        <br/>
        <b>Music Streaming</b>
        <br/>
        <sub>High-quality audio streaming directly from Youtube</sub>
      </td>
      <td width="33%" align="center">
        <img src="https://user-images.githubusercontent.com/74038190/235294007-de441046-823e-4eff-89bf-d4df52858b65.gif" width="40px" height="40px"/>
        <br/>
        <b>Smart Playlists</b>
        <br/>
        <sub>Seamless music synchronization via Youtube Music</sub>
      </td>
    </tr>
  </table>
</div>

## 🛠 Tech Stack

<div align="center">

### Core Technologies
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

### Backend & Storage
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![Room](https://img.shields.io/badge/Room-FF6B00?style=for-the-badge&logo=android&logoColor=white)

### Development Tools
![Android Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

</div>

## 🔌 Key Third-Party Integrations

### YouTube Player
We utilize the [android-youtube-player](https://github.com/PierfrancescoSoffritti/android-youtube-player) library, developed by Pierfrancesco Soffritti, to enhance our video playback capabilities. This library provides:
- Robust YouTube video integration
- Customizable player controls
- Smooth video and audio streaming experience

*Note: Special thanks to the library's maintainers for their excellent open-source contribution.👏*

## 🎯 Project Architecture

```mermaid
graph TD
    A[UI Layer<br/>Jetpack Compose] -->|User Events| B[ViewModel Layer]
    B -->|State Updates| A
    B -->|Data Operations| C[Repository Layer]
    C -->|Local Storage| D[SQLite / Room]
    C -->|Network Requests| E[Music APIs]
    
    style A fill:#c084fc,stroke:#4788c7,color:#000000
    style B fill:#fca5a5,stroke:#f5a742,color:#000000
    style C fill:#86efac,stroke:#47c747,color:#000000
    style D fill:#93c5fd,stroke:#c74747,color:#000000
    style E fill:#f0abfc,stroke:#4747c7,color:#000000
```

## 👥 Team Members

<div align="center">

| Role | Name | Student ID |
|:---:|:---:|:---:|
| **🛠️ Team Leader & Fullstack Developer** | Huỳnh Chí Hên | `23520455` |
| **👁️ Backend Visual & Audio Developer** | Đặng Trần Anh Hào | `23520444` |
| **🔧 FullStack API Developer** | Nguyễn Quốc Hải | `23520419` |
| **🎨 UI Designer & Frontend Developer** | Nguyễn Hữu Duy | `23520374` |
| **💻 Frontend QA Developer** | Nguyễn Văn Hào | `23520448` |

</div>

## 📦 Installation

### Requirements
- Android Studio LadyBug | 2024.2.1 Patch 1 or later
- Kotlin 1.8.0+
- JDK 21+
- Git

### Configuration Steps
1. Clone the Repository
```bash
git clone https://github.com/LwkPhoenix/TIUMusic.git
cd TIUMusic
```

2. Dependencies
   - Sync project with Gradle files
   - Resolve dependencies in Android Studio

3. Run the Application
   - Select an emulator or connect a physical Android device
   - Click "Run" in Android Studio

## 📚 Learning Outcomes

This project provided valuable experience in:
- Visual Programming
- Native Android app development
- Kotlin programming
- Jetpack Compose UI design
- API integration
- Mobile application architecture
- Collaborative software development

## 📞 Contact

For academic inquiries or project details, please contact:
- Project Leader: Huynhchihen2005@gmail.com

---

<div align="center">

*Developed as an Academic Project by UIT Students*

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:f94c57,100:8b5cf6&height=100&section=footer" width="100%"/>

</div>
