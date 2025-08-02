package com.example.musicapptraining.ui.mappers

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.ui.mappers.Util.KEY_SONG_PATH
import com.example.musicapptraining.ui.mappers.Util.MIME_TYPE_MP3

fun MediaItem.toSong(): Song {
    val songPath = this.mediaMetadata.extras?.getString(KEY_SONG_PATH) ?: ""
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
}
fun Song.toMediaMetaItem(): MediaMetadata {
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
object Util{
     const val KEY_SONG_PATH = "KEY_SONG_PATH"
     const val MIME_TYPE_MP3 = "mp3"
}