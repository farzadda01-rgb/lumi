# LUMI — Premium Appointment Booking Android Application

> "Your time. Your ritual."

LUMI is a native Android appointment booking application for luxury beauty, wellness, and personal-care services. Engineered entirely in Kotlin with Jetpack Compose Material 3, LUMI exemplifies digital product craftsmanship, calm editorial design, and architectural excellence.

---

## Key Features

1. **Brand Identity & Editorial Aesthetic**:
   - Palette: Deep Plum (`#4B315D`), Muted Lavender (`#A996C4`), Soft Coral (`#E9A38F`), Warm Ivory (`#FAF8F5`).
   - Bundled contemporary sans-serif typeface: **Manrope** (`res/font/manrope.ttf`).
   - 4dp spacing grid, rounded corner tokens (12dp chips, 14dp inputs, 20dp cards, 24dp hero banners, 16dp buttons).

2. **Core Flows**:
   - **Splash Screen**: Brand wordmark, lotus light emblem, tagline with subtle fade-in.
   - **Onboarding (3-step Pager)**: HorizontalPager with step indicators, "Skip", "Back", "Next", and "Get started" persisting completion to DataStore.
   - **Home Screen**: Time-aware greeting ("GOOD MORNING / AFTERNOON / EVENING"), user avatar with initials, notification bell with unread indicator, hero sanctuary card, search input, horizontal category chips (Hair, Nails, Skin, Massage, Brows, Wellness), compact upcoming appointment card, and popular service cards.
   - **Explore Screen**: Live search across service and provider names, category filters, sorting (Recommended, Highest rated, Price low-high, Price high-low), results counter, and empty states.
   - **Service Details**: Hero photography, category pill, inclusions checklist, specialist summary card, location info, cancellation policy, and sticky bottom booking bar.
   - **Specialist Selection**: View and select available specialists (Olivia Bennett, Marcus Thorne, Elena Rostova, Chloe Vance) with bios, ratings, and experience.
   - **Date & Time Scheduling**: 14-day horizontal date selector, responsive time slots grid (morning & afternoon), past-time validation, and occupied slot blocking.
   - **Booking Summary & Review**: Price breakdown, optional customer notes with character counter, policy review, and confirmation CTA.
   - **Booking Confirmation**: Animated checkmark, unique human-readable reference code (e.g. `LUMI-8492`), details summary, and direct links to Bookings and Home.
   - **My Bookings Management**: Upcoming and Past tabs, status badges (Confirmed, Completed, Cancelled), and local cancellation dialog.
   - **Profile & Edit Profile**: Personal information management with inline email format validation and length limits, dark appearance toggle via DataStore, language display, and legal dialogues.
   - **Saved Favorites**: Local favorite persistence synchronized across Home, Explore, and Details.
   - **Notification Center**: In-app notice list with unread markers and "Mark all read" action.

3. **Architecture & Tech Stack**:
   - **UI Framework**: Jetpack Compose, Material 3.
   - **Language**: 100% Kotlin with Coroutines & StateFlow.
   - **Architecture**: MVVM with Repository Pattern.
   - **Local Persistence**: Room Database (`LumiDatabase`) with entities (`services`, `providers`, `appointments`, `favorites`, `notifications`, `user_profile`).
   - **Lightweight Preferences**: Jetpack DataStore Preferences (`onboarding_completed`, `dark_mode`).
   - **Edge-to-Edge**: `enableEdgeToEdge()` and window insets safe padding.

---

## Build & Test Instructions

### Requirements
- Android SDK 26+ (target SDK 36)
- Java 11 / 17
- Gradle 8+

### Compile the Application
```bash
gradle assembleDebug
```

### Run Automated Tests
```bash
gradle :app:testDebugUnitTest
```
Tests cover:
- CurrencyFormatter (cents vs dollar calculations, formatting)
- DateTimeUtils (date formatting, time slot generation)
- Service inclusions and filtering/sorting logic
- Room database seeding and reactive Flow collection
- Booking validation (past slot rejection, duplicate slot prevention)
- Appointment cancellation status transition
- String resource verification
