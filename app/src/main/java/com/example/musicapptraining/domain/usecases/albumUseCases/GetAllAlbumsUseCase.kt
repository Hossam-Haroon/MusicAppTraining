package com.example.musicapptraining.domain.usecases.albumUseCases

import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.repositories.AlbumRepository
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAlbumsUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(): Flow<UiState<List<Album>>> {
        return albumRepository.getAllAlbums()
    }
}