# Currency Converter — Project Plan

Reference doc capturing every decision made during planning. Read this before resuming work.

---

## Goal

Build a simple Android Currency Converter app as a learning project for Kotlin and modern Android development.

**Scope (v1)**: pick a source currency, pick a target currency, enter an amount, see the converted result. That's it.

**Explicitly out of scope** (do not build, do not plan for):
- Rate history charts
- Favorites / starred currencies
- Conversion history log
- Multi-currency comparison view
- Widgets, notifications, share sheets
- Account / login / sync

If the urge to "just add" any of these strikes mid-build — resist. Ship v1 first.

---

## Decisions (locked)

| # | Decision | Choice | Why |
|---|---|---|---|
| 1 | Language | Kotlin | Android's first-class language |
| 2 | UI toolkit | **Jetpack Compose** | Google's recommended toolkit; declarative model transfers to SwiftUI later |
| 3 | Architecture | MVVM + Unidirectional Data Flow | Standard, beginner-friendly, well-documented |
| 4 | State holder | `StateFlow<UiState>` in ViewModel | Lifecycle-safe, idiomatic with Compose |
| 5 | Activity model | Single Activity (`MainActivity`) hosting Compose | Modern norm |
| 6 | Navigation | `androidx.navigation:navigation-compose` | Compose-native nav |
| 7 | DI | Hilt | Google's recommended DI for Android |
| 8 | Networking | Retrofit + OkHttp + kotlinx.serialization | Industry standard combo |
| 9 | Async | Kotlin Coroutines + Flow | Native to Kotlin |
| 10 | Local storage | DataStore (Preferences) | Lightweight cache for rates + last-used currencies (no Room — no relational data) |
| 11 | Currency API | **Frankfurter** (`api.frankfurter.dev`) | Free, no API key, ECB-sourced |
| 12 | Image loading | Coil 3 | Compose-first; only needed if we add SVG flags later. Skip for v1 (use flag emoji). |
| 13 | Min SDK | 26 (Android 8.0) | Covers ~98% of devices; no business case to go lower |
| 14 | Compile/Target SDK | 36 | Already configured |
| 15 | JDK target | 17 | Bundled with Android Studio |
| 16 | Build system | Gradle Kotlin DSL (`.kts`) | Already configured |
| 17 | Version control | Git | Project not yet a git repo — initialize before coding |

---

## Architecture

```
┌─────────────────────────────────────┐
│  Compose UI (ConverterScreen)       │
│  - observes UiState via StateFlow   │
│  - emits events via lambdas         │
└──────────────▲──────────────────────┘
               │ state ↑    events ↓
┌──────────────┴──────────────────────┐
│  ConverterViewModel                 │
│  - holds StateFlow<ConverterUiState>│
│  - launches coroutines              │
└──────────────▲──────────────────────┘
               │
┌──────────────┴──────────────────────┐
│  CurrencyRepository (interface)     │
│  - single source of truth           │
│  - returns Flow / suspend fun       │
└──┬─────────────────────────────┬────┘
   │                             │
┌──▼──────────────┐    ┌─────────▼──────┐
│  CurrencyApi    │    │  RatesCache    │
│  (Retrofit)     │    │  (DataStore)   │
│  Frankfurter    │    │  last rates +  │
│                 │    │  user prefs    │
└─────────────────┘    └────────────────┘
```

**Layers**:
- `ui/` — Compose screens, ViewModels, UI state classes
- `domain/` — pure Kotlin models (`Currency`, `ConversionRate`)
- `data/` — repository, remote (Retrofit), local (DataStore)
- `di/` — Hilt modules

**State pattern**: one sealed `UiState` per screen with `Loading` / `Success(data)` / `Error(message)` variants. UI is a pure function of state.

---

## File structure

```
app/src/main/java/com/example/currencyconverter/
├── CurrencyConverterApp.kt          # @HiltAndroidApp Application class
├── MainActivity.kt                   # @AndroidEntryPoint, hosts AppNavHost
├── di/
│   └── NetworkModule.kt              # provides Retrofit, OkHttp, CurrencyApi
├── data/
│   ├── remote/
│   │   ├── CurrencyApi.kt            # Retrofit interface
│   │   └── dto/
│   │       └── RatesDto.kt           # @Serializable DTOs
│   ├── local/
│   │   └── RatesCache.kt             # DataStore wrapper
│   └── CurrencyRepository.kt         # interface + impl
├── domain/
│   └── model/
│       ├── Currency.kt               # data class: code, name, flag
│       └── ConversionRate.kt
└── ui/
    ├── theme/
    │   ├── Color.kt                  # tokens from DESIGN_SYSTEM.md
    │   ├── Type.kt
    │   ├── Shape.kt
    │   └── Theme.kt                  # AppTheme composable
    ├── components/
    │   ├── AmountField.kt
    │   ├── CurrencyPickerSheet.kt
    │   ├── SwapButton.kt
    │   └── RateInfoStrip.kt
    ├── converter/
    │   ├── ConverterScreen.kt
    │   ├── ConverterViewModel.kt
    │   └── ConverterUiState.kt
    └── nav/
        └── AppNavHost.kt
```

For v1 there's only one screen — `ConverterScreen`. NavHost is set up to make adding screens later trivial, but routes are minimal.

