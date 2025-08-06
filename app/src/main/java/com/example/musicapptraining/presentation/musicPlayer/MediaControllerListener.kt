package com.example.musicapptraining.presentation.musicPlayer

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
import androidx.media3.session.MediaController
import com.example.musicapptraining.presentation.mappers.toSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaControllerListener @Inject constructor(
    private val playbackStateManager: PlaybackStateManager,
    private val mediaPlayerController: MediaPlayerController
):Listener {
    private var mediaControllerManager: MediaControllerManager? = null
    var onPlayingChanged : ((Boolean)->Unit)? = null
    fun setMediaControllerManager(mediaControllerManager: MediaControllerManager){
        this.mediaControllerManager = mediaControllerManager
    }
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        playbackStateManager.updatePlayingState(isPlaying)
        onPlayingChanged?.invoke(isPlaying)
    }
    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        setRequiredReason(reason,newPosition)
    }
    override fun onPlaybackStateChanged(playbackState: Int) {
        mediaControllerManager?.getController()?.let { setPlaybackStateCases(playbackState, it) }
    }
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        Log.d(
            "PlayerListener",
            "onMediaItemTransition: MediaItem changed. Reason: $reason"
        )
        if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO ||
            reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
            playbackStateManager.updateCurrentMediaProgressInMs(0L)
        }
        setSongToPlayNextHandle(reason)
        mediaControllerManager?.getController()?.currentMediaItemIndex?.let {
            playbackStateManager.updateCurrentMediaPositionInList(
                it
            )
        }
        mediaItem?.let { mediaItemValue->
            playbackStateManager.updateCurrentSongState(mediaItemValue.toSong())
        }
    }
    override fun onEvents(player: Player, events: Player.Events) {
        if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) ||
            events.contains(Player.EVENT_MEDIA_METADATA_CHANGED) ||
            events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
            val duration = player.duration
            if (duration > 0) {
                playbackStateManager.updateCurrentMediaDurationInMs(duration)
                playbackStateManager.updateCurrentSongState(
                    playbackStateManager.currentSong.value.copy(songDuration = duration)
                )
                Log.d("checkDuration", "Duration updated: $duration")
            }
        }
    }
    private fun setRequiredReason(reason:Int, newPosition:Player.PositionInfo){
        when(reason){
            Player.DISCONTINUITY_REASON_SEEK -> {
                mediaPlayerController.updatePlayerProgress(
                    newPosition.positionMs
                )
            }
            Player.DISCONTINUITY_REASON_AUTO_TRANSITION -> Unit
            Player.DISCONTINUITY_REASON_SKIP -> Unit
            Player.DISCONTINUITY_REASON_REMOVE -> Unit
            Player.DISCONTINUITY_REASON_INTERNAL -> Unit
            Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT -> Unit
            Player.DISCONTINUITY_REASON_SILENCE_SKIP -> Unit
        }
    }
    private fun setPlaybackStateCases(playbackState:Int,mediaController: MediaController){
        when(playbackState){
            Player.STATE_ENDED -> {
                if (mediaController.hasNextMediaItem()){
                    mediaController.seekToNextMediaItem()
                    playbackStateManager.updateCurrentMediaPositionInList(
                        mediaController.currentMediaItemIndex
                    )
                }
            }
            Player.STATE_IDLE ->{
                playbackStateManager.updateCurrentMediaDurationInMs(0L)
                playbackStateManager.updateCurrentMediaProgressInMs(0L)
            }
            Player.STATE_BUFFERING ->playbackStateManager.updateBufferingState(true)
            Player.STATE_READY ->playbackStateManager.updateBufferingState(false)
        }
    }
    private fun setSongToPlayNextHandle(reason: Int){
        if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO
            && mediaPlayerController.songIdToPlayNext != ""){
           mediaPlayerController.getTrackIndexById(
               mediaPlayerController.songIdToPlayNext,
               mediaControllerManager?.getController()
               )
            mediaPlayerController.songIdToPlayNext = ""
        }
    }
}