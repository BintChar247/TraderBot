# Garasi — garage-sale listing helper (Android)

Photograph stuff you don't use anymore. Garasi helps you identify it, write an honest,
human-sounding title and description (Bahasa Indonesia or English), pick a fair second-hand
price, checks your photos and text before you post, then gets you into Tokopedia, Shopee or OLX
in a few taps.

**100% free to run:** no paid APIs, no API keys, no accounts, no scraping, no automated logins.
Everything runs on your phone. AI help goes through *your own* Claude / Gemini / ChatGPT app via
Android's share sheet.

---

## What's inside

| Feature | How it works |
|---|---|
| Inventory | Grid of items with cover, title, price, status (Draft / Ready / Posted / Sold). Filter, search, sort by date or price. |
| Photos | CameraX (tap-to-focus, 1:1 guide, "flaw close-up" mode) or gallery picker, up to 10. Reorder, make cover, square crop, tag angle / flaw. Originals kept; listing copies compressed to < 1 MB. |
| Identify | ML Kit on-device: barcode scanning, text recognition (brand + model number from labels/boxes), image labeling → category. Always editable. |
| Ask AI (free) | Builds a prompt in the listing language with item details + photos, opens the share sheet (Claude, Gemini, ChatGPT shown first). The prompt is also copied to the clipboard. "Paste AI answer" parses `TITLE / DESCRIPTION / CONDITION / USED_PRICE / NEW_PRICE`. Same hand-off for **Translate** and **Improve my text**. |
| Templates | Per-category description templates in both languages, with `[blanks]` for missing details. Title builder: Brand Model Variant Condition, max 70 chars. |
| Listing language | Per item 🇮🇩 / 🇬🇧 toggle; title + description stored separately per language. Default in Settings. |
| Price check | Opens Tokopedia / Shopee / OLX search sorted by lowest price. Enter low / median / high → suggestion = median × condition factor (New 0.9, Like New 0.8, Used–Good 0.65, Has Flaws 0.45) + 10 % negotiation buffer. |
| Appeal Score | 0–100, on-device. Photos 40 pts (Laplacian blur, brightness/contrast, ML Kit object framing 60–80 %, background edge density + colour variance, ≥ 800 px, square cover, angle checklist, 4–8 photos, flaw-photo honesty check), title 20, description 30 (required info, 300–1,200 chars, long sentences, walls of text, emoji, "!!!", ALL CAPS, stiff/hype phrases per language, mixed language, phone numbers / links), price 10 (vs. compare median). Shows the top 3 fixes with one-tap actions. |
| Post | Per-platform title/description limits, category hint, weight. Copies the title, saves photos to `Pictures/Garasi/<item>`, opens the marketplace's sell screen (or app, or website). A **floating checklist** with Copy buttons for title / description / price stays on top of the marketplace app ("Display over other apps"; falls back to a notification). "Copy both languages" puts Indonesian on top, English under a divider. Mark posted per platform + store the listing URL. |
| After posting | Mark sold (final price + platform) → reminder to delete the other listings. Stats: items sold, total IDR earned, average days to sell. |
| Settings | Default listing language, default city, weight presets, theme (system/light/dark), export / import everything as a `.zip` (JSON + photos). |

UI text is English and lives in `app/src/main/res/values/strings*.xml` (add `values-in/strings.xml`
etc. for more UI languages). Listing text (templates, condition labels, AI prompts) is listing
*content*, in `app/src/main/java/com/garasi/app/text/`.

### Project layout

```
garasi/
  app/src/main/java/com/garasi/app/
    data/       Room entities (Item, Photo, PlatformListing), DAO, settings store
    photo/      import, EXIF rotation, < 1 MB compression, square crop
    ml/         ML Kit identification + photo quality metrics
    quality/    Appeal Score (pure Kotlin, unit-tested)
    text/       templates, title builder, AI prompts + answer parser, price maths
    platform/   marketplace links, share sheet, clipboard, gallery album, backup, floating checklist
    ui/         Compose screens + shared per-item ViewModel
  app/src/test/ unit tests (parser, prices, title/description/photo checks)
```

---

## Download the APK (no computer setup)

**Direct link (always the newest build):**
https://github.com/BintChar247/TraderBot/releases/download/garasi-latest/garasi.apk