---

## API: Frankfurter

- **Base URL**: `https://api.frankfurter.dev/v1/`
- **No API key required**.
- **Endpoints we'll use**:
  - `GET /currencies` — returns map of `{ "USD": "United States Dollar", ... }`. Call once on first launch, cache.
  - `GET /latest?base=USD&symbols=MYR` — returns latest rate. Call when source/target/amount changes.
- **Caching strategy**: cache the latest rates response in DataStore with a timestamp. Treat as fresh for 60 minutes. Show "showing cached rates from Xm ago" if offline and cache is stale.

---

## Dependencies to add

To `gradle/libs.versions.toml` (version catalog) and `app/build.gradle.kts`:

```
# Compose BOM (controls all compose versions together)
androidx.compose:compose-bom

# Compose
androidx.activity:activity-compose
androidx.compose.ui:ui
androidx.compose.material3:material3
androidx.compose.material:material-icons-extended
androidx.compose.ui:ui-tooling-preview
androidx.compose.ui:ui-tooling           (debug only)

# Lifecycle + ViewModel + Compose
androidx.lifecycle:lifecycle-viewmodel-compose
androidx.lifecycle:lifecycle-runtime-compose

# Navigation
androidx.navigation:navigation-compose
androidx.hilt:hilt-navigation-compose

# Hilt
com.google.dagger:hilt-android
com.google.dagger:hilt-compiler           (ksp)

# Networking
com.squareup.retrofit2:retrofit
com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter
com.squareup.okhttp3:okhttp
com.squareup.okhttp3:logging-interceptor
org.jetbrains.kotlinx:kotlinx-serialization-json

# DataStore
androidx.datastore:datastore-preferences

# Tests
junit:junit
io.mockk:mockk
org.jetbrains.kotlinx:kotlinx-coroutines-test
app.cash.turbine:turbine
```

Plugins to add:
- `org.jetbrains.kotlin.plugin.serialization`
- `com.google.devtools.ksp`
- `com.google.dagger.hilt.android`
- `org.jetbrains.kotlin.plugin.compose` (Kotlin 2.0+ Compose compiler)

`buildFeatures { compose = true }` and **remove** `viewBinding = true` (we won't need it).

---

## Build order (when we start coding)

Each step is small and testable on its own. Don't move to the next until the current one runs on a device.

1. ✅ **Git init** + initial commit of the current scaffold.
2. ✅ **Migrate Gradle to Compose**: version catalog, plugins, BOM, dropped ViewBinding, JVM 17.
3. ✅ **Delete legacy scaffold**: fragments, XML layouts, navigation, menu, dimens.
4. ✅ **Theme module**: `ui/theme/` (Color, Type, Shape, Spacing, Theme.kt) — tokens from DESIGN_SYSTEM.md.
5. ✅ **Hello-Compose smoke test**: `MainActivity` renders `ConverterScreen` stub. `assembleDebug` passes.
6. ✅ **Domain models**: `Currency`, `ConversionRate`.
7. ✅ **Networking layer**: Retrofit interface, DTOs, NetworkModule (Hilt). Verified live against Frankfurter on emulator.
8. ✅ **DataStore cache**: `RatesCache` (currencies + last rate + last selection persisted as JSON).
9. ✅ **Repository**: `CurrencyRepository` cache-or-fetch for currencies, fetch-and-cache for rates.
10. ✅ **ViewModel**: `ConverterViewModel` `@HiltViewModel` with Loading/Idle/Error phases, debounced via Job cancellation.
11. ✅ **Wire UI to ViewModel**: amount field + From/To dropdowns + swap button + result card (real `1 USD = 3.921 MYR` confirmed on device).
12. **Components polish**: extract `AmountField`, `CurrencyPickerSheet`, `SwapButton`, `RateInfoStrip` (currently inline in `ConverterScreen.kt`). Skipped for v1 — works as-is.
13. **States**: explicitly design and implement Loading / Error / Offline / Empty (per design system §10).
14. **Accessibility pass**: TalkBack walkthrough, font scale 200% test.
15. **Release config**: enable R8/minification, set up signing, generate release APK.

---

## What's installed / configured already

- ✅ Android Studio (project opens)
- ✅ Android SDK 36, JDK 17, Gradle 8.13, AGP 8.13.2, Kotlin 2.0.21
- ✅ Git initialized, pushed to `github.com:syedmhdarif/currency-converter`
- ✅ Compose, Hilt, Retrofit, OkHttp, kotlinx.serialization, DataStore, Coroutines (configured in `libs.versions.toml`)
- ✅ KSP + Hilt + Compose compiler + serialization plugins applied

---

## Reference docs

- [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md) — colors, typography, spacing, components
- This file — decisions, architecture, build order
- Frankfurter API: https://frankfurter.dev/
- Compose docs: https://developer.android.com/jetpack/compose
- Hilt + Compose: https://developer.android.com/training/dependency-injection/hilt-jetpack

---

## When in doubt

- Adding a feature not in "Goal"? **Don't.** Note it as a v2 idea instead.
- Adding a library not in the "Dependencies" list? **Stop, justify it, then update this doc.**
- Touching architecture? **Update the diagram in this file** before writing code.
