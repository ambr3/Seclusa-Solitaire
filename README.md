<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="Seclusa Solitaire" width="110" height="110">
</p>

<h1 align="center">Seclusa Solitaire</h1>

<p align="center">
  <em>Privacy-first solitaire for Android — zero tracking, no accounts, no ads.</em>
</p>

<p align="center">
  <em>Seclusa — from the Latin for "private", "secluded", "set apart".</em>
</p>

<p align="center">
  <a href="LICENSE.txt"><img alt="License: GPL-3.0" src="https://img.shields.io/badge/license-GPL--3.0-blue.svg"></a>
  <a href="https://github.com/ambr3/Seclusa-Solitaire/commits/master"><img alt="Last commit" src="https://img.shields.io/github/last-commit/ambr3/Seclusa-Solitaire"></a>
  <img alt="Java" src="https://img.shields.io/badge/built%20with-Java-orange.svg">
  <img alt="Android" src="https://img.shields.io/badge/platform-Android-3ddc84.svg">
</p>

---

**Seclusa Solitaire** is a fork of [Simple Solitaire Collection](https://github.com/TobiasBielefeld/Simple-Solitaire) by Tobias Bielefeld, modified according to the GPL.

## A note from me

Modernising this app has taken me a lot of late nights, I've had help of AI. I'm not a professional developer, so it's been hard going and honestly, I could not have done it without AI.

If you build or modify it yourself, please audit the code before relying on it — all changes are plain GPL-3.0 and open to review.

---

## Games

- **18 solitaire variants** — AcesUp, Calculation, Canfield, Forty&Eight, FreeCell, Golf, Grandfather's Clock, Gypsy, Klondike, Maze, Mod3, Napoleon's Tomb, Pyramid, SimpleSimon, Spider, Spiderette, TriPeaks and Yukon
- **Auto-saving** — your current game is saved when you pause or close the app
- **Undo & hints** — undo up to 20 card movements, with a hint function that shows possible moves
- **High scores** — the top 10 scores are saved in a statistics list

## Interface

- **Highly customizable** — 10 card themes, 10 card backgrounds, 4 background colours
- **Material 3** — modern themes with system dark mode, four colour palettes (Deep Orange, Green, Blue, Purple) and dynamic colours on Android 12+
- **Difficulty settings** — easy / medium / hard for every game
- **Left-handed mode** — mirror the card positions to the left side
- **Landscape & tablet support** — with the option to lock orientation
- **Tap to move or drag & drop** — your choice of controls

## Privacy

Your data is your business

- **Zero permissions** — no internet, location, or storage
- **No network** — the app can't send your data anywhere
- **Zero tracking** — no analytics, no ads, no third-party trackers
- **Stays on device** — game state and scores live in app-private storage
- **Open source** — GPL-3.0, read every line

## Installation

Download the signed APK from the [Releases page](https://github.com/ambr3/Seclusa-Solitaire/releases) and install it on your device.

**Install with [Obtainium](https://obtainium.im):** tap the badge — it pre-fills the app config for this repository, so you just review and confirm:

<p align="center">
  <a href="https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%20%22com.ambr3.seclusasolitaire%22%2C%20%22url%22%3A%20%22https%3A%2F%2Fgithub.com%2Fambr3%2FSeclusa-Solitaire%22%2C%20%22author%22%3A%20%22ambr3%22%2C%20%22name%22%3A%20%22Seclusa%20Solitaire%22%2C%20%22preferredApkIndex%22%3A%200%2C%20%22additionalSettings%22%3A%20%22%7B%5C%22includePrereleases%5C%22%3A%20false%2C%20%5C%22fallbackToOlderReleases%5C%22%3A%20true%2C%20%5C%22autoApkFiltering%5C%22%3A%20true%2C%20%5C%22appName%5C%22%3A%20%5C%22Seclusa%20Solitaire%5C%22%7D%22%7D">
    <img src="obtainium-badge.png" alt="Get it on Obtainium" width="161">
  </a>
</p>

To verify an APK is signed by this project, check its signing certificate against this SHA-256 fingerprint:

```
ee9572ee718afb5df1883d9ad27d1c0ced367ab54e3fb04a08aabc80ee05b766
```

Each release APK is also analyzed with [MobSF](https://github.com/MobSF/Mobile-Security-Framework-MobSF). The latest report is stored in the repository: [`security/mobsf-4.3.1.pdf`](security/mobsf-4.3.1.pdf).

---

## Changes made in this fork

### Material 3 modernisation (the big one)

- **Full Material 3 migration** — every screen now runs on `Theme.Material3` DayNight themes with the complete M3 colour-role system (surface containers, outlines, error states) instead of the old primary/accent handful
- **Native dark mode** — light and dark colour palettes in `values/` and `values-night/`, following the system setting
- **Four colour palettes** — Deep Orange (default), Green, Blue and Purple, each with full light+dark role definitions
- **Dynamic colours** — Material You Wallpaper colours on Android 12+, switchable from Settings
- **Custom type scale** — a clear, tight Material 3 text hierarchy (bold headings, sans-serif body) across games, dialogs, About, Manual and Settings
- **Refreshed surfaces** — rounded Material 3 dialogs, card-style Settings preferences, pill-shaped Manual buttons, a redesigned score chip, and a theme-aware Menu-order list

### Privacy & security

- Forked as *Seclusa Solitaire* under its own app ID (`com.ambr3.seclusasolitaire`)
- Zero permissions — no internet, location, or storage access
- Disabled Google's automatic app-data backup
- Replaced the card-shuffle RNG with `SecureRandom`
- Removed developer/cheat options (instant win, play every card, etc.) from release builds
- Hardened preference loading so corrupted stored values fall back safely instead of crashing

### Platform & build

- Updated compile/target SDK to 37 (Android 17)
- Modernized the Gradle/AGP build files (AGP 9.3)
- Release APKs are signed and auto-named `Seclusa-Solitaire-v<version>-android17.apk`

### Interface & polish

- New launcher icon
- Rounded card corners
- Edge-to-edge screen-fit fixes so the board clears the system bars
- Simplified the About screen and added proper attribution for contributors

### Cleanup

- Removed background music and its settings
- Removed the in-app changelog screen and the card-mixing dialog
- Deleted unused classes, audio and image resources
- Fixed handler memory leaks on screen rotation

### Why the Vegas game was removed

Seclusa Solitaire ships without **any** gambling/gaming aspects. The Vegas variant (a Klondike scoring mode) tracked a simulated bet amount, win amount and a "balance" in app settings. Even though that money was purely fictional — a local number, never real currency, never connected to anything — betting mechanics have no place in this collection. The game and every trace of its betting/scoring settings were removed for good.

Current version: **v4.3.1** (versionCode 431)

---

## License & Credits

**Seclusa Solitaire** is a fork of **Simple Solitaire Collection** by Tobias Bielefeld, modified according to the GPL.

- Simple Solitaire Collection — https://github.com/TobiasBielefeld/Simple-Solitaire
- Copyright 2016 – Tobias Bielefeld – tobias.bielefeld@gmail.com
- Licensed under GPLv3+ https://www.gnu.org/licenses/gpl-3.0

[GPL-3.0](LICENSE.txt) — free to use, modify, and share, with the same freedom preserved for derivatives.
