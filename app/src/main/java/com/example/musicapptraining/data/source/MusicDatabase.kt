package com.example.musicapptraining.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.data.model.Artist
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.data.model.Song

@Database(
    entities = [Song::class, Artist::class, Album::class, PlayList::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class MusicDatabase : RoomDatabase(){
    abstract fun musicDao() : MusicDao
}