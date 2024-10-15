package com.example.musicapptraining.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity
data class PlayList(
    @PrimaryKey
    val playlistName : String,
    val playlistSongs : MutableList<Song>
): Parcelable
