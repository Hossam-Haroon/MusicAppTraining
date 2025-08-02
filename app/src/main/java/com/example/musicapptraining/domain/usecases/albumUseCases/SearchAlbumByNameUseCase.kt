package com.example.musicapptraining.domain.usecases.albumUseCases

import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.repositories.AlbumRepository
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchAlbumByNameUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(albumName:String): Flow<List<Album>>{
        return albumRepository.searchAlbumByName(albumName)
    }
}