package com.example.musicapptraining.domain.usecases.mediaControllerUseCases

import com.example.musicapptraining.domain.repositories.MediaRepository
import javax.inject.Inject

class ToggleRepeatUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke() = mediaRepository.toggleRepeat()
}