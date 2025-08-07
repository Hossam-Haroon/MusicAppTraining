package com.example.musicapptraining.data.repositories

import android.util.Log
import com.example.musicapptraining.data.mediaController.MediaControllerManager
import com.example.musicapptraining.domain.model.PlaybackProgress
import com.example.musicapptraining.domain.model.PlaybackState
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.repositories.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val mediaControllerManager: MediaControllerManager
):MediaRepository {
    override fun togglePlayback() = mediaControllerManager.togglePlayback()
    override fun seekTo(position: Long) = mediaControllerManager.moveToSpecificPosition(position)
    override fun seekToNext() = mediaControllerManager.seekToNextItem()
    override fun seekToPrevious() = mediaControllerManager.seekToPreviousItem()
    override fun toggleShuffle() = mediaControllerManager.shuffleButtonClicked()
    override fun toggleRepeat() = mediaControllerManager.repeatButtonClicked()
    override fun addPlaylist(songs: List<Song>) {
        mediaControllerManager.addPlaylistOfAudiosToPlayer(songs)
    }
    override fun clearPlaylist() = mediaControllerManager.clearPlayer()
    override fun seekToItem(index: Int) = mediaControllerManager.moveToSpecificItem(index)
    override fun setSongToPlayNext(id: String) = mediaControllerManager.setSongToPlayNext(id)
    override fun seekForward() = mediaControllerManager.seekForward()
    override fun seekBackward() =mediaControllerManager.seekBackward()
    override fun reconnectIfNeeded() = mediaControllerManager.reconnectIfNeeded()
    override fun getPositionOfSongInsidePlaylist(id: String) {
        mediaControllerManager.getTrackIndexById(id)
    }
    override fun observePlaybackState(): Flow<PlaybackState> {
        return mediaControllerManager.playbackState
    }
    override fun observePlaybackProgress(): Flow<PlaybackProgress> {
        return mediaControllerManager.playbackProgress
    }
    override fun observeCurrentSong(): Flow<Song> = mediaControllerManager.currentSong
    override fun cycleShuffleRepeat() {
        mediaControllerManager.cycleShuffleRepeat()
        Log.d("PLAYER_REPO","${mediaControllerManager.playbackState.value.isShufflingClicked}")
    }
}