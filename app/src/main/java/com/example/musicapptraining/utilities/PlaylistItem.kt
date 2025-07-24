package com.example.musicapptraining.utilities

import com.example.musicapptraining.data.model.PlayList

sealed class PlaylistItem {
    data class PlaylistContent(val playlist: PlayList) : PlaylistItem()
    data object AddPlaylistButton : PlaylistItem()
}