package com.example.musicapptraining.data.utils

import com.example.musicapptraining.data.entities.SongEntity
import com.example.musicapptraining.data.mappers.toDomain
import com.example.musicapptraining.domain.utils.DataFetcher
import com.example.musicapptraining.domain.model.Song

object SongEntityMock {
    val validSongEntity = SongEntity(songId = "1",
        songName = "Hello",
        songPath = "",
        songArtist = "", songDuration = 0L, songAlbum = "",
        songDateAdded = 0L, songArt = "", songMimeType = ""
    )
    val validSongModel = Song(
        songId = "1",
        songName = "Hello",
        songPath = "",
        songArtist = "", songDuration = 0L, songAlbum = "",
        songDateAdded = 0L, songArt = "", songMimeType = ""
    )
    val secondValidSongEntity = SongEntity(
        songId = "2",
        songName = "Hi",
        songPath = "",
        songArtist = "", songDuration = 0L, songAlbum = "",
        songDateAdded = 0L, songArt = "", songMimeType = ""
    )
    val invalidSongEntity = SongEntity(songId = "1",
        songName = "Hi",
        songPath = "",
        songArtist = "", songDuration = 0L, songAlbum = "",
        songDateAdded = 0L, songArt = "", songMimeType = ""
    )
    val fakeAudioFetcher = object : DataFetcher<Song> {
        override fun fetchDataFromDevice(): List<Song> {
            return listOf(validSongEntity.toDomain())
        }
    }
}