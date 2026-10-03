# Changelog

All notable changes to **Seclusa Solitaire**.

## Unreleased

### Changed
- Settings boolean options use Material Switch slides instead of checkboxes
- Version **4.3.2** — audit hardening (backup/export/cleartext), JitPack for ambilwarna
- Stop tracking `release/*.apk` in git (distribute via GitHub Releases)

### Security
- MobSF scan for v4.3.2 — 85/100 (grade A); report at `security/mobsf-4.3.2.pdf`
- README: note MobSF RNG/log findings as false positives (SecureRandom + ProGuard Log strip)

### Fixed
- Manifest: non-launcher activities not exported; network security config; backup excluded


### Removed
- Browser PWA (`pwa/`) — Android app only
