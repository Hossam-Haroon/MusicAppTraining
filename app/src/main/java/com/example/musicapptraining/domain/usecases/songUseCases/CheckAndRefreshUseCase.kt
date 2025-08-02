package com.example.musicapptraining.domain.usecases.songUseCases

import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.repositories.SongRepository
import com.example.musicapptraining.utilities.UiState
import javax.inject.Inject

class CheckAndRefreshUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    suspend operator fun invoke(): List<Song> {
        return songRepository.checkAndRefresh()
    }
}