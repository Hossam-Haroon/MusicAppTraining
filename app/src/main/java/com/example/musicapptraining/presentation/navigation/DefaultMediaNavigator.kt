package com.example.musicapptraining.presentation.navigation

import androidx.navigation.NavController
import com.example.musicapptraining.presentation.fragments.homeFragment.HomeFragmentDirections

class DefaultMediaNavigator(
    private val navController: NavController
): MediaNavigator {
    override fun openSelectedMedia(navigableMedia: NavigableMedia) {
        when(navigableMedia){
            is NavigableMedia.ArtistMedia -> {
                navigateToArtistsAndAlbumsAndPlaylistsFragmentWithArtistName(navigableMedia.name)
            }
            is NavigableMedia.PlaylistMedia -> {
                navigateToArtistsAndAlbumsAndPlaylistsFragmentWithPlaylistName(navigableMedia.name)
            }
        }
    }
    private fun navigateToArtistsAndAlbumsAndPlaylistsFragmentWithArtistName(artistName: String){
        val action = HomeFragmentDirections
            .actionHomeFragmentToArtistsAndAlbumsAndPlaylistsFragment(
                artistName = artistName,
                albumName = "",
                playListName = ""
            )
        navController.navigate(action)
    }
    private fun navigateToArtistsAndAlbumsAndPlaylistsFragmentWithPlaylistName(playlistName:String){
        val action = HomeFragmentDirections.
        actionHomeFragmentToArtistsAndAlbumsAndPlaylistsFragment(
            artistName = "",
            playListName = playlistName,
            albumName = ""
        )
        navController.navigate(action)
    }
}

