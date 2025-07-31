package com.example.musicapptraining.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val playlistName : String,
    val playlistSongs : MutableList<Song>
):Parcelable
