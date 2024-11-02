
<div align="center">

![Banner](https://capsule-render.vercel.app/api?type=venom&color=0:fc3c44,100:FF3868&height=200&section=header&text=TIU%20Music&fontSize=60&fontColor=ffffff&animation=twinkling&fontAlignY=50&)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-purple.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Android Studio](https://img.shields.io/badge/Android_Studio-2024.11-green.svg?style=for-the-badge&logo=android-studio)](https://developer.android.com/studio)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Latest-blue.svg?style=for-the-badge&logo=jetpack-compose)](https://developer.android.com/jetpack/compose)

*A native Android music streaming application inspired by Apple Music, uses Spotify Android SDK, built with Kotlin and Jetpack Compose* 🎵

[Key Features](#-key-features) • [Tech Stack](#-tech-stack) • [Installation](#-installation) • [Team](#-team-members)

</div>

## ✨ Key Features

<div align="center">
  <table>
    <tr>
      <td width="33%" align="center">
        <img src="https://github.com/useAnimations/react-useanimations/raw/master/preview/loading-circle.gif" width="40px" height="40px"/>
        <br/>
        <b>Real-time Sync</b>
        <br/>
        <sub>Seamless music synchronization via spotify</sub>
      </td>
      <td width="33%" align="center">
        <img src="https://github.com/useAnimations/react-useanimations/raw/master/preview/spotify.gif" width="40px" height="40px"/>
        <br/>
        <b>Music Streaming</b>
        <br/>
        <sub>High-quality audio streaming</sub>
      </td>
      <td width="33%" align="center">
        <img src="https://github.com/useAnimations/react-useanimations/raw/master/preview/skip-forward.gif" width="40px" height="40px"/>
        <br/>
        <b>Smart Playlists</b>
        <br/>
        <sub>Intelligent music organization</sub>
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

## 🎯 Project Architecture

```mermaid
graph TD
    A[UI Layer<br/>Jetpack Compose] -->|User Events| B[ViewModel Layer]
    B -->|State Updates| A
    B -->|Data Operations| C[Repository Layer]
    C -->|Local Storage| D[SQLite / Room]
    C -->|Network Requests| E[Music APIs]
    
    style A fill:#c084fc,stroke:#4788c7
    style B fill:#fca5a5,stroke:#f5a742
    style C fill:#86efac,stroke:#47c747
    style D fill:#93c5fd,stroke:#c74747
    style E fill:#f0abfc,stroke:#4747c7
```

## 👥 Team Members

<div align="center">

| Role | Name | Student ID |
|:---:|:---:|:---:|
| **🛠️ Team Lead & Fullstack** | Huỳnh Chí Hên | `23520455` |
| **⚙️ Backend Developer** | Đặng Trần Anh Hào | `23520444` |
| **🔧 Backend SDK Developer** | Nguyễn Quốc Hải | `23520419` |
| **🎨 UI Designer & Frontend** | Nguyễn Hữu Duy | `23520374` |
| **💻 Frontend Developer** | Nguyễn Văn Hào | `23520448` |

</div>

## 📦 Installation

<details>
<summary>Click to expand installation steps</summary>

### Prerequisites
* Android Studio
* Git

### Steps
1. Create and navigate to project directory
```bash
mkdir tiu-music && cd tiu-music
```

2. Clone the repository
```bash
git clone https://github.com/LwkPhoenix/TIUMusic.git
```

3. Open in Android Studio
```
File > Open > TIUMusic
```

4. Create an account to login

</details>



## 📚 Resources
- [Project Designs](https://www.figma.com/community/file/1377364496499750549/apple-music-ui-kit)
- [Team Drive](https://drive.google.com/drive/folders/1qdDjAw2VQ9wsmv2D0wbnXL9PBu6ataJx)

---

<div align="center">

*Built with ❤️ by UIT Students*

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:f94c57,100:8b5cf6&height=100&section=footer" width="100%"/>

</div>
