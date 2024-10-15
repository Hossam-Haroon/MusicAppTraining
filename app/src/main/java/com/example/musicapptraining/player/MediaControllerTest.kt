package com.example.musicapptraining.player

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.utilities.Constants.LAST_PLAYED_VALUE
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MediaControllerTest(
    val player: ExoPlayer,
    val currentSong: MutableStateFlow<Song>,
    val currentMediaPosition: MutableStateFlow<Float>,
    val currentMediaDurationInMinutes: MutableStateFlow<Long>,
    val currentMediaProgressInMinutes: MutableStateFlow<Long>,
    val isPausePlayClicked: MutableStateFlow<Boolean>,
    val isRepeatingClicked: MutableStateFlow<Boolean>,
    val isBufferingClicked: MutableStateFlow<Boolean>,
    val isShufflingClicked: MutableStateFlow<Boolean>,
    val viewModelScope: CoroutineScope,
    val sharedPreferences: SharedPreferences,
    val currentMediaPositionInList: MutableStateFlow<Float>
) : Player.Listener {

    var duration: Long = 0
    var songIdToPlayNext: String = ""
    private lateinit var controller : ListenableFuture<androidx.media3.session.MediaController>

    init {
        player.addListener(this)
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        updateMediaInfo()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState) {
            Player.STATE_READY -> {
                isBufferingClicked.value = false
                updateMediaInfo()
            }
            Player.STATE_ENDED -> {
                if (player.hasNextMediaItem()) {
                    nextItem()
                    saveFloatValue(player.currentMediaItemIndex.toFloat())
                }
            }
            Player.STATE_BUFFERING -> {
                isBufferingClicked.value = true
            }
            Player.STATE_IDLE -> {
                currentMediaDurationInMinutes.value = 0L
                currentMediaProgressInMinutes.value = 0L
                isBufferingClicked.value = false
            }
        }
    }

    private fun updateMediaInfo() {
        player.currentMediaItem?.let { mediaItem ->
            currentSong.value = toMusicItem(mediaItem)
            updateDuration()
            currentMediaProgressInMinutes.value = player.currentPosition
            currentMediaPosition.value = if (duration > 0) player.currentPosition.toFloat() / duration.toFloat() else 0f
            currentMediaPositionInList.value = player.currentMediaItemIndex.toFloat()
            saveFloatValue(player.currentMediaItemIndex.toFloat())
        }
    }

    private fun updateDuration() {
        duration = player.duration
        if (duration == C.TIME_UNSET) {
            viewModelScope.launch {
                while (duration == C.TIME_UNSET) {
                    delay(100)
                    duration = player.duration
                }
                currentMediaDurationInMinutes.value = duration
            }
        } else {
            currentMediaDurationInMinutes.value = duration
        }
    }

    fun addPlayList(songs: List<Song>, updateListRequired: Boolean) {
        if (updateListRequired) {
            player.clearMediaItems()
        }
        if (player.mediaItemCount <= 0 || updateListRequired) {
            for (item in songs) {
                val metaData = getMediaMetaDataFromItem(item)
                val mediaItem = MediaItem.Builder().apply {
                    setUri(item.songPath)
                    setMediaId(item.songId)
                    setMediaMetadata(metaData)
                }.build()
                player.addMediaItem(mediaItem)
            }
            player.prepare()
            updateMediaInfo()
        }
    }

    fun pauseOrPlay() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
            updateMediaInfo()
        }
        currentSong.value = toMusicItem(player.currentMediaItem!!)
        isPausePlayClicked.value = player.isPlaying
    }
    fun shuffleClick(){
        if (isShufflingClicked.value){
            isShufflingClicked.value = false
            player.shuffleModeEnabled = isShufflingClicked.value
        }else{
            isShufflingClicked.value = true
            player.shuffleModeEnabled = isShufflingClicked.value
        }

    }

    fun repeatClick(){
        if (isRepeatingClicked.value){
            isRepeatingClicked.value = false
            player.repeatMode = Player.REPEAT_MODE_OFF
        }else{
            isRepeatingClicked.value = true
            player.repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    fun setupMediaNotification(context : Context){
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        controller = MediaController.Builder(context,sessionToken).buildAsync()
        controller.addListener({
            val mediaController = controller.get()
            mediaController.addListener(object: Player.Listener{
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    currentSong.value = toMusicItem(player.currentMediaItem!!)
                    isPausePlayClicked.value = isPlaying
                    duration = mediaController.duration
                    if (duration == -9223372036854775807) duration = 0
                    currentMediaDurationInMinutes.value = duration
                    viewModelScope.launch {
                        while (isPausePlayClicked.value){
                            currentSong.value = toMusicItem(player.currentMediaItem!!)
                            updatePlayerProgress(player.currentPosition)
                            delay(1000)

                        }
                    }
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                    when(reason){
                        Player.DISCONTINUITY_REASON_SEEK -> {
                            updatePlayerProgress(newPosition.contentPositionMs)
                            player.seekTo(newPosition.contentPositionMs)
                        }
                        Player.DISCONTINUITY_REASON_AUTO_TRANSITION -> Unit
                        Player.DISCONTINUITY_REASON_SKIP -> Unit
                        Player.DISCONTINUITY_REASON_REMOVE -> Unit
                        Player.DISCONTINUITY_REASON_INTERNAL -> Unit
                        Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT -> Unit
                    }
                }

            })

        },
            MoreExecutors.directExecutor()

        )
    }
    fun updatePlayerProgress(playerProgress : Long){
        currentMediaProgressInMinutes.value = playerProgress
        val progress = playerProgress.toFloat()/duration.toFloat()
        if (!progress.isNaN()) currentMediaPosition.value = progress
    }

    // ... other methods ...

    fun moveToSpecificItem(index: Int) {
        player.seekTo(index, 0L)
        player.play()
        updateMediaInfo()
    }

    fun moveToSpecificPosition(position: Long) {
        player.seekTo(position)
        updateMediaInfo()
    }
    private fun toMusicItem(mediaItem: MediaItem): Song {
        val songPath =mediaItem.mediaMetadata.extras?.getString("KEY_SONG_PATH") ?: ""
        return Song(
            mediaItem.mediaId,
            mediaItem.mediaMetadata.displayTitle.toString(),
            songPath,
            mediaItem.mediaMetadata.artist.toString(),
            0,
            mediaItem.mediaMetadata.albumTitle.toString(),
            0,
            mediaItem.mediaMetadata.artworkUri.toString(),
            "mp3"
        )
    }
    private fun getMediaMetaDataFromItem(song : Song): MediaMetadata {
        val extras = Bundle().apply {
            putString("KEY_SONG_PATH", song.songPath)
        }
        return MediaMetadata.Builder()
            .setTitle(song.songName)
            .setDisplayTitle(song.songName)
            .setArtist(song.songArtist)
            .setAlbumArtist(song.songArtist)
            .setAlbumTitle(song.songAlbum)
            .setArtworkUri(Uri.parse(song.songArt))
            .setExtras(extras)
            .build()
    }
    fun saveFloatValue(value : Float){
        val editor= sharedPreferences.edit()
        editor.putFloat(LAST_PLAYED_VALUE,value)
        editor.apply()
    }
    fun nextItem(){
        if (player.hasNextMediaItem()){
            player.seekToNextMediaItem()
            currentMediaPositionInList.value = player.currentMediaItemIndex.toFloat()
        }
    }

    // ... rest of the class ...
}