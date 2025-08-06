package com.example.musicapptraining.presentation.musicPlayer
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.mappers.toMediaMetaItem
import com.example.musicapptraining.presentation.mappers.toSong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlayerController @Inject constructor(
    private val playbackStateManager: PlaybackStateManager
) {
     var songIdToPlayNext = ""
     fun togglePlayback(mediaController: MediaController?) {
         mediaController?.let { controller ->
            if (controller.isPlaying) controller.pause() else controller.play()
        }
    }
     fun moveToSpecificPosition(position: Long,mediaController: MediaController?) {
         mediaController?.seekTo(position)
    }
     fun seekForward(mediaController: MediaController?) {
         mediaController?.seekForward()
    }
     fun seekBackward(mediaController: MediaController?) {
         mediaController?.seekBack()
    }
     fun clearPlayer(mediaController: MediaController?) {
         mediaController?.stop()
         mediaController?.clearMediaItems()
    }
     fun shuffleButtonClicked(mediaController: MediaController?) {
        when(playbackStateManager.isShufflingClicked.value){
            true ->{
                playbackStateManager.updateShufflingState(false)
                mediaController?.shuffleModeEnabled =
                    playbackStateManager.isShufflingClicked.value
            }
            else ->{
                playbackStateManager.updateShufflingState(true)
                mediaController?.shuffleModeEnabled =
                    playbackStateManager.isShufflingClicked.value
            }
        }
    }
     fun repeatButtonClicked(mediaController: MediaController?) {
        when(playbackStateManager.isRepeatingClicked.value){
            true -> {
                playbackStateManager.updateRepeatingState(false)
                mediaController?.repeatMode = Player.REPEAT_MODE_OFF
            }
            else->{
                playbackStateManager.updateRepeatingState(true)
                mediaController?.repeatMode = Player.REPEAT_MODE_ONE
            }
        }
    }
     fun seekToNextItem(mediaController: MediaController?) {
         mediaController?.let {
            if (it.hasNextMediaItem()) {
                it.seekToNextMediaItem()
                playbackStateManager.updateCurrentMediaPositionInList(it.currentMediaItemIndex)
            }
        }
    }
     fun seekToPreviousItem(mediaController: MediaController?) {
         mediaController?.let {
            if (it.hasPreviousMediaItem()){
                it.seekToPreviousMediaItem()
                playbackStateManager.updateCurrentMediaPositionInList(it.currentMediaItemIndex)
            }
        }
    }
     fun updatePlayerProgress(playerProgress: Long) {
         playbackStateManager.updateCurrentMediaProgressInMs(playerProgress)
    }
     fun setSongToPlayNext(songId : String){
        songIdToPlayNext = songId
    }
     fun getTrackIndexById(songId: String,mediaController: MediaController?):Int{
         mediaController?.let {
            for (i in 0 until it.mediaItemCount){
                val mediaItem = it.getMediaItemAt(i)
                if (mediaItem.mediaId == songId){
                    moveToSpecificItem(i,mediaController)
                    return i
                }
            }
        }
        return -1
    }
     fun moveToSpecificItem(itemIndex : Int,mediaController: MediaController?){
        mediaController?.let {controller->
            controller.apply {
                seekTo(itemIndex,0L)
                play()
                playbackStateManager.updateCurrentSongState(currentMediaItem!!.toSong())
                playbackStateManager.updateCurrentMediaPositionInList(currentMediaItemIndex)
            }
        }
    }
     fun addPlaylistOfAudiosToPlayer(audios:List<Song>,mediaController: MediaController?){
         if (mediaController == null) {
             Log.e("MediaPlayerController", "MediaController is null, waiting...")
             return
         }
         mediaController.let {controller->
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
         }
    }
}