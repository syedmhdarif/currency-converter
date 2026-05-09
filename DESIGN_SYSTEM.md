# Currency Converter — Design System

A small, opinionated design system for a Material 3 Compose app. Tokens here drive `ui/theme/` Kotlin files; treat this doc as the source of truth and the code as the implementation.

---

## 1. Design principles

1. **Numeric clarity first.** The amount and converted result are the loudest things on screen. Everything else recedes.
2. **One thumb, one screen.** Primary action lives in the bottom 1/3 of the viewport. Avoid layouts that need scrolling for the core flow.
3. **Stateful, not stateless.** Every screen has explicit Loading / Empty / Error states — no silent failures.
4. **Material 3, not Material 2.** Use M3 components (`Button`, `OutlinedTextField`, `ListItem`) and dynamic color where available.
5. **Dark mode is not an afterthought.** Both schemes are designed together; no inverted-light hacks.

---

## 2. Color tokens

Material 3 uses **role-based** colors (primary, surface, error...), not raw palette names. We define a seed and let M3 generate the tonal palette.

**Seed color**: `#0A6EFF` (a confident blue — communicates trust, common for finance UIs).

### Light scheme
| Role | Hex | Usage |
|---|---|---|
| `primary` | `#0A6EFF` | Convert button, focused field outline, key amounts |
| `onPrimary` | `#FFFFFF` | Text/icons on primary |
| `primaryContainer` | `#D9E4FF` | Selected currency chip background |
| `onPrimaryContainer` | `#001A41` | Text on primary container |
| `secondary` | `#565E71` | Less-prominent actions, supporting text |
| `surface` | `#FAFAFB` | Screen background |
| `surfaceContainer` | `#EEF0F4` | Cards, currency picker rows |
| `onSurface` | `#1A1C1E` | Body text, headers |
| `onSurfaceVariant` | `#44474E` | Secondary text, captions |
| `outline` | `#74777F` | Dividers, unfocused field outlines |
| `error` | `#BA1A1A` | Validation, API errors |
| `onError` | `#FFFFFF` | Text on error |

### Dark scheme
| Role | Hex |
|---|---|
| `primary` | `#AEC6FF` |
| `onPrimary` | `#002E6A` |
| `primaryContainer` | `#0049A6` |
| `onPrimaryContainer` | `#D9E4FF` |
| `secondary` | `#BEC6DC` |
| `surface` | `#111316` |
| `surfaceContainer` | `#1D1F23` |
| `onSurface` | `#E3E2E6` |
| `onSurfaceVariant` | `#C4C6CF` |
| `outline` | `#8E9099` |
| `error` | `#FFB4AB` |
| `onError` | `#690005` |

> Use **dynamic color** on Android 12+ (`dynamicLightColorScheme(LocalContext.current)`) when the user has Material You enabled — fall back to the seed-based scheme above otherwise.

---

## 3. Typography

Material 3 type scale, with the **display** sizes promoted for the amount readouts.

| Token | Size / Line height / Weight | Usage |
|---|---|---|
| `displayLarge` | 57 / 64 / 400 | (unused) |
| `displayMedium` | 45 / 52 / 400 | Converted result amount |
| `displaySmall` | 36 / 44 / 400 | Input amount |
| `headlineMedium` | 28 / 36 / 400 | Screen titles |
| `titleLarge` | 22 / 28 / 500 | Card headers, picker title |
| `titleMedium` | 16 / 24 / 500 | Currency code (e.g. "USD") |
| `bodyLarge` | 16 / 24 / 400 | Body text |
| `bodyMedium` | 14 / 20 / 400 | Secondary info, "Last updated 2m ago" |
| `labelLarge` | 14 / 20 / 500 | Buttons |
| `labelMedium` | 12 / 16 / 500 | Captions, currency long names |

**Font family**: System default (Roboto on Android). No custom font for v1 — keeps APK small and rendering crisp. If we want a numeric-tabular feel later, swap to `Roboto Mono` for amount displays only.

