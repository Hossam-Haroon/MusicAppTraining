package com.example.musicapptraining.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Artist(
    val artistName: String,
    val artistSongs : MutableList<Song>
):Parcelable
