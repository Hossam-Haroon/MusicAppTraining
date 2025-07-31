package com.example.musicapptraining.domain.usecases.playlistUseCases

import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.repositories.PlaylistRepository
import javax.inject.Inject

class AddSongToPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(song: Song, playlist: Playlist){
        playlistRepository.addSongToPlayList(song,playlist)
    }
}