Or open the [Garasi release page](https://github.com/BintChar247/TraderBot/releases/tag/garasi-latest)
on your phone and tap **garasi.apk**.

Every push that touches `garasi/` runs **GitHub Actions → "Garasi APK"**
(`.github/workflows/garasi-android.yml`): unit tests, `assembleRelease`, then it replaces the
`garasi-latest` release with the new APK. You can also start it by hand: Actions → Garasi APK →
*Run workflow*.

Without signing secrets the CI APK is signed with that run's throwaway debug key. It installs
fine, but the next CI build has a different key, so Android will refuse to *update* over it
(uninstall first, after exporting a backup). Add your own key as secrets (below) to fix that.

---

## Build it yourself

### Android Studio
1. Install Android Studio (Ladybug or newer). It bundles JDK 17+.
2. *File → Open* → select the `garasi/` folder. Let Gradle sync (first time downloads ~1 GB).
3. Plug in your phone (USB debugging on) → ▶ Run. That's a debug build.
4. Release APK: *Build → Generate Signed App Bundle / APK → APK*, or use the command line below.

### Command line
Requirements: JDK 17, Android SDK (set `ANDROID_HOME` or create `garasi/local.properties`
with `sdk.dir=/path/to/Android/sdk`).

```bash
cd garasi
./gradlew testDebugUnitTest      # unit tests
./gradlew assembleRelease        # → app/build/outputs/apk/release/app-release.apk
```

If no release key is configured, `assembleRelease` signs with your local debug key so the APK
is still installable.

---

## Create a signing key (do this once, keep it forever)

```bash
keytool -genkeypair -v -keystore garasi-release.jks -alias garasi \
  -keyalg RSA -keysize 4096 -validity 10000
```

Pick a strong password and **back the `.jks` file and password up** (password manager + cloud
drive). If you lose it you can't update the app — only uninstall/reinstall.

Local builds: put the keystore in `garasi/` and create `garasi/keystore.properties`
(already git-ignored — never commit it):

```properties
storeFile=garasi-release.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=garasi
keyPassword=YOUR_KEY_PASSWORD
```

GitHub Actions: add these repository secrets (*Settings → Secrets and variables → Actions*):

| Secret | Value |
|---|---|
| `GARASI_KEYSTORE_BASE64` | output of `base64 -w0 garasi-release.jks` (macOS: `base64 -i garasi-release.jks`) |
| `GARASI_KEYSTORE_PASSWORD` | store password |
| `GARASI_KEY_ALIAS` | `garasi` |
| `GARASI_KEY_PASSWORD` | key password |

---

## Sideload onto your phone

1. Copy the `.apk` to the phone (Google Drive, USB, Telegram "Saved Messages", …) or download
   the CI artifact directly on the phone.
2. Tap it. Android asks to allow installs from that app (Files / Chrome / Drive):
   *Settings → Allow from this source*. Go back and tap **Install**.
3. Play Protect may warn about an unknown developer → *More details → Install anyway*.
4. Or with a computer: `adb install -r app-release.apk`.

Updating: install the newer APK on top — works only when both are signed with the same key.

---

## Real-device test checklist

Do these on a real phone (Android 8+), ideally once in light and once in dark mode.

**Setup**
- [ ] First launch shows the empty inventory with a big **+**.
- [ ] Settings: set default listing language, city, a weight preset; switch theme.

**Photos**
- [ ] **+** → Camera: permission prompt appears. Deny → clear message + "Allow camera" (opens app settings on 2nd try).
- [ ] Allow → preview with square guide; tap to focus; take 6 photos; counter updates; ✓ returns.
- [ ] Gallery: pick several photos; an 11th photo is refused with a friendly message.
- [ ] Manage photos: move up/down, make cover (cover = first), crop to square, tag angles, tag flaw close-up, delete.
- [ ] Each photo shows a score after a few seconds; a deliberately blurry / dark shot gets flagged.
- [ ] Files in the app stay < 1 MB (Settings → export backup and check sizes, optional).

**Identify**
- [ ] Photo of a box with a barcode/label → "Identify from photos" suggests brand, model, category → Apply fills fields.

**Text**
- [ ] Build title → starts with brand + model, ≤ 70 chars. Fill from template → `[blanks]` only where details are missing.
- [ ] Switch 🇮🇩/🇬🇧: each language keeps its own text.
- [ ] Ask AI → share sheet shows Claude/Gemini/ChatGPT; app opens with prompt + photos (or paste the copied prompt).
- [ ] Copy the AI's full answer → Paste AI answer → title, description, condition, price range filled.
- [ ] Paste AI answer with an empty clipboard / with the prompt still copied → clear message.
- [ ] Translate → paste answer → the other language is filled and selected.
- [ ] Uninstall all AI apps → Ask AI shows "no AI chat app" message.

**Price**
- [ ] Price check → Tokopedia / Shopee / OLX searches open sorted by price.
- [ ] Enter low/median/high → suggestion = median × factor + 10 %; "Use this price" sets it.

**Quality Check**
- [ ] Score + verdict (Ready ≥ 80 / Good 60–79 / Needs fixing < 60), top 3 fixes, buttons work (retake, crop, make cover, edit field → keyboard opens on that field, price).
- [ ] Condition "Has Flaws" with no flaw photo → reminder.
- [ ] Put a phone number / link / "Anda" / "CHEAP!!!" in the text → flagged.

**Post**
- [ ] Allow "Display over other apps" → Copy & open Tokopedia: photos appear in Gallery album *Garasi/<item>*, title is in the clipboard, marketplace (or website if not installed) opens, floating checklist appears; Copy buttons work; drag & hide work; ✕ closes it.
- [ ] Without overlay permission → checklist shows as a notification instead.
- [ ] Copy both languages → description has Indonesian, divider, English.
- [ ] Mark posted with pasted URL → link opens. Status becomes Posted.
- [ ] Mark sold on one platform → reminder lists the other listings. Stats update (sold, IDR earned, days to sell).

**Backup**
- [ ] Export backup → zip saved. Delete an item → Import → item and photos are back.

---

## Limits & honest notes

- **Marketplace sell-screen deep links are best effort.** Tokopedia/Shopee/OLX don't publish
  stable "add product" links, so Garasi tries known ones, then opens the app's home screen, then
  the website. You may need one tap on "Jual" / "Tambah Produk".
- **Clipboard holds one thing at a time** — that's why the floating checklist has Copy buttons
  for title, description and price.
- Photo quality thresholds (blur, brightness, background) are heuristics tuned for typical
  1080p+ phone photos; they're in `quality/QualityChecker.kt` → `Thresholds` if you want to tweak.
- AI price estimates come from your AI app and can be off; always sanity-check with Price check.
- Some AI apps ignore text when images are shared; the prompt is in the clipboard — just paste it.
