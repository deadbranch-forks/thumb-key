# Emoji selector feature

## Overview
The emoji selector is an alternate keyboard mode that replaces the key grid with AndroidX’s `EmojiPickerView`. It is entered by tapping a key bound to `ToggleEmojiMode(true)` (commonly the mood icon). While in emoji mode, the UI shows the picker plus a slim column of control keys (back-to-ABC, numeric, backspace, return) sized to match the active keyboard layout. Selecting an emoji commits the emoji character directly to the current `InputConnection`.

## Feature Documents

1. [Emoji Selector Initial Draft Proposal](02-emoji-selector-draft-proposal.md)
   Initially published: *Jan 3, 2025*
   Status: Supersceeded by `Emoji Selector Initial Proposal and Draft Technical Specification`
2. [Emoji Selector Initial Proposal and Draft Technical Specification](03-emoji-selector-technical-specs.md)
   Intially published: *Jan 13, 2025*
   Status: active
