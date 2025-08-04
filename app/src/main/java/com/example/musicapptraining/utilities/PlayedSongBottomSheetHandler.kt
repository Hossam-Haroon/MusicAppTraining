package com.example.musicapptraining.utilities

import com.example.musicapptraining.domain.model.Song

interface PlayedSongBottomSheetHandler {
    fun openPlayedSongBottomSheet(song: Song)
}