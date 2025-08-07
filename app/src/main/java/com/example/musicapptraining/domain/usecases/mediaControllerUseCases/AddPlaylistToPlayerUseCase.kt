package com.example.musicapptraining.domain.usecases.mediaControllerUseCases

import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.repositories.MediaRepository
import javax.inject.Inject

class AddPlaylistToPlayerUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(songs: List<Song>) = mediaRepository.addPlaylist(songs)
}