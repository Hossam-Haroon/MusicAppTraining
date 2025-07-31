package com.example.musicapptraining.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.musicapptraining.data.entities.AlbumEntity
import com.example.musicapptraining.data.entities.ArtistEntity
import com.example.musicapptraining.data.entities.PlaylistEntity
import com.example.musicapptraining.data.entities.SongEntity

@Database(
    entities = [SongEntity::class, ArtistEntity::class, AlbumEntity::class, PlaylistEntity::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class MusicDatabase : RoomDatabase(){
    abstract fun musicDao() : MusicDao
}