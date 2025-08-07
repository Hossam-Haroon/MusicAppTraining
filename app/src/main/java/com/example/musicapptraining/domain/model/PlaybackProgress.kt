package com.example.musicapptraining.domain.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PlaybackProgress(
    val currentMediaProgressInMs: Long = 0L,
    val currentMediaDurationInMs: Long = 0L
)

