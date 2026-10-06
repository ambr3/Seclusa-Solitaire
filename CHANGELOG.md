# Changelog

All notable changes to **Seclusa Solitaire**.

## 4.3.5 — 2026-10-06

### Changed
- Difficulty deal filter: Easy needs a hint-bot win plus 3 opening moves; Medium needs ~40 greedy moves (was 20); exhausted searches keep the best deal seen
- Settings / menus themed to match preference pills: Material dialog theme for framework prefs, pill radio/spinner/hide-game rows, ensure-movability & undo dialogs, Statistics pill tabs, frosted recycle chip & themed autocomplete button
- Night mode: true-neutral charcoal (dropped green-grey cast), softer accents, quieter pill rims, clearer shell vs card contrast
- Settings migrated off deprecated PreferenceActivity to AndroidX PreferenceFragmentCompat
- Won / new-game / help menus use outline pill option buttons instead of flat list rows
- Settings dialog uses nearly full width in landscape (lifted Material dialog width cap)
- Appearance: System / Light / Dark pill tabs (defaults to system; layout no longer stripped by section-card styling)
- Dark mode: score / timer (and stack outlines) use light text when the saved colour is too dark to read

## 4.3.4 — 2026-10-05

### Changed
- README: smaller one-row screenshots; F-Droid / Obtainium / GitHub install badges on one row
- Settings left-column header pills sit closer together
- In-game hide-menu control lives in the menu bar (menu-bar style); peek button when collapsed
- Dark mode: cooler surfaces, elevated preference pills, dark frosted game chrome

### Fixed
- Settings left-column selection highlight updates when switching sections (no scroll needed)
- README “A note from me” links to the Seclusa about page
- Dark mode contrast (muddy near-black pills; light icons on white menu glass)

### Security
- MobSF scan for v4.3.4 — 85/100 (grade A); report at `security/mobsf-4.3.4.pdf`
- VirusTotal for v4.3.4 — no vendors flagged malicious; report at `security/virustotal4.3.4.pdf`

## 4.3.3 — 2026-10-04


### Added
- Separate Settings toggle for the winning sound
- Unit tests for difficulty deal-filter rules and colour-clump shuffle
- Fresh phone/tablet screenshots (menu + Klondike) under `pictures/screenshots/`

### Changed
- README simplified; shows current phone/tablet screenshots

### Security
- MobSF scan for v4.3.3 — 85/100 (grade A); report at `security/mobsf-4.3.3.pdf`

### Changed
- Statistics tabs use Material TabLayout (dropped abandoned PagerSlidingTabStrip)
- Track `gradle/gradle-daemon-jvm.properties` for reproducible Gradle JDK 25 toolchain
- Default theme colour is green
- Settings boolean options use Material Switch slides instead of checkboxes
- Settings options each use their own pill card (clearer spacing)
- Settings header pills and option rows have clearer gaps in portrait
- Settings pills are light with a theme-colour accent rim/title
- Main menu game pills are square (equal width/height) in portrait and landscape
- Medium difficulty now filters deals for playable starts (min 20 moves)
- Shuffle breaks long same-colour clumps (when true randomisation is off)

### Fixed
- README: default theme is green; six playfield background colours
- Drop unused Play Store / GitHub-issues URL string resources
- Theme colour change now fully restarts the task so the menu background updates
- Theme change refreshes menu preview label colours (cached bitmaps cleared)
- Menu square pills fit previews without stretching
- Playfield background/text colours re-apply when returning from Settings
- Settings pill text stays readable when selected (no fade / contrast washout)
- Phone landscape card sizing overflow for all games (tablets unchanged)
- Phone playfield inset so Score/Time chips never cover cards
- Main menu title wraps to 2 lines on phone; game grid fits one screen (no scroll)

### Removed
- Abandoned PagerSlidingTabStrip dependency (and its About license entry)
