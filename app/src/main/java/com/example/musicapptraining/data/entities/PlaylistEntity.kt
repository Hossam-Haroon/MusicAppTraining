package com.example.musicapptraining.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaylistEntity(
    @PrimaryKey
    val playlistName : String,
    val playlistSongs : MutableList<SongEntity>
)
