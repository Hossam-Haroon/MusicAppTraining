package com.example.musicapptraining.domain.repositories

import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    fun getArtists(): Flow<List<Artist>>
    fun searchArtistByName(text : String): Flow<List<Artist>>
    //suspend fun insertArtist(artistName: String)

}