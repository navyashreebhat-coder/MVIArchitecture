package com.example.mviarchitectureapp.mvi

/**
 * Marker for user intents / events that drive state changes.
 */
interface MviIntent

/**
 * Immutable UI state rendered by Compose.
 */
interface MviState

/**
 * One-shot side effects (navigation, snackbars, etc.).
 */
interface MviEffect
