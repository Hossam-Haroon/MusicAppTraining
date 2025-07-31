package com.example.musicapptraining.utilities

import com.example.musicapptraining.domain.model.Playlist

sealed class PlaylistItem {
    data class PlaylistContent(val playlist: Playlist) : PlaylistItem()
    data object AddPlaylistButton : PlaylistItem()
}