package com.example.musicapptraining.domain.usecases.playlistUseCases

import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.repositories.PlaylistRepository
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLikedPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    operator fun invoke(): Flow<UiState<Playlist>>{
        return playlistRepository.getLikedPlaylist()
    }
}