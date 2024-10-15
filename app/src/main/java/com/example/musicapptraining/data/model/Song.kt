package com.example.musicapptraining.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity
data class Song(
    @PrimaryKey
    var songId : String,
    val songName : String,
    val songPath : String,
    val songArtist : String,
    val songDuration : Long,
    val songAlbum : String,
    val songDateAdded : Long,
    val songArt : String?,
    val songMimeType : String

):Parcelable
