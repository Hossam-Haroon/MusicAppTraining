package com.example.musicapptraining.presentation.navigation

sealed interface NavigableMedia {
    data class ArtistMediaFromHomeFragment(val name:String): NavigableMedia
    data class ArtistMediaFromSearchMoreButtonFragment(val name:String): NavigableMedia
    data class PlaylistMediaFromHomeFragment(val name:String): NavigableMedia
}