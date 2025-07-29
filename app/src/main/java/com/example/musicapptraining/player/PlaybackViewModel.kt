package com.example.musicapptraining.player


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.media3.common.C.TIME_UNSET
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicapptraining.data.model.Song
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
    private val applicationContext: Context
):ViewModel(){
    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var songIdToPlayNext = ""
    private var job : Job? = null
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
    private var _currentSong = MutableStateFlow(Song("","","",
        "",0,"",0,null,""))
    val currentSong = _currentSong.asStateFlow()
    init {
        initializeService()
    }
    private fun initializeService() {
        val intent = Intent(applicationContext, MusicServiceTest::class.java)
        applicationContext.startService(intent)
        setMediaControllerToConnectToMediaSessionService()
    }
    private fun setMediaControllerToConnectToMediaSessionService(){
        val sessionToken = SessionToken(
            applicationContext,
            ComponentName(applicationContext,MusicServiceTest::class.java)
        )
        Log.e("checkMediaController", "loading mediaController")
        mediaControllerFuture = MediaController
            .Builder(applicationContext,sessionToken)
            .buildAsync()
        mediaControllerFuture?.addListener(
            setListenerForMediaControllerFuture(),
            MoreExecutors.directExecutor()
        )
    }
    private fun setListenerForMediaControllerFuture():Runnable{
        return Runnable{
            try {
                mediaController = mediaControllerFuture?.get()
                mediaController?.let {
                    it.addListener(PlayerListener(it))
                    Log.e("checkMediaController", "mediaController connection successful")
                    if (it.playbackState == Player.STATE_IDLE) {
                        it.prepare()
                    }
                }
            }catch(e:Exception){
                Log.e("PlaybackViewModel", "MediaController connection failed", e)
            }
        }
    }
    private fun togglePlayback() {
        mediaController?.let { controller ->
            if (controller.isPlaying) {
                controller.pause()
            } else {
                controller.play()
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        mediaControllerFuture?.let { future ->
            if (!future.isDone) {
                future.cancel(true)
            }
        }
        mediaController = null
    }
    fun MediaItem.toSong():Song{
        val songPath = this.mediaMetadata.extras?.getString(KEY_SONG_PATH) ?: ""
        return Song(
            this.mediaId,
            this.mediaMetadata.displayTitle.toString(),
            songPath,
            this.mediaMetadata.artist.toString(),
            _currentMediaDurationInMs.value,
            this.mediaMetadata.albumTitle.toString(),
            0,
            this.mediaMetadata.artworkUri.toString(),
            MIME_TYPE_MP3
        )
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
            if (_isPlaying.value){
                job?.cancel()
                job = viewModelScope.launch {
                    while (isActive){
                        updatePlayerProgress(mediaController.currentPosition)
                        delay(ONE_SECOND)
                    }
                }
            }else{
                job?.cancel()
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
            _currentMediaPosition.value = 0f
            _currentMediaProgressInMs.value = 0L
            setSongToPlayNextHandle(reason)
            _currentMediaPositionInList.value = mediaController.currentMediaItemIndex
            mediaItem?.let { mediaItemValue->
                _currentSong.value = mediaItemValue.toSong()
                if (
                    mediaController.duration > 0
                    && mediaController.duration != TIME_UNSET
                ){
                    _currentMediaDurationInMs.value = mediaController.duration
                }
            }
        }
    }
    private fun addPlaylistOfAudiosToPlayer(audios:List<Song>){
        viewModelScope.launch {
            while(mediaController == null){
                delay(100)
            }
        }
        val mediaItems = audios.map { item->
            val metadata = item.toMediaMetaItem()
            MediaItem.Builder().apply {
                setMediaId(item.songId)
                setUri(item.songPath)
                setMediaMetadata(metadata)
            }.build()
        }
        mediaController?.addMediaItems(mediaItems)
        mediaController?.prepare()
        mediaController?.pause()
    }
    fun getEvent(event:PlayerEvents){
        try {
            when(event){
                is PlayerEvents.AddPlayList -> {
                    if (mediaController == null){
                        setMediaControllerToConnectToMediaSessionService()
                        viewModelScope.launch {
                            delay(500)
                            addPlaylistOfAudiosToPlayer(event.songs)
                        }
                    }else{
                        addPlaylistOfAudiosToPlayer(event.songs)
                    }
                }
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
    companion object{
        private const val KEY_SONG_PATH = "KEY_SONG_PATH"
        private const val MIME_TYPE_MP3 = "mp3"
        private const val ONE_SECOND = 1000L
    }
}