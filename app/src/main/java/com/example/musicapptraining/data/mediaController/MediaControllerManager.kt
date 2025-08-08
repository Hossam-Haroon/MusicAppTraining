package com.example.musicapptraining.data.mediaController

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.C.TIME_UNSET
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicapptraining.data.services.MusicService
import com.example.musicapptraining.domain.model.PlaybackProgress
import com.example.musicapptraining.domain.model.PlaybackState
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.mappers.toMediaMetaItem
import com.example.musicapptraining.presentation.mappers.toSong
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaControllerManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context
):DefaultLifecycleObserver {
    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var songIdToPlayNext = ""
    private var audioProgressJob : Job? = null
    private var connectionJob : Job? = null
    private var connectionRetryCount = 0
    private val maxRetryCount = 3
    private var _playbackState = MutableStateFlow(PlaybackState())
    val playbackState = _playbackState.asStateFlow()
    private val _playbackProgress = MutableStateFlow(PlaybackProgress())
    val playbackProgress = _playbackProgress.asStateFlow()
    private var _currentSong = MutableStateFlow(
        Song("","","",
            "",0,"",0,null,"")
    )
    val currentSong = _currentSong.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    init {
        initializeService()
    }
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        clearJobs()
    }
    fun cycleShuffleRepeat() {
        val playbackState = _playbackState.value
        when {
            !playbackState.isShufflingClicked && !playbackState.isRepeatingClicked -> {
                mediaController?.shuffleModeEnabled = true
            }
            playbackState.isShufflingClicked -> {
                mediaController?.shuffleModeEnabled = false
                mediaController?.repeatMode = Player.REPEAT_MODE_ONE
            }
            else -> {
                mediaController?.repeatMode = Player.REPEAT_MODE_OFF
            }
        }
    }
    private fun initializeService() {
        val intent = Intent(applicationContext, MusicService::class.java)
        applicationContext.startService(intent)
        scope.launch {
            delay(500)
            setMediaControllerToConnectToMediaSessionService()
        }
    }
    private fun setMediaControllerToConnectToMediaSessionService(){
        if (mediaControllerFuture != null && !mediaControllerFuture!!.isDone) {
            Log.d("PlaybackViewModel", "MediaController connection already in progress.")
            return
        }
        connectionJob?.cancel()
        connectionJob = scope.launch {
            try {
                val sessionToken = SessionToken(
                    applicationContext,
                    ComponentName(applicationContext, MusicService::class.java)
                )
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
    }
    private fun setListenerForMediaControllerFuture():Runnable{
        return Runnable {
            try {
                mediaController = mediaControllerFuture?.get()
                mediaController?.let { controller ->
                    connectionRetryCount = 0
                    controller.addListener(PlayerListener(controller))
                    if (controller.playbackState == Player.STATE_IDLE) {
                        controller.prepare()
                    }
                    updateInitialStates(controller)
                }
            } catch (e: Exception) {
                Log.e("PlaybackViewModel", "MediaController connection failed", e)
                retryConnection()
            }
        }
    }
    private fun updateInitialStates(controller: MediaController){
        _playbackState.value = _playbackState.value.copy(
            isPlaying = controller.isPlaying,
            currentMediaPositionInList = controller.currentMediaItemIndex,
            isRepeatingClicked = controller.repeatMode == Player.REPEAT_MODE_ONE,
            isShufflingClicked = controller.shuffleModeEnabled,
        )
        _playbackProgress.value = _playbackProgress.value.copy(
            currentMediaDurationInMs = if (controller.duration > 0
                && controller.duration != TIME_UNSET
            ) controller.duration else 0L,
            currentMediaProgressInMs = controller.currentPosition
        )
        controller.currentMediaItem?.let { item->
            _currentSong.value = item.toSong()
        }
    }
    fun togglePlayback() {
        mediaController?.let { controller ->
            if (controller.isPlaying) controller.pause() else controller.play()
        }
    }
     fun moveToSpecificPosition(position: Long) {
        mediaController?.seekTo(position)
    }
     fun seekForward() {
        mediaController?.seekForward()
    }
     fun seekBackward() {
        mediaController?.seekBack()
    }
     fun clearPlayer() {
        mediaController?.stop()
        mediaController?.clearMediaItems()
    }
     fun seekToNextItem() {
        mediaController?.let {
            if (it.hasNextMediaItem()) {
                mediaController?.seekToNextMediaItem()
                _playbackState.value = _playbackState.value.copy(
                    currentMediaPositionInList = it.currentMediaItemIndex
                )
            }
        }
    }
     fun seekToPreviousItem() {
        mediaController?.let {
            if (it.hasPreviousMediaItem()){
                it.seekToPreviousMediaItem()
                _playbackState.value = _playbackState.value.copy(
                    currentMediaPositionInList = it.currentMediaItemIndex
                )
            }
        }
    }
     private fun updatePlayerProgress(playerProgress: Long) {
        mediaController?.let {
            _playbackProgress.value = _playbackProgress.value.copy(
                currentMediaProgressInMs = playerProgress
            )
        }
    }
     fun setSongToPlayNext(songId : String){
        songIdToPlayNext = songId
    }
    fun getTrackIndexById(songId: String):Int{
        mediaController?.let {
            for (i in 0 until it.mediaItemCount){
                val mediaItem = it.getMediaItemAt(i)
                if (mediaItem.mediaId == songId){
                    moveToSpecificItem(i)
                    return i
                }
            }
        }
        return -1
    }
     fun moveToSpecificItem(itemIndex : Int){
        mediaController?.let {controller->
            controller.apply {
                seekTo(itemIndex,0L)
                play()
                _currentSong.value =currentMediaItem!!.toSong()
                _playbackState.value = _playbackState.value.copy(
                    currentMediaPositionInList = currentMediaItemIndex
                )
            }
        }
    }
     fun addPlaylistOfAudiosToPlayer(audios:List<Song>){
        scope.launch {
            if (mediaController == null) {
                reconnectIfNeeded()
                delay(1000)
            }
            waitForMediaController()
            mediaController?.let {controller->
                val mediaItems = audios.map { item->
                    val metadata = item.toMediaMetaItem()
                    MediaItem.Builder().apply {
                        setMediaId(item.songId)
                        setUri(item.songPath)
                        setMediaMetadata(metadata)
                    }.build()
                }
                controller.addMediaItems(mediaItems)
                controller.prepare()
                controller.pause()
            } ?: Log.e(
                "PlaybackViewModel",
                "MediaController is null, cannot add playlist."
            )
        }
    }
    private suspend fun waitForMediaController() {
        while (mediaController == null) delay(100)
    }
    inner class PlayerListener(private val mediaController: MediaController): Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
            if (isPlaying){
                startProgressTracking()
            }else{
                stopProgressTracking()
            }
        }
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            setRequiredReason(reason,newPosition)
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            setPlaybackStateCases(playbackState,mediaController)
        }
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO ||
                reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
                _playbackProgress.value = _playbackProgress.value.copy(
                    currentMediaProgressInMs = 0L
                )
            }
            setSongToPlayNextHandle(reason)
            _playbackState.value = _playbackState.value.copy(
                currentMediaPositionInList = mediaController.currentMediaItemIndex
            )
            mediaItem?.let { mediaItemValue->
                _currentSong.value = mediaItemValue.toSong()
            }
        }
        override fun onEvents(player: Player, events: Player.Events) {
            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) ||
                events.contains(Player.EVENT_MEDIA_METADATA_CHANGED) ||
                events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
                val duration = player.duration
                if (duration > 0) {
                    _playbackProgress.value = _playbackProgress.value.copy(
                        currentMediaDurationInMs = duration
                    )
                    _currentSong.value = _currentSong.value.copy(songDuration = duration)
                }
            }
        }
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _playbackState.value = _playbackState.value.copy(
                isShufflingClicked = shuffleModeEnabled
            )
        }
        override fun onRepeatModeChanged(repeatMode: Int) {
            val isRepeating = repeatMode == Player.REPEAT_MODE_ONE
            _playbackState.value = _playbackState.value.copy(isRepeatingClicked = isRepeating)
        }
    }
    private fun startProgressTracking(){
        audioProgressJob?.cancel()
        audioProgressJob = scope.launch {
            while (isActive){
                mediaController?.let {
                    updatePlayerProgress(it.currentPosition)
                }
                delay(ONE_SECOND)
            }
        }
    }
    private fun stopProgressTracking() = audioProgressJob?.cancel()
    private fun setRequiredReason(reason:Int, newPosition:Player.PositionInfo){
        when(reason){
            Player.DISCONTINUITY_REASON_SEEK -> {
                updatePlayerProgress(newPosition.positionMs)
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
                    _playbackState.value = _playbackState.value.copy(
                        currentMediaPositionInList = mediaController.currentMediaItemIndex
                    )
                }
            }
            Player.STATE_IDLE ->{
                _playbackProgress.value = _playbackProgress.value.copy(
                    currentMediaProgressInMs = 0L,
                    currentMediaDurationInMs = 0L
                )
            }
            Player.STATE_BUFFERING ->{}
            Player.STATE_READY -> {}
        }
    }
    private fun setSongToPlayNextHandle(reason: Int){
        if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO && songIdToPlayNext != ""){
            getTrackIndexById(songIdToPlayNext)
            songIdToPlayNext = ""
        }
    }
    fun reconnectIfNeeded() {
        if (mediaController == null) {
            Log.d(
                "PlaybackViewModel",
                "MediaController is null, attempting to reconnect..."
            )
            setMediaControllerToConnectToMediaSessionService()
        }
    }
    private fun retryConnection() {
        if (connectionRetryCount < maxRetryCount) {
            connectionRetryCount++
            Log.d(
                "PlaybackViewModel",
                "Retrying connection (attempt $connectionRetryCount)"
            )
            scope.launch {
                delay(1000 * connectionRetryCount.toLong())
                setMediaControllerToConnectToMediaSessionService()
            }
        } else {
            Log.e("PlaybackViewModel", "Max retry attempts reached. Connection failed.")
        }
    }
    private fun clearJobs() {
        connectionJob?.cancel()
        scope.cancel()
        mediaControllerFuture?.let { future ->
            if (!future.isDone) {
                future.cancel(true)
            }
            MediaController.releaseFuture(future)
        }
        mediaController?.release()
        mediaController = null
        audioProgressJob?.cancel()
    }
    companion object{
        private const val ONE_SECOND = 1000L
    }
}