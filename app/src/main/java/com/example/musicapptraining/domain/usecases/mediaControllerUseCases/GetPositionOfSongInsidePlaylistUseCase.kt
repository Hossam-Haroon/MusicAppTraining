package com.example.musicapptraining.domain.usecases.mediaControllerUseCases

import com.example.musicapptraining.domain.repositories.MediaRepository
import javax.inject.Inject

class GetPositionOfSongInsidePlaylistUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(id:String){
        mediaRepository.getPositionOfSongInsidePlaylist(id = id)
    }
}