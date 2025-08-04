package com.example.musicapptraining.utilities

import com.example.musicapptraining.domain.model.Song

interface MoreButtonBottomSheetHandler {
    fun openMoreButtonBottomSheet(song: Song)
}