---

## 4. Spacing scale

A 4dp grid. Don't invent values outside this scale.

| Token | dp | Common use |
|---|---|---|
| `space.xs` | 4 | Tight icon/text gap |
| `space.sm` | 8 | Padding inside chips |
| `space.md` | 16 | Default screen edge padding, gap between sibling components |
| `space.lg` | 24 | Section separation |
| `space.xl` | 32 | Major section breaks |
| `space.xxl` | 48 | Hero spacing on landing |

**Screen edge padding**: always `16.dp` horizontally on phones.

---

## 5. Elevation & shape

### Elevation (M3 surface tones, not raw shadows)
| Level | dp | Usage |
|---|---|---|
| 0 | 0 | Screen background |
| 1 | 1 | Cards at rest |
| 2 | 3 | Cards on press / FAB |
| 3 | 6 | Bottom sheets |
| 4 | 8 | Modal dialogs |

### Shape (corner radii)
| Token | Radius | Usage |
|---|---|---|
| `shapes.extraSmall` | 4dp | Chips |
| `shapes.small` | 8dp | TextFields, small buttons |
| `shapes.medium` | 12dp | Cards, currency picker rows |
| `shapes.large` | 16dp | Bottom sheets, large buttons |
| `shapes.extraLarge` | 28dp | Hero containers |

---

## 6. Iconography

- **Source**: Material Symbols (via `androidx.compose.material.icons` or `material-symbols-core`).
- **Sizes**: `16.dp` (inline), `24.dp` (default), `32.dp` (toolbar accent), `48.dp` (empty-state hero).
- **Currency flags**: use ISO 4217 code → flag emoji as a starting point (zero asset cost). Upgrade to SVG flags later if needed.

---

## 7. Motion

Material 3 standard easings — **don't roll custom curves for v1**.

| Token | Duration | Usage |
|---|---|---|
| `motion.short` | 100ms | Button press, ripple |
| `motion.medium` | 250ms | Screen transitions, picker open |
| `motion.long` | 400ms | Bottom sheet slide |

Use `AnimatedVisibility` and `animate*AsState` for state-driven motion. Avoid `LaunchedEffect` for animation unless coordinating multiple values.

---

## 8. Component inventory (v1)

These are the only components we need to build the MVP:

1. **`AmountField`** — large numeric input, displays selected currency code as a leading affix. Built on `OutlinedTextField` with `keyboardType = Decimal`.
2. **`CurrencyPickerRow`** — tappable row showing currency code + flag + long name. Used inside the picker bottom sheet.
3. **`CurrencyPickerSheet`** — `ModalBottomSheet` containing a search field + scrollable list of `CurrencyPickerRow`.
4. **`SwapButton`** — circular icon button between source and target currencies; rotates 180° on tap.
5. **`RateInfoStrip`** — small horizontal strip below the result: "1 USD = 4.71 MYR · updated 2m ago".
6. **`ErrorBanner`** — surface-tinted banner for API/network errors with a retry action.
7. **`LoadingShimmer`** — placeholder block for the result while rates fetch.

---

## 9. Accessibility

- Minimum touch target: **48×48dp** (M3 default — most components hit this automatically; verify icon-only buttons).
- Color contrast: tokens above are checked at WCAG AA for normal text. Don't override `onSurface` / `onSurfaceVariant` for "subtle" text — that breaks contrast.
- Every input has a visible label *and* a `contentDescription` for screen readers.
- Support font scaling — never set fixed `sp` values that ignore the user's system text size.
- Test with TalkBack at least once before shipping any new screen.

---

## 10. States every screen must define

For each screen, explicitly design:
- **Loading** — shimmer or progress indicator, never blank.
- **Empty** — friendly illustration/text + a primary action.
- **Error** — what went wrong, what the user can do, retry button.
- **Success** — the happy path.
- **Offline** — cached data with a "showing cached rates from Xm ago" strip.

A screen without all five states is incomplete.
