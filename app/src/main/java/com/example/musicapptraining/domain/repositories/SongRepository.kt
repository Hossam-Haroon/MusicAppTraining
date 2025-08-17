package com.example.musicapptraining.domain.repositories

import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>
    fun searchSong(songName : String): Flow<List<Song>>
    suspend fun checkAndRefresh(): List<Song>
}