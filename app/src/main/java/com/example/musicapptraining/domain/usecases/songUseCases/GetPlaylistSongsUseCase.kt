package com.example.musicapptraining.domain.usecases.songUseCases

import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.repositories.SongRepository
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaylistSongsUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(playlistName : String): Flow<UiState<Playlist>>{
        return songRepository.getPlaylistSongs(playlistName)
    }
}