package com.example.musicapptraining.presentation.PlayerControllerViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.domain.model.PlaybackProgress
import com.example.musicapptraining.domain.model.PlaybackState
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.repositories.MediaRepository
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.AddPlaylistToPlayerUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.ClearPlayerUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.CycleShuffleRepeatUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.GetPositionOfSongInsidePlaylistUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.ReconnectIfNeededUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SeekBackwardUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SeekForwardUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SeekToNextTrackUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SeekToPositionUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SeekToPreviousTrackUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SeekToTrackUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SetSongToPlayNextUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.TogglePlaybackUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.ToggleRepeatUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.ToggleShuffleUseCase
import com.example.musicapptraining.utilities.PlayerEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerControllerViewModel @Inject constructor(
    private val togglePlaybackUseCase: TogglePlaybackUseCase,
    private val seekToPositionUseCase: SeekToPositionUseCase,
    private val seekToNextTrackUseCase: SeekToNextTrackUseCase,
    private val seekToPreviousTrackUseCase: SeekToPreviousTrackUseCase,
    private val toggleShuffleUseCase: ToggleShuffleUseCase,
    private val toggleRepeatUseCase: ToggleRepeatUseCase,
    private val addPlaylistUseCase: AddPlaylistToPlayerUseCase,
    private val clearPlaylistUseCase: ClearPlayerUseCase,
    private val seekToTrackUseCase: SeekToTrackUseCase,
    private val seekForwardUseCase: SeekForwardUseCase,
    private val seekBackwardUseCase: SeekBackwardUseCase,
    private val setSongToPlayNextUseCase: SetSongToPlayNextUseCase,
    private val cycleShuffleRepeatUseCase: CycleShuffleRepeatUseCase,
    private val getPositionOfSongInsidePlaylistUseCase: GetPositionOfSongInsidePlaylistUseCase,
    val reconnectIfNeededUseCase: ReconnectIfNeededUseCase,
    mediaRepository: MediaRepository
):ViewModel() {
    private var _playbackState = MutableStateFlow(PlaybackState())
    val playbackState = _playbackState.asStateFlow()
    /*val playbackState = mediaRepository.observePlaybackState().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PlaybackState()
        )*/
    val playbackProgress = mediaRepository.observePlaybackProgress()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlaybackProgress()
        )
    val currentSong = mediaRepository.observeCurrentSong()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Song(
                "","","","",0,"",
                0,null,""
            )
        )
    init {
        viewModelScope.launch {
            mediaRepository.observePlaybackState().collect{
                _playbackState.value = it
            }
        }
    }
    fun getEvent(event:PlayerEvents){
        when(event){
            is PlayerEvents.AddPlayList -> addPlaylistUseCase(event.songs)
            is PlayerEvents.AddSongToPlayNext -> setSongToPlayNextUseCase(event.songId)
            PlayerEvents.ClearMediaItems -> clearPlaylistUseCase()
            is PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList ->
                getPositionOfSongInsidePlaylistUseCase(event.id)
            is PlayerEvents.GoToSpecificItem -> seekToTrackUseCase(event.index)
            is PlayerEvents.GoToSpecificPosition -> seekToPositionUseCase(event.position)
            PlayerEvents.Next -> seekToNextTrackUseCase()
            PlayerEvents.PausePlay -> togglePlaybackUseCase()
            PlayerEvents.Previous -> seekToPreviousTrackUseCase()
            PlayerEvents.Repeat -> toggleRepeatUseCase()
            PlayerEvents.SeekBackward -> seekBackwardUseCase()
            PlayerEvents.SeekForward -> seekForwardUseCase()
            PlayerEvents.Shuffle -> toggleShuffleUseCase()
            PlayerEvents.CycleShuffleRepeat -> {
                Log.d("PLAYER_VM","▶️ CycleShuffleRepeat in VM")
                Log.d("PLAYER_VM","${playbackState.value.isShufflingClicked}")
                cycleShuffleRepeatUseCase()
            }
        }
    }
}