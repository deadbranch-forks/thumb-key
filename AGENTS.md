# Repository Guidelines

## Project Structure & Module Organization
- Android app source lives in `app/src/main/java/`, with resources in `app/src/main/res/`.
- Debug-only resources are under `app/src/debug/`.
- Build configuration uses Gradle Kotlin DSL in `build.gradle.kts`, `app/build.gradle.kts`, and `settings.gradle`.
- Release assets and metadata are under `fastlane/metadata/`.

## Build, Test, and Development Commands
- `./gradlew assembleDebug` — build a debug APK.
- `./gradlew lintKotlin` — check Kotlin style with ktlint rules.
- `./gradlew formatKotlin` — auto-format Kotlin sources.
- `prettier -c "*.md" "*.yml"` — verify Markdown/YAML formatting (see `CONTRIBUTING.md`).
- `prettier --write "*.md" "*.yml"` — fix Markdown/YAML formatting.

## Coding Style & Naming Conventions
- Kotlin-only codebase; avoid adding Java classes.
- Follow ktlint’s standard rules; run `./gradlew formatKotlin` before committing.
- Layout names use the pattern described in `CONTRIBUTING.md` (e.g., `language base qualifier version`).
- Keep resources organized by feature: strings in `app/src/main/res/values*/strings.xml`, themes in theme-related Kotlin files.

## Testing Guidelines
- No dedicated test suite is documented; rely on lint/format checks and Android Studio run configurations.
- When adding new functionality, verify behavior with a local debug build (`./gradlew assembleDebug`).

## Commit & Pull Request Guidelines
- Commit messages in history are short, sentence-case summaries and often include a PR reference (e.g., “Fix … (#1234)”).
- Before opening a PR, run formatters (`formatKotlin`, `prettier`) and keep diffs focused.
- PRs should explain the user-visible impact, link related issues, and include screenshots for UI changes.

## Configuration & Security Notes
- The app targets modern Android APIs; use Android Studio (2022.3.1+ suggested) and Java 11+ (Java 17 preferred).
- Keep secrets out of the repo; use local Gradle properties for any developer-specific config.

## Documentation Index
- `dev-docs/emoji-selector.md` — overview of the emoji selector feature, including scope, UI/UX behavior, data sources, and limitations so readers can quickly understand the full breadth of the component.
