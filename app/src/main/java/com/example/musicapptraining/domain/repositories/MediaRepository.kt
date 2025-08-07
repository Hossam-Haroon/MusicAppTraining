package com.example.musicapptraining.domain.repositories

import com.example.musicapptraining.domain.model.PlaybackProgress
import com.example.musicapptraining.domain.model.PlaybackState
import com.example.musicapptraining.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun togglePlayback()
    fun seekTo(position: Long)
    fun seekToNext()
    fun seekToPrevious()
    fun toggleShuffle()
    fun toggleRepeat()
    fun addPlaylist(songs: List<Song>)
    fun clearPlaylist()
    fun seekToItem(index: Int)
    fun setSongToPlayNext(id : String)
    fun seekForward()
    fun seekBackward()
    fun reconnectIfNeeded()
    fun getPositionOfSongInsidePlaylist(id:String)
    fun observePlaybackState(): Flow<PlaybackState>
    fun observePlaybackProgress(): Flow<PlaybackProgress>
    fun observeCurrentSong(): Flow<Song>
    fun cycleShuffleRepeat()
}