# Emoji selector feature

## Overview
The emoji selector is an alternate keyboard mode that replaces the key grid with AndroidX’s `EmojiPickerView`. It is entered by tapping a key bound to `ToggleEmojiMode(true)` (commonly the mood icon). While in emoji mode, the UI shows the picker plus a slim column of control keys (back-to-ABC, numeric, backspace, return) sized to match the active keyboard layout. Selecting an emoji commits the emoji character directly to the current `InputConnection`.

## User flow
1. User taps an emoji toggle key (mood icon) on any keyboard layout.
2. The toggle key emits `KeyAction.ToggleEmojiMode(true)`, which is handled by `performKeyAction`.
3. The app calls `TextProcessor.handleFinishInput` (if a processor is active) and switches `KeyboardScreen` into `KeyboardMode.EMOJI`.
4. `KeyboardScreen` renders an `EmojiPickerView` alongside controller keys sized based on the number of main keyboard rows.
5. When the user picks an emoji, the picker commits the emoji to the current input connection, with optional haptic and audio feedback.
6. The user exits emoji mode via the “ABC” back key (`ToggleEmojiMode(false)`), returning to the main layout.

## Location map (code & resources)
- Emoji mode UI and picker integration: `app/src/main/java/com/dessalines/thumbkey/ui/components/keyboard/KeyboardScreen.kt` (`KeyboardScreen`, `KeyboardMode.EMOJI`, `EmojiPickerView`, controller key sizing).
- Key action dispatch and emoji-mode toggle handling: `app/src/main/java/com/dessalines/thumbkey/utils/Utils.kt` (`performKeyAction`, `KeyAction.ToggleEmojiMode`).
- Emoji-mode action types: `app/src/main/java/com/dessalines/thumbkey/utils/Types.kt` (`KeyAction.ToggleEmojiMode`).
- Emoji toggle key definitions: `app/src/main/java/com/dessalines/thumbkey/keyboards/CommonKeys.kt` (`EMOJI_KEY_ITEM`, `EMOJI_BACK_KEY_ITEM`, `TOGGLE_EMOJI_MODE_TRUE_KEYC`, `TOGGLE_EMOJI_MODE_FALSE_KEYC`).
- Emoji-specific layout (Toki Pona sitelen emoji keyboard): `app/src/main/java/com/dessalines/thumbkey/keyboards/TOKSitelenThumbkeyEmoji.kt` (`KB_TOK_SITELEN_THUMBKEY_EMOJI`).
- Emoji keyboard layout registration: `app/src/main/java/com/dessalines/thumbkey/utils/KeyboardLayout.kt` (`TOKSitelenThumbKeyEmoji` enum entry).
- Text processor hook used when toggling emoji mode: `app/src/main/java/com/dessalines/thumbkey/textprocessors/TextProcessor.kt` (`handleFinishInput`).

## Entity-object analysis
### UI/feature entities
- **Emoji selector surface** (`EmojiPickerView`)
  - **Role:** Renders the emoji grid UI.
  - **Owner:** `KeyboardScreen` in `KeyboardMode.EMOJI`.
  - **Lifecycle:** Created in `AndroidView` factory; listener commits emoji to `InputConnection`.
  - **Data:** Emits `it.emoji` from picker callback.

- **Emoji mode keyboard surface** (`KeyboardScreen` with `KeyboardMode.EMOJI`)
  - **Role:** Switches rendering between standard keyboard rows and emoji selector UI.
  - **State:** `mode` mutable state drives the mode selection.
  - **Dependencies:** Uses `KeyboardDefinition` for sizing; uses `AppSettings` for haptics/sound and layout sizing.

- **Emoji toggle key** (`EMOJI_KEY_ITEM` / `EMOJI_BACK_KEY_ITEM`)
  - **Role:** Entry/exit points for emoji mode.
  - **Action payload:** `ToggleEmojiMode(true|false)`.
  - **Placement:** Included in many keyboard layout definitions; special emoji keyboard provides its own entry.

### Domain entities
- **Key action** (`KeyAction.ToggleEmojiMode`)
  - **Role:** Command object indicating a change to emoji mode.
  - **Handler:** `performKeyAction` performs logging, calls `TextProcessor.handleFinishInput`, then updates mode via callback.

- **Text processor** (`TextProcessor`)
  - **Role:** Optional pre/post-processing hooks for text input.
  - **Emoji mode tie-in:** `handleFinishInput` is invoked before switching modes, allowing processors to flush or finalize state.

- **Keyboard layout definition** (`KeyboardDefinition`, `KeyboardLayout`)
  - **Role:** Supplies layout data and metadata; `KeyboardLayout` contains emoji-specific layout registration (`TOKSitelenThumbKeyEmoji`).
  - **Emoji mode tie-in:** `KeyboardScreen` uses the main layout row count to decide how many controller keys are shown alongside the emoji picker.

### Data flow summary
1. **Tap → KeyAction:** User taps emoji key → `KeyAction.ToggleEmojiMode(true)`.
2. **KeyAction → Mode change:** `performKeyAction` handles the toggle, calls `TextProcessor.handleFinishInput`, triggers `onToggleEmojiMode`.
3. **Mode change → UI:** `KeyboardScreen` switches to emoji UI, constructs controller key column based on row count.
4. **Emoji pick → Commit:** `EmojiPickerView` listener commits `emoji` to `InputConnection` and applies haptic/audio feedback.

## Behavior details worth noting
- The emoji UI is backed by AndroidX `EmojiPickerView` rather than a custom Compose list, and it commits directly to the active `InputConnection`.
- Controller key count adapts to the number of rows in the main keyboard layout to keep the emoji picker height aligned.
- Emoji mode and numeric/shift/alt/ctrl modes are mutually exclusive because the `mode` state in `KeyboardScreen` is set directly when toggling.

