package com.example.musicapptraining.domain.usecases.mediaControllerUseCases

import android.util.Log
import com.example.musicapptraining.domain.repositories.MediaRepository
import javax.inject.Inject

class CycleShuffleRepeatUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(){
        mediaRepository.cycleShuffleRepeat()
    }
}