package com.example.musicapptraining.data.source

import androidx.room.TypeConverter
import com.example.musicapptraining.data.entities.ArtistEntity
import com.example.musicapptraining.data.entities.SongEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun convertSongListIntoJsonString(songList: List<SongEntity>):String = Gson().toJson(songList)

    @TypeConverter
    fun convertJsonStringIntoSongList(jsonString: String): List<SongEntity> {
        val list = object : TypeToken<List<SongEntity>>() {}.type
       return Gson().fromJson(jsonString, list)
    }
    @TypeConverter
    fun convertArtistListIntoJsonString(
        artistList : List<ArtistEntity>
    ): String = Gson().toJson(artistList)

    @TypeConverter
    fun convertJsonStringIntoArtistList(jsonString: String): List<ArtistEntity>{
        val list = object : TypeToken<List<ArtistEntity>>() {}.type
        return Gson().fromJson(jsonString,list)
    }
}