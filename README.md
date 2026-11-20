# זמר · Zemer

**זמר** הוא נגן מוזיקה לאנדרואיד **בלי תמונות ובלי וידאו**, של KosherSafe.
**Zemer** is an Android music player **without pictures or video**, made for KosherSafe.

## What is different from Kreate

Zemer is a modified version of [Kreate](https://github.com/knighthat/Kreate) by knighthat
(itself a fork of [RiMusic](https://github.com/fast4x/RiMusic)), based on Kreate v2.2.5.
Changes in Zemer:

- **No pictures at all.** The image loader has no network fetcher and refuses every
  picture request (`NoPictures.kt`); the HTTP client refuses picture servers
  (`PictureHostBlocker.kt`); artwork is never put into the notification / lock screen
  (`CoilBitmapLoader.kt`). Neutral icons are drawn instead.
- **No video.** The embedded video player and the "videos" / "podcasts" search tabs were removed.
- **Always uses the phone's own DNS** (no proxy / DNS-over-HTTPS), so a filter on the phone applies.
- No account login, no Discord, no self-updater (built from the `fdroid` flavor).
- Hebrew first, right-to-left layout, and layouts that do not break with a very large
  system font or display zoom (`ZemerScale.kt`, single-line auto-shrinking tab labels).
- New name, logo and package id (`il.co.koshersafe.music`).

Search for `Zemer:` in the code to find every change.

## Building

```
gradlew :composeApp:assembleFdroidArm64ProdRelease
```

Release signing reads `.ignore.d/keystores/zemer.jks` + `keystore.properties` (not in the repository).

## License

Zemer, like Kreate and RiMusic, is free software under the **GNU General Public License v3.0**
(see [LICENSE](LICENSE)). The original Kreate README is kept in [README-KREATE.md](README-KREATE.md).
Zemer is not affiliated with or endorsed by Kreate, RiMusic, YouTube or Google.
