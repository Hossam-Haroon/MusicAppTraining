package com.example.musicapptraining.domain.repositories

import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getAllSongs(): Flow<UiState<List<Song>>>
    fun searchSong(songName : String): Flow<UiState<List<Song>>>
    fun getAlbumSongs(albumName : String): Flow<UiState<Album>>
    fun getArtistSongs(artistName: String): Flow<UiState<Artist>>
    fun getPlaylistSongs(playListSong : String): Flow<UiState<Playlist>>
    suspend fun checkAndRefresh(): UiState<List<Song>>
}