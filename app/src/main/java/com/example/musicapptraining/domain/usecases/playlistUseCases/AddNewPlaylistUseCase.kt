package com.example.musicapptraining.domain.usecases.playlistUseCases

import com.example.musicapptraining.domain.repositories.PlaylistRepository
import javax.inject.Inject

class AddNewPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistName:String){
         playlistRepository.addNewPlayList(playlistName)
    }
}