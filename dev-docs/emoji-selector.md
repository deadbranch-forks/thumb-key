# Emoji selector feature

## Overview
The emoji selector is an alternate keyboard mode that replaces the key grid with AndroidX’s `EmojiPickerView`. It is entered by tapping a key bound to `ToggleEmojiMode(true)` (commonly the mood icon). While in emoji mode, the UI shows the picker plus a slim column of control keys (back-to-ABC, numeric, backspace, return) sized to match the active keyboard layout. Selecting an emoji commits the emoji character directly to the current `InputConnection`.

## Technical Documents

1. [Draft proposal](emoji-selector-draft-proposal.md) - *created Jan 3, 2025*
2.  [Emoji Selectar Proposal and Technical Spec](emoji-selector-technical-spec.md) - *created Jan 13, 2025*
