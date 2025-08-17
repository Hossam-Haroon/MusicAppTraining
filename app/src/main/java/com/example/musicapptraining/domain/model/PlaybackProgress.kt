package com.example.musicapptraining.domain.model



data class PlaybackProgress(
    val currentMediaProgressInMs: Long = 0L,
    val currentMediaDurationInMs: Long = 0L
)

