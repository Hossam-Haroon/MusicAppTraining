package com.example.musicapptraining.presentation.musicPlayer

import android.util.Log
import com.example.musicapptraining.domain.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Singleton

@Singleton
class PlaybackStateManager {
    private var _isPlaying = MutableStateFlow(false)
    val isPlaying: MutableStateFlow<Boolean> = _isPlaying
    private var _currentMediaPositionInList = MutableStateFlow(0)
    val currentMediaPositionInList = _currentMediaPositionInList.asStateFlow()
    private var _currentMediaDurationInMs = MutableStateFlow(0L)
    val currentMediaDurationInMs = _currentMediaDurationInMs.asStateFlow()
    private var _currentMediaProgressInMs = MutableStateFlow(0L)
    val currentMediaProgressInMs = _currentMediaProgressInMs.asStateFlow()
    private var _isBufferingClicked = MutableStateFlow(false)
    val isBufferingClicked = _isBufferingClicked.asStateFlow()
    private var _isRepeatingClicked = MutableStateFlow(false)
    val isRepeatingClicked = _isRepeatingClicked.asStateFlow()
    private var _isShufflingClicked = MutableStateFlow(false)
    val isShufflingClicked = _isShufflingClicked.asStateFlow()
    private var _currentMediaPosition = MutableStateFlow(0f)
    private var _currentSong = MutableStateFlow(
        Song("","","",
            "",0,"",0,null,"")
    )
    val currentSong = _currentSong.asStateFlow()
    fun updateBufferingState(state:Boolean){
        _isBufferingClicked.value =state
    }
    fun updatePlayingState(isPlaying:Boolean){
        _isPlaying.value = isPlaying
    }
    fun updateCurrentMediaPositionInList(position:Int){
        _currentMediaPositionInList.value = position
    }
    fun updateCurrentMediaProgressInMs(progress:Long){
        _currentMediaProgressInMs.value = progress
    }
    fun updateCurrentMediaDurationInMs(duration:Long){
        _currentMediaDurationInMs.value = duration
    }
    fun updateRepeatingState(isRepeating:Boolean){
        _isRepeatingClicked.value = isRepeating
    }
    fun updateShufflingState(isShuffling:Boolean){
        _isShufflingClicked.value = isShuffling
    }
    fun updateCurrentSongState(song:Song){
        Log.d("STATE_FLOW", "Updating song to: ${song.songName}")
        _currentSong.value = song
    }

}