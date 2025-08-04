package com.example.musicapptraining.presentation.navigation

sealed interface NavigableMedia {
    data class ArtistMedia(val name:String): NavigableMedia
    data class PlaylistMedia(val name:String): NavigableMedia
}