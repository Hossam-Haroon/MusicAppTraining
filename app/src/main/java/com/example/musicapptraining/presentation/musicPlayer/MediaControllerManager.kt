package com.example.musicapptraining.presentation.musicPlayer

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.media3.common.C.TIME_UNSET
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicapptraining.data.services.MusicService
import com.example.musicapptraining.presentation.mappers.toSong
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaControllerManager @Inject constructor(
    private val playbackStateManager: PlaybackStateManager,
    private val mediaControllerListener: MediaControllerListener,
   @ApplicationContext private val applicationContext: Context
) {
    init {
        mediaControllerListener.setMediaControllerManager(this)
    }
    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var connectionRetryCount = 0
    private val maxRetryCount = 3
    var onRetryRequested : ((Int)->Unit)? = null
    fun checkMediaControllerValidation(){
        if (mediaControllerFuture != null && !mediaControllerFuture!!.isDone) {
            Log.d("PlaybackViewModel", "MediaController connection already in progress.")
            return
        }
    }
    fun setMediaController(){
        try {
            val sessionToken = SessionToken(
                applicationContext,
                ComponentName(applicationContext, MusicService::class.java)
            )
            Log.e("checkMediaController", "loading mediaController")
            mediaControllerFuture = MediaController
                .Builder(applicationContext,sessionToken)
                .buildAsync()
            mediaControllerFuture?.addListener(
                setListenerForMediaControllerFuture(),
                MoreExecutors.directExecutor()
            )
        }catch (e:Exception){
            Log.e("PlaybackViewModel", "Failed to connect to media controller", e)
            retryConnection()
        }
    }
    private fun setListenerForMediaControllerFuture():Runnable{
        return Runnable {
            try {
                mediaController = mediaControllerFuture?.get()
                mediaController?.let { controller ->
                    connectionRetryCount = 0
                    controller.addListener(mediaControllerListener)
                    Log.e("checkMediaController", "mediaController connection successful")
                    if (controller.playbackState == Player.STATE_IDLE) {
                        controller.prepare()
                    }
                    playbackStateManager.updatePlayingState(controller.isPlaying)
                    playbackStateManager.updateCurrentMediaPositionInList(
                        controller.currentMediaItemIndex
                    )
                    playbackStateManager.updateCurrentMediaDurationInMs(
                        if (controller.duration > 0
                            && controller.duration != TIME_UNSET
                        ) controller.duration else 0L
                    )
                    playbackStateManager.updateCurrentMediaProgressInMs(controller.currentPosition)
                    playbackStateManager.updateRepeatingState(
                        controller.repeatMode == Player.REPEAT_MODE_ONE
                    )
                    playbackStateManager.updateShufflingState(controller.shuffleModeEnabled)
                    controller.currentMediaItem?.let { item ->
                        playbackStateManager.updateCurrentSongState(item.toSong())
                    }
                }
            } catch (e: Exception) {
                Log.e("PlaybackViewModel", "MediaController connection failed", e)
                retryConnection()
            }
        }
    }
    private fun retryConnection() {
        if (connectionRetryCount < maxRetryCount) {
            connectionRetryCount++
            Log.d(
                "PlaybackViewModel",
                "Retrying connection (attempt $connectionRetryCount)"
            )
            onRetryRequested?.invoke(connectionRetryCount)
        } else {
            Log.e("PlaybackViewModel", "Max retry attempts reached. Connection failed.")
        }
    }
    fun getController():MediaController? = mediaController
    fun onClear(){
        mediaControllerFuture?.let { future ->
            if (!future.isDone) {
                future.cancel(true)
            }
            MediaController.releaseFuture(future)
        }
        mediaController?.release()
        mediaController = null
    }
}