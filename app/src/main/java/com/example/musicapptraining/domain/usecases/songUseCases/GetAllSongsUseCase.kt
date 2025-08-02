package com.example.musicapptraining.domain.usecases.songUseCases

import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.repositories.SongRepository
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
    private val songRepository: SongRepository
){
    operator fun invoke(): Flow<List<Song>> {
        return songRepository.getAllSongs()
    }
}