package com.example.musicapptraining.domain.repositories

import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlayLists(): Flow<UiState<List<Playlist>>>
    suspend fun addNewPlayList(playlistName : String)
    suspend fun addSongToPlayList(song : Song, playList: Playlist)
    fun getLikedPlaylist(): Flow<UiState<Playlist>>
    suspend fun deleteSongFromPlayList(song: Song, playList: Playlist)
}