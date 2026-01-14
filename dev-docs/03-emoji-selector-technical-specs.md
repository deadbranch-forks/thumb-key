# Emoji selector integration — initial proposal and draft technical specification

## Summary
Integrate a purpose-built, open source emoji selector with search into Thumb-Key’s emoji mode. The selector will replace the current EmojiPickerView surface with a keyboard-grade emoji UI that supports recent, category navigation, and search, while keeping Thumb-Key’s existing emoji-mode entry/exit flow.

## Goals
- Provide an emoji selector with **first-class search** (query → filtered emojis).
- Preserve existing emoji-mode toggling and commit behavior (emoji selection inserts into `InputConnection`).
- Support recent emojis, skin-tone variants, and category browsing.
- Keep performance acceptable on low-end devices (fast search, small memory footprint).

## Non-goals
- Redesigning the base keyboard layouts or key actions.
- Implementing custom emoji glyphs beyond the provider supplied by the emoji selector library.

## Library selection
### Recommended library: Vanniktech Emoji
**Why**: Vanniktech Emoji is an open source Android emoji keyboard/popup library that explicitly supports search via the `SearchEmoji` interface and has a large star count on GitHub, indicating strong community adoption. The README documents dependency setup and `EmojiPopup` configuration, including the ability to supply custom search behavior and use the default search implementation. The repository has 1,600+ GitHub stars, indicating it is a highly rated, purpose-built emoji component. 

