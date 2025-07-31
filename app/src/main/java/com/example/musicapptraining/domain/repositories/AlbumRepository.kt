package com.example.musicapptraining.domain.repositories

import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAllAlbums(): Flow<UiState<List<Album>>>
    fun searchAlbumByName(text : String): Flow<UiState<List<Album>>>
}