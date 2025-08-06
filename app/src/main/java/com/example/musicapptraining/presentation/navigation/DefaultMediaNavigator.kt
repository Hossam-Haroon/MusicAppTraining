package com.example.musicapptraining.presentation.navigation

import androidx.navigation.NavController
import com.example.musicapptraining.presentation.fragments.homeFragment.HomeFragmentDirections
import com.example.musicapptraining.presentation.fragments.searchMoreButtonFragment.SearchMoreButtonFragmentDirections

class DefaultMediaNavigator(
    private val navController: NavController
): MediaNavigator {
    override fun openSelectedMedia(navigableMedia: NavigableMedia) {
        when(navigableMedia){
            is NavigableMedia.ArtistMediaFromHomeFragment -> {
                navigateToArtistsAndAlbumsAndPlaylistsFragmentWithArtistName(navigableMedia.name)
            }
            is NavigableMedia.PlaylistMediaFromHomeFragment -> {
                navigateToArtistsAndAlbumsAndPlaylistsFragmentWithPlaylistName(navigableMedia.name)
            }
            is NavigableMedia.ArtistMediaFromSearchMoreButtonFragment -> {
                navigateToArtistsAndPlaylistsFragmentFromSearchMoreButtonFragment(
                    navigableMedia.name
                )
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
    private fun navigateToArtistsAndPlaylistsFragmentFromSearchMoreButtonFragment(
        artistName:String
    ){
        val action = SearchMoreButtonFragmentDirections
            .actionSearchMoreButtonFragmentToArtistsAndAlbumsAndPlaylistsFragment(
                artistName,
                "",
                ""
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

