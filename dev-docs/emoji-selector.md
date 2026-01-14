# Emoji selector feature

## Overview
The emoji selector is an alternate keyboard mode that replaces the key grid with AndroidX’s `EmojiPickerView`. It is entered by tapping a key bound to `ToggleEmojiMode(true)` (commonly the mood icon). While in emoji mode, the UI shows the picker plus a slim column of control keys (back-to-ABC, numeric, backspace, return) sized to match the active keyboard layout. Selecting an emoji commits the emoji character directly to the current `InputConnection`.

## Technical Documents

1. *Jan 3, 2025* - [Draft proposal](02-emoji-selector-draft-proposal.md)
2.  *Jan 13, 2025* - [Emoji Selector Proposal and Technical Spec](03-emoji-selector-technical-specs.md)
