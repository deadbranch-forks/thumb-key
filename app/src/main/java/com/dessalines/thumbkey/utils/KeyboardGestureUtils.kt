package com.dessalines.thumbkey.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

// Base score (in dp²/s) a swipe must reach at sensitivity 1 to trigger a
// keyboard-wide gesture; the sensitivity setting divides this threshold.
const val GESTURE_SCORE_BASE = 500_000f

/**
 * Detects a keyboard-wide gesture: a cardinal swipe that crosses out of its
 * starting key, fast and/or long enough to reach the sensitivity threshold.
 *
 * Swipe length and lift-off velocity trade off inversely: a short fast flick
 * or a long slower swipe both reach the same score. Decelerating to a stop
 * before lifting cancels the gesture.
 *
 * @param finalOffset The total drag offset at lift-off, in px.
 * @param flingVelocity The lift-off velocity from a VelocityTracker, in px/s.
 * @param keyWidthPx The width of the starting key (including its width multiplier), in px.
 * @param keyHeightPx The height of the starting key, in px.
 * @param density The display density, used to normalize across devices.
 * @param sensitivity How easily the gesture triggers (higher is easier).
 * @param minSwipeLength The minimum swipe length setting.
 * @param gestureActions The configured actions per cardinal direction.
 * @return The triggered action, or null if the swipe doesn't qualify.
 */
fun detectKeyboardWideGesture(
    finalOffset: Offset,
    flingVelocity: Velocity,
    keyWidthPx: Float,
    keyHeightPx: Float,
    density: Float,
    sensitivity: Int,
    minSwipeLength: Int,
    gestureActions: Map<SwipeDirection, KeyAction>,
): KeyAction? {
    if (gestureActions.isEmpty()) return null

    val direction =
        swipeDirection(finalOffset.x, finalOffset.y, minSwipeLength, SwipeNWay.FOUR_WAY_CROSS)
            ?: return null
    val action = gestureActions[direction] ?: return null

    val horizontal = direction == SwipeDirection.LEFT || direction == SwipeDirection.RIGHT
    val axisOffset = if (horizontal) finalOffset.x else finalOffset.y
    val axisVelocity = if (horizontal) flingVelocity.x else flingVelocity.y
    val keyExtentPx = if (horizontal) keyWidthPx else keyHeightPx

    // The swipe must cross out of its starting key, so that normal single-key
    // swipes can never trigger a keyboard-wide gesture.
    if (abs(axisOffset) <= keyExtentPx) return null

    // The finger must still be moving in the swipe direction at lift-off.
    if (axisVelocity * axisOffset <= 0f) return null

    val lengthDp = finalOffset.getDistance() / density
    val velocityDpPerS = abs(axisVelocity) / density
    val score = lengthDp * velocityDpPerS
    return if (score >= GESTURE_SCORE_BASE / sensitivity.coerceAtLeast(1)) action else null
}
