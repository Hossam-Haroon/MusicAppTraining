package com.example.musicapptraining.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SongEntity(
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
)
