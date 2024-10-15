package com.example.musicapptraining.ui.musicPlayer

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.player.MediaController
import com.example.musicapptraining.player.MediaControllerTest
import com.example.musicapptraining.utilities.PlayerEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel
    @Inject constructor(
        player : ExoPlayer,
        @ApplicationContext context: Context,
       private var sharedPreferences: SharedPreferences
): ViewModel() {

    private var _currentSong = MutableStateFlow(Song("","","",
            "",0,"",0,null,""))
    var currentSong = _currentSong.asStateFlow()

    private var _currentMediaPosition = MutableStateFlow(0f)
    var currentMediaPosition = _currentMediaPosition.asStateFlow()

    private var _currentMediaDurationInMinutes = MutableStateFlow(0L)
    var currentMediaDurationInMinutes = _currentMediaDurationInMinutes.asStateFlow()

    private var _currentMediaProgressionInMinutes = MutableStateFlow(0L)
    var currentMediaProgressionInMinutes = _currentMediaProgressionInMinutes.asStateFlow()

    private var _isPausePlayClicked = MutableStateFlow(false)
    var isPausePlayClicked = _isPausePlayClicked.asStateFlow()

    private var _isRepeatingClicked = MutableStateFlow(false)
    var isRepeatingClicked = _isRepeatingClicked.asStateFlow()

    private var _isShufflingClicked = MutableStateFlow(false)
    var isShufflingClicked = _isShufflingClicked.asStateFlow()

    private var _isBufferingCLicked = MutableStateFlow(false)
    var isBufferingCLicked = _isBufferingCLicked.asStateFlow()

    private var _currentMediaPositionInList = MutableStateFlow(0f)
    var currentMediaPositionInList = _currentMediaPositionInList.asStateFlow()


    val playerController = MediaController(
            player,_currentSong,_currentMediaPosition,_currentMediaDurationInMinutes,
            _currentMediaProgressionInMinutes,_isPausePlayClicked,_isRepeatingClicked,_isBufferingCLicked,
            _isShufflingClicked,viewModelScope,sharedPreferences,_currentMediaPositionInList)

    init {
        player.addListener(playerController)
        playerController.setupMediaNotification(context)
    }
    fun formatDuration(durationMs: Long): String {
        val seconds = (durationMs / 1000) % 60
        val minutes = (durationMs / (1000 * 60)) % 60
        val hours = durationMs / (1000 * 60 * 60)

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

   /* fun forceUpdateMediaInfo(){
        playerController.updateMediaInfo()
    }*/

    fun getEvent(event : PlayerEvents){
        when(event){
            is PlayerEvents.AddPlayList -> playerController.addPlayList(event.songs,event.isUpdatePlaylistRequired)
            is PlayerEvents.AddSongToPlayNext -> playerController.setSongToPlayNext(event.songId)
            PlayerEvents.ClearMediaItems -> playerController.clearPlayer()
            is PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList ->
                playerController.getTrackIndexById(event.id)
            is PlayerEvents.GoToSpecificItem -> playerController.moveToSpecificItem(event.index)
            is PlayerEvents.GoToSpecificPosition -> playerController.moveToSpecificPosition(event.position)
            PlayerEvents.Next -> {
                playerController.nextItem()
                playerController.pauseOrPlay()
            }
            PlayerEvents.PausePlay -> playerController.pauseOrPlay()
            PlayerEvents.Previous -> playerController.previousItem()
            PlayerEvents.Repeat -> playerController.repeatClick()
            PlayerEvents.SeekBackward -> playerController.seekBackward()
            PlayerEvents.SeekForward -> playerController.seekForward()
            PlayerEvents.Shuffle -> playerController.shuffleClick()
        }
    }

}