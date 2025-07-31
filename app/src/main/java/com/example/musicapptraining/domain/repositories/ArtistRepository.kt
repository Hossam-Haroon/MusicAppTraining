package com.example.musicapptraining.domain.repositories

import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    fun getArtists(): Flow<UiState<List<Artist>>>
    fun searchArtistByName(text : String): Flow<UiState<List<Artist>>>
    //suspend fun insertArtist(artistName: String)

}