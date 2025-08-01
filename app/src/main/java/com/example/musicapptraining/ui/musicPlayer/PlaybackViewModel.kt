package com.example.musicapptraining.ui.musicPlayer


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.media3.common.C.TIME_UNSET
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.player.MusicService
import com.example.musicapptraining.utilities.PlayerEvents
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    @ApplicationContext
    private val applicationContext: Context,
):ViewModel(){
    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var songIdToPlayNext = ""
    private var audioProgressJob : Job? = null
    private var connectionRetryCount = 0
    private val maxRetryCount = 3
    private var connectionJob: Job? = null
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
    val currentMediaPosition = _currentMediaPosition.asStateFlow()
    private var _currentSong = MutableStateFlow(
        Song("","","",
        "",0,"",0,null,"")
    )
    val currentSong = _currentSong.asStateFlow()
    init {
        initializeService()
    }
    private fun initializeService() {
        val intent = Intent(applicationContext, MusicService::class.java)
        applicationContext.startService(intent)
        viewModelScope.launch {
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
        connectionJob = viewModelScope.launch {
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
    }
    private fun setListenerForMediaControllerFuture():Runnable{
        return Runnable{
            try {
                mediaController = mediaControllerFuture?.get()
                mediaController?.let {controller->
                    connectionRetryCount = 0
                    controller.addListener(PlayerListener(controller))
                    Log.e("checkMediaController", "mediaController connection successful")
                    if (controller.playbackState == Player.STATE_IDLE) {
                        controller.prepare()
                    }
                    _isPlaying.value = controller.isPlaying
                    _currentMediaPositionInList.value = controller.currentMediaItemIndex
                    _currentMediaDurationInMs.value = if (controller.duration > 0
                        && controller.duration != TIME_UNSET) controller.duration else 0L
                    _currentMediaProgressInMs.value = controller.currentPosition
                    _isRepeatingClicked.value = controller.repeatMode == Player.REPEAT_MODE_ONE
                    _isShufflingClicked.value = controller.shuffleModeEnabled
                    controller.currentMediaItem?.let { item ->
                        _currentSong.value = item.toSong()
                    }
                }
            }catch(e:Exception){
                Log.e("PlaybackViewModel", "MediaController connection failed", e)
                retryConnection()
            }
        }
    }
    private fun togglePlayback() {
        mediaController?.let { controller ->
            if (controller.isPlaying) controller.pause() else controller.play()
        }
    }
    private fun moveToSpecificPosition(position: Long) {
        mediaController?.seekTo(position)
    }
    private fun seekForward() {
        mediaController?.seekForward()
    }
    private fun seekBackward() {
        mediaController?.seekBack()
    }
    private fun clearPlayer() {
        mediaController?.stop()
        mediaController?.clearMediaItems()
    }
    private fun shuffleButtonClicked() {
        when(_isShufflingClicked.value){
            true ->{
                _isShufflingClicked.value = false
                mediaController?.shuffleModeEnabled = _isShufflingClicked.value
            }
            else ->{
                _isShufflingClicked.value = true
                mediaController?.shuffleModeEnabled = _isShufflingClicked.value
            }
        }
    }
    private fun repeatButtonClicked() {
        when(_isRepeatingClicked.value){
            true -> {
                _isRepeatingClicked.value = false
                mediaController?.repeatMode = Player.REPEAT_MODE_OFF
            }
            else->{
                _isRepeatingClicked.value = true
                mediaController?.repeatMode = Player.REPEAT_MODE_ONE
            }
        }
    }
    private fun seekToNextItem() {
        mediaController?.let {
            if (it.hasNextMediaItem()) {
                mediaController?.seekToNextMediaItem()
                _currentMediaPositionInList.value = it.currentMediaItemIndex
            }
        }
    }
    private fun seekToPreviousItem() {
        mediaController?.let {
            if (it.hasPreviousMediaItem()){
                it.seekToPreviousMediaItem()
                _currentMediaPositionInList.value = it.currentMediaItemIndex
            }
        }
    }
    private fun updatePlayerProgress(playerProgress: Long) {
        mediaController?.let {
            _currentMediaProgressInMs.value = playerProgress
            val progress = playerProgress.toFloat() / it.duration.toFloat()
            if (!progress.isNaN()) _currentMediaPosition.value = progress
        }
    }
    private fun setSongToPlayNext(songId : String){
        songIdToPlayNext = songId
    }
    private fun getTrackIndexById(songId: String):Int{
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
    private fun moveToSpecificItem(itemIndex : Int){
        mediaController?.let {
            it.apply {
                seekTo(itemIndex,0L)
                play()
                _currentSong.value =currentMediaItem!!.toSong()
                _currentMediaPositionInList.value = currentMediaItemIndex
            }
        }
    }
    private fun setRequiredReason(reason:Int, newPosition:Player.PositionInfo){
        when(reason){
            Player.DISCONTINUITY_REASON_SEEK -> {
                mediaController?.seekTo(newPosition.positionMs)
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
                    _currentMediaPositionInList.value = mediaController.currentMediaItemIndex
                }
            }
            Player.STATE_IDLE ->{
                _currentMediaDurationInMs.value = 0L
                _currentMediaProgressInMs.value = 0L
                _isBufferingClicked.value = false
            }
            Player.STATE_BUFFERING ->_isBufferingClicked.value = true
            Player.STATE_READY ->_isBufferingClicked.value = false
        }
    }
    private fun setSongToPlayNextHandle(reason: Int){
        if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO && songIdToPlayNext != ""){
            getTrackIndexById(songIdToPlayNext)
            songIdToPlayNext = ""
        }
    }
    inner class PlayerListener(private val mediaController: MediaController):Listener{
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying){
                audioProgressJob?.cancel()
                audioProgressJob = viewModelScope.launch {
                    while (isActive){
                        updatePlayerProgress(mediaController.currentPosition)
                        delay(ONE_SECOND)
                    }
                }
            }else{
                audioProgressJob?.cancel()
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
            Log.d(
                "PlayerListener",
                "onMediaItemTransition: MediaItem changed. Reason: $reason"
            )
            if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO ||
                reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
                _currentMediaPosition.value = 0f
                _currentMediaProgressInMs.value = 0L
            }
            setSongToPlayNextHandle(reason)
            _currentMediaPositionInList.value = mediaController.currentMediaItemIndex
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
                    _currentMediaDurationInMs.value = duration
                    _currentSong.value = _currentSong.value.copy(songDuration = duration)
                    Log.d("checkDuration", "Duration updated: $duration")
                }
            }
        }
    }
    private suspend fun waitForMediaController() {
        while (mediaController == null) delay(100)
    }
    private fun addPlaylistOfAudiosToPlayer(audios:List<Song>){
        viewModelScope.launch {
            if (mediaController == null) {
                reconnectIfNeeded()
                delay(1000) // Wait for potential reconnection
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
                Log.d("PlaybackViewModel", "Playlist added to player and prepared.")
            } ?: Log.e(
                "PlaybackViewModel",
                "MediaController is null, cannot add playlist."
            )
        }
    }
    fun getEvent(event:PlayerEvents){
        try {
            when(event){
                is PlayerEvents.AddPlayList -> addPlaylistOfAudiosToPlayer(event.songs)
                is PlayerEvents.AddSongToPlayNext -> setSongToPlayNext(event.songId)
                PlayerEvents.ClearMediaItems -> clearPlayer()
                is PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList ->
                    getTrackIndexById(event.id)
                is PlayerEvents.GoToSpecificItem -> moveToSpecificItem(event.index)
                is PlayerEvents.GoToSpecificPosition -> moveToSpecificPosition(event.position)
                PlayerEvents.Next -> seekToNextItem()
                PlayerEvents.PausePlay -> togglePlayback()
                PlayerEvents.Previous -> seekToPreviousItem()
                PlayerEvents.Repeat -> repeatButtonClicked()
                PlayerEvents.SeekBackward -> seekBackward()
                PlayerEvents.SeekForward -> seekForward()
                PlayerEvents.Shuffle -> shuffleButtonClicked()
            }
        }catch (e:Exception){
            Log.e("PlaybackViewModel", "Error handling player event", e)
        }

    }
    fun MediaItem.toSong():Song{
        val songPath = this.mediaMetadata.extras?.getString(KEY_SONG_PATH) ?: ""
        if (mediaController != null){
            return Song(
                this.mediaId,
                this.mediaMetadata.displayTitle.toString(),
                songPath,
                this.mediaMetadata.artist.toString(),
                0L,
                this.mediaMetadata.albumTitle.toString(),
                0,
                this.mediaMetadata.artworkUri.toString(),
                MIME_TYPE_MP3
            )
        }else{
            return Song(
                "", "", "", "", 0,
                "", 0, null, ""
            )
        }
    }
    private fun Song.toMediaMetaItem():MediaMetadata{
        val extras = Bundle().apply {
            putString(KEY_SONG_PATH, this@toMediaMetaItem.songPath)
        }
        return MediaMetadata.Builder()
            .setTitle(this.songName)
            .setDisplayTitle(this.songName)
            .setArtist(this.songArtist)
            .setAlbumArtist(this.songArtist)
            .setAlbumTitle(this.songAlbum)
            .setArtworkUri(Uri.parse(this.songArt))
            .setExtras(extras)
            .build()
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
            viewModelScope.launch {
                delay(1000 * connectionRetryCount.toLong())
                setMediaControllerToConnectToMediaSessionService()
            }
        } else {
            Log.e("PlaybackViewModel", "Max retry attempts reached. Connection failed.")
        }
    }
    override fun onCleared() {
        super.onCleared()
        connectionJob?.cancel()
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
        private const val KEY_SONG_PATH = "KEY_SONG_PATH"
        private const val MIME_TYPE_MP3 = "mp3"
        private const val ONE_SECOND = 1000L
    }
}