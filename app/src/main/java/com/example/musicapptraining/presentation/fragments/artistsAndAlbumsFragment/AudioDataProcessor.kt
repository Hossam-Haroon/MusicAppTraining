package com.example.musicapptraining.presentation.fragments.artistsAndAlbumsFragment

import android.net.Uri
import com.bumptech.glide.Glide
import com.example.musicapptraining.databinding.FragmentArtistsAndAlbumsAndPlaylistsBinding
import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.fragments.songsFragment.SongAdapter

class AudioDataProcessor(
    private val binding : FragmentArtistsAndAlbumsAndPlaylistsBinding,
    private val songAdapter: SongAdapter
) {
    fun processAlbumData(album: Album){
        val sortedList = album.albumSongs.sortedByDescending { it.songDateAdded }
        updateAdapterWithAudioList(album.albumSongs.size.toString(),sortedList,album.albumName)
        setGlideForAlbumImage(album)
    }
    fun processArtistData(artist: Artist){
        val sortedList = artist.artistSongs.sortedByDescending { it.songDateAdded }
        updateAdapterWithAudioList(artist.artistSongs.size.toString(),sortedList,artist.artistName)
    }
    fun processPlaylistData(playlist: Playlist){
        val sortedList = playlist.playlistSongs.sortedByDescending { it.songDateAdded }
        updateAdapterWithAudioList(
            playlist.playlistSongs.size.toString(),
            sortedList,
            playlist.playlistName
        )
    }
    private fun updateAdapterWithAudioList(
        songsCount: String,
        songList: List<Song>,
        listName: String
    ){
        binding.songsCountTv.text = songsCount
        songAdapter.submitList(songList)
        binding.name.text = listName
    }
    private fun setGlideForAlbumImage(album: Album) {
        Glide.with(binding.root.context)
            .load(Uri.parse(album.albumArt))
            .into(binding.image)
        binding.name.text = album.albumName
    }
}