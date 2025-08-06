package com.example.musicapptraining.presentation.musicPlayer

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.data.services.MusicService
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.utilities.PlayerEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.Thread.State
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playbackStateManager: PlaybackStateManager,
    private val mediaPlayerController: MediaPlayerController,
    private val mediaControllerManager: MediaControllerManager,
    private val mediaControllerListener: MediaControllerListener,
    @ApplicationContext private val context: Context
):ViewModel(){
    private var connectionJob: Job? = null
    private var audioProgressJob : Job? = null
    val isPlaying: StateFlow<Boolean> = playbackStateManager.isPlaying
    val currentMediaDurationInMs: StateFlow<Long> = playbackStateManager.currentMediaDurationInMs
    val currentMediaProgressInMs:StateFlow<Long> = playbackStateManager.currentMediaProgressInMs
    val isRepeatingClicked:StateFlow<Boolean> = playbackStateManager.isRepeatingClicked
    val isShufflingClicked:StateFlow<Boolean> = playbackStateManager.isShufflingClicked
    val currentSong:StateFlow<Song> = playbackStateManager.currentSong
    init {
        mediaControllerListener.setMediaControllerManager(mediaControllerManager)
        setupOnPlayingChangedCallBack()
        setOnRetryRequestCallBack()
        initializeService()
        Log.d("checkPlayerViewModel","${currentSong.value}")
    }
    private fun initializeService() {
        val intent = Intent(context, MusicService::class.java)
        context.startService(intent)
        viewModelScope.launch {
            delay(500)
            setMediaControllerToConnectToMediaSessionService()
        }
    }
    private fun setMediaControllerToConnectToMediaSessionService(){
        mediaControllerManager.checkMediaControllerValidation()
        connectionJob?.cancel()
        connectionJob = viewModelScope.launch {
            mediaControllerManager.setMediaController()
        }
    }
    private fun setupOnPlayingChangedCallBack(){
        mediaControllerListener.onPlayingChanged = { isPlaying->
        if (isPlaying){
            startProgressTracking()
        }else{
            stopProgressTracking()
        }
        }
    }
    private fun startProgressTracking(){
        audioProgressJob?.cancel()
        audioProgressJob = viewModelScope.launch {
            while (isActive){
                mediaControllerManager.getController()
                    ?.let {
                        mediaPlayerController.updatePlayerProgress(
                        it.currentPosition
                    )
                    }
                delay(ONE_SECOND)
            }
        }
    }
    private fun stopProgressTracking(){
        audioProgressJob?.cancel()
    }
    private fun setOnRetryRequestCallBack() {
        mediaControllerManager.onRetryRequested = { connectionRetryCount ->
            viewModelScope.launch {
                delay(1000 * connectionRetryCount.toLong())
                setMediaControllerToConnectToMediaSessionService()
            }
        }
    }
    fun getEvent(event: PlayerEvents){
        when(event){
            is PlayerEvents.AddPlayList -> {
                viewModelScope.launch {
                    while (mediaControllerManager.getController() == null){
                        delay(100)
                    }
                    mediaPlayerController.addPlaylistOfAudiosToPlayer(
                        event.songs,mediaControllerManager.getController()
                    )
                }
            }
            is PlayerEvents.AddSongToPlayNext -> mediaPlayerController.setSongToPlayNext(
                event.songId
            )
            PlayerEvents.ClearMediaItems -> mediaPlayerController.clearPlayer(
                mediaControllerManager.getController()
            )
            is PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList ->
                mediaPlayerController.getTrackIndexById(
                    event.id,mediaControllerManager.getController()
                )
            is PlayerEvents.GoToSpecificItem -> mediaPlayerController.moveToSpecificItem(
                event.index,mediaControllerManager.getController()
            )
            is PlayerEvents.GoToSpecificPosition -> mediaPlayerController.moveToSpecificPosition(
                event.position,mediaControllerManager.getController()
            )
            PlayerEvents.Next -> mediaPlayerController.seekToNextItem(
                mediaControllerManager.getController()
            )
            PlayerEvents.PausePlay -> mediaPlayerController.togglePlayback(
                mediaControllerManager.getController()
            )
            PlayerEvents.Previous -> mediaPlayerController.seekToPreviousItem(
                mediaControllerManager.getController()
            )
            PlayerEvents.Repeat -> mediaPlayerController.repeatButtonClicked(
                mediaControllerManager.getController()
            )
            PlayerEvents.SeekBackward -> mediaPlayerController.seekBackward(
                mediaControllerManager.getController()
            )
            PlayerEvents.SeekForward -> mediaPlayerController.seekForward(
                mediaControllerManager.getController()
            )
            PlayerEvents.Shuffle -> mediaPlayerController.shuffleButtonClicked(
                mediaControllerManager.getController()
            )
        }
    }
    fun reconnectIfNeeded(){
        if (mediaControllerManager.getController() == null){
            Log.d("CHeckMedia","${mediaControllerManager.getController()}")
            setMediaControllerToConnectToMediaSessionService()
        }
    }
    companion object{
        private const val ONE_SECOND = 1000L
    }

    override fun onCleared() {
        super.onCleared()
        connectionJob?.cancel()
        mediaControllerManager.onClear()
        audioProgressJob?.cancel()
    }
}