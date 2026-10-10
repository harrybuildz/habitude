# Habitude

An offline-first habit tracker for Android. Check off habits, keep streaks alive, and get a
nudge in the evening when something is still unchecked.

## Features

- **Today list** — every habit with its emoji, current streak (🔥), best streak, and a one-tap
  check circle for today's completion. Long-press a habit to delete it.
- **Streaks** — a current streak is anchored at today *or* yesterday, so an unchecked today
  doesn't break the streak until the day is actually over. Best streak is tracked independently.
- **Add habits** — name plus an emoji picked from a curated list.
- **Daily reminder** — an optional "Don't break your streaks" notification at a configurable
  hour, listing only the habits that are still unchecked. Runs via WorkManager, so it survives
  reboots and Doze.
- **Dark theme** — follows the system until explicitly toggled; the choice persists.
- **Fully offline** — no network permission, no accounts. Your data stays on the device.

## Architecture

**Offline-first.** Room is the single source of truth. Every screen observes the database
through cold `Flow`s, so UI state is always a projection of persisted data — there is no
in-memory cache to drift out of sync, and the app is indistinguishable online or offline.

**Unidirectional data flow.** Each screen has one ViewModel exposing a single immutable
`StateFlow` built with `stateIn(WhileSubscribed(5_000))`. Events flow down (user intent →
ViewModel → repository → Room), state flows up (Room → Flow → UI). Compose renders state;
it never mutates it.

**Boundaries behind interfaces.** `HabitRepository`, `SettingsRepository`, `DateProvider`,
and `ReminderScheduler` are all interfaces with in-memory fakes in the test source set, so
every ViewModel test runs without Android, Room, or WorkManager.

**Pure domain logic.** `StreakCalculator` is plain Kotlin over epoch days — no clocks, no
Android types — which makes the streak rules (the most bug-prone part of any habit tracker)
exhaustively unit-testable.

**Why a single module?** This app is deliberately one `:app` module with clear package
boundaries (`data`, `domain`, `ui`, `work`, `di`). At this size, Gradle module boundaries
would add build complexity without buying encapsulation that packages don't already provide.
The package structure mirrors the module split the app would get if it grew — see the roadmap.

```
app/src/main/java/com/habitude/
├── data/        Room entities, DAOs, repositories, DataStore settings
├── domain/      StreakCalculator, DateProvider (pure logic)
├── ui/          Compose screens + ViewModels (today, addhabit, settings, theme)
├── work/        ReminderWorker, ReminderScheduler
└── di/          Hilt modules
```

## Tech stack

| Area | Choice |
| --- | --- |
| Language | Kotlin 2.1 |
| UI | Jetpack Compose + Material 3 (dynamic color on Android 12+) |
| Architecture | MVVM, unidirectional data flow, `StateFlow` |
| DI | Hilt |
| Persistence | Room (habits + completions), DataStore Preferences (settings) |
| Background work | WorkManager (`@HiltWorker` + `Configuration.Provider`) |
| Navigation | Navigation Compose |
| Testing | JUnit 4, Turbine, kotlinx-coroutines-test |
| Build | Gradle version catalog, KSP, GitHub Actions CI |

## Getting started

1. Clone the repo and open it in Android Studio (Ladybug or newer), or build from the CLI:

   ```sh
   ./gradlew assembleDebug
   ```

2. Run the unit tests:

   ```sh
   ./gradlew testDebugUnitTest
   ```

Requires JDK 17+ and the Android SDK (compileSdk 35). `local.properties` with your `sdk.dir`
is created automatically by Android Studio. Min SDK is 26.

## Roadmap

- Home screen widget showing today's unchecked habits (Glance)
- Wear OS tile for one-tap check-ins from the wrist
- Per-habit history screen with a calendar heatmap
- Habit schedules (e.g. weekdays only) feeding into the streak rules
- Multi-module split (`:core:data`, `:core:domain`, `:feature:*`) if the feature set grows
- Export/import of the database for device migration
