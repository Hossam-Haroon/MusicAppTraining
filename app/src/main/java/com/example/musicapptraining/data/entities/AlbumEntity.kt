package com.example.musicapptraining.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlbumEntity(
    val albumName : String,
    @PrimaryKey
    val albumID : String,
    val albumArt : String?,
    val albumSongs : MutableList<SongEntity>,
    val albumCreator : String
)
