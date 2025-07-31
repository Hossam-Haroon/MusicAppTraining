package com.example.musicapptraining.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val songId: String,
    val songName: String,
    val songPath: String,
    val songArtist: String,
    val songDuration: Long,
    val songAlbum: String,
    val songDateAdded: Long,
    val songArt: String?,
    val songMimeType: String
) : Parcelable
