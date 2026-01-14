# Emoji selector feature draft proposal

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


