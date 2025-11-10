package com.example.agriscan.presentation.tutorial

/**
 * Simple state holder for the tutorial screen.
 * Add more fields as you need (e.g., playbackPosition, isMuted, captionsEnabled).
 */
data class TutorialState(
    val youtubeVideoId: String = "8of5w7RgcTc",
    val isPlaying: Boolean = false
)
