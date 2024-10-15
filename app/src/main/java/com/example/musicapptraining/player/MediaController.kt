package com.example.musicapptraining.player

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
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

class MediaController(
    val player : ExoPlayer,
    val currentSong : MutableStateFlow<Song>,
    val currentMediaPosition: MutableStateFlow<Float>,
    val currentMediaDurationInMinutes: MutableStateFlow<Long>,
    val currentMediaProgressInMinutes : MutableStateFlow<Long>,
    val isPausePlayClicked : MutableStateFlow<Boolean>,
    val isRepeatingClicked : MutableStateFlow<Boolean>,
    val isBufferingClicked : MutableStateFlow<Boolean>,
    val isShufflingClicked: MutableStateFlow<Boolean>,
    val viewModelScope: CoroutineScope,
    val sharedPreferences: SharedPreferences,
    val currentMediaPositionInList: MutableStateFlow<Float>
    ): Player.Listener{

        var duration : Long = 0
        var songIdToPlayNext : String = ""
        private lateinit var controller : ListenableFuture<MediaController>


        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            currentMediaPosition.value = 0f

            /*if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO && songIdToPlayNext != "") {
                getTrackIndexById(songIdToPlayNext)
                songIdToPlayNext = ""

                /*if (mediaItem != null) {
                    currentSong.value = toMusicItem(mediaItem)
                    saveFloatValue(player.currentMediaItemIndex.toFloat())
                    currentMediaPositionInList.value = player.currentMediaItemIndex.toFloat()

                    // Update duration for the new media item
                    duration = player.duration
                    if (duration == C.TIME_UNSET) duration = 0
                    currentMediaDurationInMinutes.value = duration

                    // Reset progress
                    currentMediaProgressInMinutes.value = 0L
                    updatePlayerProgress(0L)
                }

                getTrackIndexById(songIdToPlayNext)
                songIdToPlayNext = ""

            }*/


            }*/
            if (mediaItem != null){
                currentSong.value = toMusicItem(mediaItem)
                saveFloatValue(player.currentMediaItemIndex.toFloat())
                currentMediaPositionInList.value = player.currentMediaItemIndex.toFloat()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            isPausePlayClicked.value = isPlaying
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)

            when(playbackState){
                Player.STATE_ENDED ->{
                    if (player.hasNextMediaItem()) {
                        nextItem()
                        saveFloatValue(player.currentMediaItemIndex.toFloat())
                    }

                }
                Player.STATE_IDLE -> {
                    currentMediaDurationInMinutes.value = 0L
                    currentMediaProgressInMinutes.value = 0L
                    isBufferingClicked.value = false

                }
                Player.STATE_BUFFERING->{
                    isBufferingClicked.value = true
                }
                Player.STATE_READY -> {
                    isBufferingClicked.value = false
                   // updateMediaInfo()

                }
            }
        }
     fun updateMediaInfo() {
        player.currentMediaItem?.let { mediaItem ->
            currentSong.value = toMusicItem(mediaItem)
            duration = player.duration
            if (duration == C.TIME_UNSET) duration = 0
            currentMediaDurationInMinutes.value = duration
            currentMediaProgressInMinutes.value = player.currentPosition
            currentMediaPosition.value = if (duration > 0) player.currentPosition.toFloat() / duration.toFloat() else 0f
            currentMediaPositionInList.value = player.currentMediaItemIndex.toFloat()
            saveFloatValue(player.currentMediaItemIndex.toFloat())
        }
    }

    fun getTrackIndexById(itemId: String): Int{
        for (i in 0 until player.mediaItemCount){
            val mediaItem = player.getMediaItemAt(i)
            if (mediaItem.mediaId.equals(itemId)){
                moveToSpecificItem(i)
                return i
            }

        }
        return -1
    }

    fun addPlayList(songs: List<Song>, updateListRequired: Boolean){
        if (updateListRequired){
            for (item in songs){
                val metaData = getMediaMetaDataFromItem(item)
                val mediaItem = MediaItem.Builder().apply {
                    setUri(item.songPath)
                    setMediaId(item.songId)
                    setMediaMetadata(metaData)
                }.build()
               player.addMediaItem(mediaItem)
            }
            player.prepare()
            player.pause()
        }else{
            if (player.mediaItemCount <= 0){
                for (item in songs){
                    val metaData = getMediaMetaDataFromItem(item)
                    val mediaItem = MediaItem.Builder().apply {
                        setMediaId(item.songId)
                        setUri(item.songPath)
                        setMediaMetadata(metaData)
                    }.build()
                    player.addMediaItem(mediaItem)
                }
                player.prepare()
                player.pause()
            }
        }
    }

    fun updatePlayerProgress(playerProgress : Long){
        currentMediaProgressInMinutes.value = playerProgress
        val progress = playerProgress.toFloat()/duration.toFloat()
        if (!progress.isNaN()) currentMediaPosition.value = progress
    }

    fun pauseOrPlay(){
        if (player.isPlaying){
            player.pause()
        }else{
            player.play()
        }
        currentSong.value = toMusicItem(player.currentMediaItem!!)
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




    fun nextItem(){
        if (player.hasNextMediaItem()){
            player.seekToNextMediaItem()
            currentMediaPositionInList.value = player.currentMediaItemIndex.toFloat()
        }
    }

    fun previousItem(){
        if (player.hasPreviousMediaItem()){
            player.seekToPreviousMediaItem()
            currentMediaPositionInList.value = player.currentMediaItemIndex.toFloat()

        }
    }

    fun moveToSpecificItem(index:Int){
        player.seekTo(index,0L)
        player.play()
        currentSong.value = toMusicItem(player.currentMediaItem!!)
        saveFloatValue(player.currentMediaItemIndex.toFloat())
        currentMediaPositionInList.value = player.currentMediaItemIndex.toFloat()
    }

    fun moveToSpecificPosition(position:Long){
        player.seekTo(position)
    }

    fun saveFloatValue(value : Float){
        val editor= sharedPreferences.edit()
        editor.putFloat(LAST_PLAYED_VALUE,value)
        editor.apply()
    }


    fun seekForward(){
        player.seekForward()
    }
    fun seekBackward(){
        player.seekBack()
    }
    fun clearPlayer(){
        player.stop()
        player.clearMediaItems()
    }

    private fun getMediaMetaDataFromItem(song : Song):MediaMetadata{
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

    fun setSongToPlayNext(songId: String){
        songIdToPlayNext = songId
    }

    fun setupMediaNotification(context : Context){
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        controller =MediaController.Builder(context, sessionToken).buildAsync()
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

    private fun toMusicItem(mediaItem: MediaItem): Song{
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




    }
