package com.example.musicapptraining.domain.model

data class PlaybackState(
    val isPlaying : Boolean = false,
    val currentMediaPositionInList:Int = 0,
    val isRepeatingClicked : Boolean = false,
    val isShufflingClicked : Boolean = false
)

