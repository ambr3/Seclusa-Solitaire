<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="Seclusa Solitaire" width="120" height="120">
</p>

<h1 align="center">Seclusa Solitaire</h1>

<p align="center">
  <em>Seclusa — from the Latin meaning "private", "secluded", or "set apart"</em>
</p>

<p align="center">
  <em>A privacy-first solitaire collection for Android — zero tracking, no accounts, no ads.</em>
</p>

<p align="center">
  <em>Seclusa Solitaire is a fork of <a href="https://github.com/TobiasBielefeld/Simple-Solitaire">Simple Solitaire Collection</a> by Tobias Bielefeld, modified according to the GPL.</em>
</p>

<p align="center">
  <a href="LICENSE.txt"><img alt="License: GPL-3.0" src="https://img.shields.io/badge/license-GPL--3.0-blue.svg"></a>
  <a href="https://github.com/ambr3/Seclusa-Solitaire/commits/master"><img alt="Last commit" src="https://img.shields.io/github/last-commit/ambr3/Seclusa-Solitaire"></a>
  <img alt="Java" src="https://img.shields.io/badge/built%20with-Java-orange.svg">
  <img alt="Android" src="https://img.shields.io/badge/platform-Android-3ddc84.svg">
</p>

<p align="center">
  <a href="#features">Features</a> ·
  <a href="#privacy">Privacy</a> ·
  <a href="#installation">Installation</a> ·
  <a href="#license">License</a>
</p>

---

No accounts, no ads, no tracking — just privacy-first card games.

---
> **Maintenance note:** Updates are limited to bug fixes and security. The interface may not change between releases.

---

## ✨ Features

### 🃏 Games
- **19 solitaire variants** — AcesUp, Calculation, Canfield, Forty&Eight, FreeCell, Golf, Grandfather's Clock, Gypsy, Klondike, Maze, Mod3, Napoleon's Tomb, Pyramid, SimpleSimon, Spider, Spiderette, TriPeaks, Vegas and Yukon
- **Auto-saving** — your current game is saved when you pause or close the app
- **Undo & hints** — undo up to 20 card movements, with a hint function that shows possible moves
- **High scores** — the top 10 scores are saved in a statistics list

### 🎨 Interface
- **Highly customizable** — 10 card themes, 10 card backgrounds, and 4 background colours
- **Difficulty settings** — for Klondike, Spider, and Golf
- **Left-handed mode** — mirror the card positions to the left side
- **Landscape & tablet support** — with the option to lock orientation
- **Tap to move or drag & drop** — your choice of controls

---

## 🔒 Privacy

Your data is your business. That's the whole point.

- **Zero permissions** — no internet, location, or storage
- **No network** — can't send your data anywhere
- **Zero tracking** — no analytics, no ads, no third-party trackers
- **Stays on device** — game state and scores live in app-private storage
- **Open source** — GPL-3.0, read every line

---

## 📦 Installation

### Via APK

Download the signed APK from the [Releases page](https://github.com/ambr3/Seclusa-Solitaire/releases) and install it on your device.

To verify the APK is signed by this project, check the signing certificate. It must match the SHA-256 fingerprint below:

```
ee9572ee718afb5df1883d9ad27d1c0ced367ab54e3fb04a08aabc80ee05b766
```

### Security scan

Each release APK is analyzed with [MobSF](https://github.com/MobSF/Mobile-Security-Framework-MobSF) (Mobile Security Framework). The full report for this release is stored in the repository: [`security/mobsf-4.1.0.pdf`](security/mobsf-4.1.0.pdf).

## ⚠️ Disclaimer

> The fork is vibe-coded. I am not a professional developer, so I may have missed something. Audit it yourself before use, especially if self-hosting or modifying. Use at your own risk.

---

## 📄 License & Credits

**Seclusa Solitaire** is a fork of **Simple Solitaire Collection** by Tobias Bielefeld, modified according to the GPL.

- Simple Solitaire Collection — https://github.com/TobiasBielefeld/Simple-Solitaire
- Copyright 2016 – Tobias Bielefeld – tobias.bielefeld@gmail.com
- Licensed under GPLv3+ https://www.gnu.org/licenses/gpl-3.0

**Changes made in this fork (cumulative since forking from Simple Solitaire):**

*Privacy & security*
- Forked as *Seclusa Solitaire* under its own app ID (`com.ambr3.seclusasolitaire`)
- Zero permissions — no internet, location, or storage access
- Disabled Google's automatic app-data backup
- Replaced the card-shuffle RNG with `SecureRandom`
- Removed developer/cheat options (instant win, play every card, etc.) from release builds
- Hardened preference loading so corrupted stored values fall back safely instead of crashing

*Platform & build*
- Updated compile/target SDK to 37 (Android 17)
- Modernized the Gradle/AGP build files (AGP 9.3)
- Release APKs are signed and auto-named `Seclusa-Solitaire-v<version>-android17.apk`

*Interface*
- New launcher icon
- Rounded card corners
- Edge-to-edge screen-fit fixes so the board clears the system bars
- Themed dialogs and refreshed menus
- Simplified the About screen

*Cleanup*
- Removed background music and its settings
- Removed the in-app changelog screen and the card-mixing dialog
- Deleted unused classes, audio and image resources
- Fixed handler memory leaks on screen rotation

Current version: **v4.1.0** (versionCode 410)

[GPL-3.0](LICENSE.txt) — free to use, modify, and share, with the same freedom preserved for derivatives.

---
