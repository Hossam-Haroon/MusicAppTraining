package com.example.musicapptraining.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ArtistEntity(
    @PrimaryKey
    val artistName: String,
    val artistSongs : MutableList<SongEntity>
)
