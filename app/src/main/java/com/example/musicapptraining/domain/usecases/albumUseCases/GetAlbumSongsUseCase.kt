package com.example.musicapptraining.domain.usecases.albumUseCases

import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.repositories.AlbumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumSongsUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(albumName:String): Flow<Album> {
        return albumRepository.getAlbumSongs(albumName)
    }
}