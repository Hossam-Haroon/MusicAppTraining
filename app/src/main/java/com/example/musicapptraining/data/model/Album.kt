package com.example.musicapptraining.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity
data class Album(
    val albumName : String,
    @PrimaryKey
    val albumID : String,
    val albumArt : String?,
    val albumSongs : MutableList<Song>,
    val albumCreator : String
): Parcelable