**Evidence**:
- The README documents `EmojiPopup` configuration and a `SearchEmoji` interface for search, with a default search implementation when none is provided. (https://github.com/vanniktech/Emoji/blob/master/README.md)
- Dependency instructions for the AndroidX Emoji2 provider (`emoji-androidx-emoji2`) and `EmojiManager.install(...)` are documented in the README. (https://github.com/vanniktech/Emoji/blob/master/README.md)
- The GitHub API lists the repository’s `stargazers_count` (1,600+), supporting “highly rated” status. (https://api.github.com/repos/vanniktech/Emoji)

## Current system context
Thumb-Key already has an emoji mode driven by `KeyAction.ToggleEmojiMode` and an emoji selector surface in `KeyboardScreen` (see `dev-docs/emoji-selector.md`). This spec extends that flow by swapping the emoji surface with a Vanniktech Emoji-backed selector while keeping the surrounding controller keys, toggle actions, and commit-to-`InputConnection` flow intact.

## User experience
### Entry points
- User taps the emoji key (existing behavior). 
- Keyboard transitions to Emoji mode with the emoji selector and control keys (ABC, numeric, backspace, return) on the side.

### Primary interactions
- **Search**: A search row at the top of the emoji selector allows text query. Results show filtered emoji across categories.
- **Category tabs**: Horizontal tab bar for categories (smileys, animals, food, etc.).
- **Recents**: First tab shows recently used emojis.
- **Skin tone variants**: Long-press or tap variant indicator to select tone.

### Exit points
- “ABC” back key exits emoji mode and returns to standard keyboard.

## Architecture changes
### High-level component map
- **Emoji selector surface**: Replace `EmojiPickerView` with Vanniktech Emoji’s popup/keyboard view embedded in emoji mode UI.
- **Emoji data provider**: Install `GoogleCompatEmojiProvider` (Emoji2) or a configurable provider from Vanniktech Emoji in the app’s `Application` class.
- **Search integration**: Implement a search bar and hook it into Vanniktech’s `SearchEmoji` interface (default or custom).

### Modules & files
- `app/src/main/java/.../ui/components/keyboard/KeyboardScreen.kt`
  - Replace EmojiPickerView AndroidView with a Vanniktech Emoji view container.
  - Add composable search bar in emoji mode (input → search).
- `app/src/main/java/.../App.kt` (or app `Application` class)
  - Initialize `EmojiManager.install(...)` with a provider.
- `app/src/main/java/.../settings/EmojiSettings.kt` (new)
  - Persist default skin tone and search behavior preferences (optional).
- `app/src/main/res/values/strings.xml`
  - Add strings for search hint and accessibility labels.

## Detailed design
### UI layout (emoji mode)
```
+--------------------------------------------------+
| Search field (query, clear button)               |
+--------------------------------------------------+
| Category tabs (recents, smileys, animals, etc.)  |
+--------------------------------------------------+
| Emoji grid (filtered by search query)            |
|                                                  |
|                                                  |
+--------------------------------------------------+
| (Right-side control keys column)                 |
| ABC | 123 | Backspace | Enter | ...              |
+--------------------------------------------------+
```

### Emoji picker integration
- Use Vanniktech Emoji’s keyboard/popup view to render the emoji grid and category tabs.
- Configure `EmojiPopup` to listen for emoji clicks and commit the selected emoji to `InputConnection`.
- Wire recents and variants using Vanniktech’s default managers unless custom behavior is required.

### Search behavior
- Add a search field in the emoji mode header.
- On query update:
  - Call Vanniktech’s `SearchEmoji` implementation (default `SearchEmojiManager` or custom).
  - Update the emoji grid dataset to show results.
- If the search query is empty, restore category-based browsing.

### Data flow
1. User taps emoji key → `ToggleEmojiMode(true)`.
2. Emoji mode composes the Vanniktech Emoji view and search bar.
3. Emoji view uses installed `EmojiProvider` (Emoji2).
4. User types in search → `SearchEmoji.search(query)` → results list.
5. User taps emoji → `onEmojiClickListener` commits emoji to `InputConnection`.
6. Recent emojis updated via Vanniktech `RecentEmoji` manager.

## Implementation steps
1. **Dependency setup**
   - Add Vanniktech Emoji and `emoji-androidx-emoji2` provider dependencies to `app/build.gradle.kts`.
   - Ensure Emoji2 is initialized in the application class.
2. **Emoji provider initialization**
   - Initialize `EmojiManager.install(GoogleCompatEmojiProvider(EmojiCompat.init(this)))` in the app’s `Application`.
3. **Emoji selector surface**
   - Replace `EmojiPickerView` in `KeyboardScreen` with a Vanniktech Emoji view or popup embedded in Compose via `AndroidView`.
   - Add `onEmojiClickListener` to commit to `InputConnection`.
4. **Search UI**
   - Add a Compose `TextField` at the top of emoji mode.
   - Connect to `SearchEmoji` (default `SearchEmojiManager`) for query → results.
5. **Recents & variants**
   - Use Vanniktech defaults; optionally integrate app settings for sticky variants.
6. **Telemetry (optional)**
   - Track search usage and emoji selection counts (if analytics exists).

## Performance considerations
- Cache last search results for immediate backspacing.
- Debounce search input (e.g., 150–250ms) to avoid frequent filtering.
- Avoid blocking UI thread; perform search in a background dispatcher if needed.

## Accessibility
- Search field must expose hint text and clear button with content description.
- Emoji items should be accessible via TalkBack (Vanniktech provides content descriptions where possible).
- Ensure category tabs have accessible labels.

## Risk assessment
| Risk | Impact | Mitigation |
|------|--------|------------|
| Increased APK size | Medium | Use `emoji-androidx-emoji2` provider only; avoid extra emoji sets. |
| Search latency on low-end devices | Medium | Debounce input and cache results. |
| UI mismatch with keyboard style | Low | Customize styles to match existing theme. |

## Rollout plan
- Feature flag: `emojiSwitcherEnabled` in settings or remote config.
- Beta rollout: enable for debug builds first.
- Gradual release: enable for 10% of users if analytics indicates acceptable performance.

## Open questions
- Should emoji search be localized (language-specific keywords)?
- Should search work across custom keyboards (e.g., Toki Pona emoji)?
- Do we need a per-layout emoji selector toggle or global?
