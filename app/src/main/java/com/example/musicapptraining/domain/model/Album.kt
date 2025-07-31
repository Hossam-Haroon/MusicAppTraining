package com.example.musicapptraining.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    val albumName : String,
    val albumID : String,
    val albumArt : String?,
    val albumSongs : MutableList<Song>,
    val albumCreator : String
):Parcelable
