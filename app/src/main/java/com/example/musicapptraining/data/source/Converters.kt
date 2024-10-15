package com.example.musicapptraining.data.source

import androidx.room.TypeConverter
import com.example.musicapptraining.data.model.Artist
import com.example.musicapptraining.data.model.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun convertSongListIntoJsonString(songList: List<Song>):String = Gson().toJson(songList)

    @TypeConverter
    fun convertJsonStringIntoSongList(jsonString: String): List<Song> {
        val list = object : TypeToken<List<Song>>() {}.type
       return Gson().fromJson(jsonString, list)
    }


    @TypeConverter
    fun convertArtistListIntoJsonString(artistList : List<Artist>): String = Gson().toJson(artistList)

    @TypeConverter
    fun convertJsonStringIntoArtistList(jsonString: String): List<Artist>{
        val list = object : TypeToken<List<Artist>>() {}.type
        return Gson().fromJson(jsonString,list)
    }
}