package com.example.musicapptraining.data.mappers

import com.example.musicapptraining.data.entities.SongEntity
import com.example.musicapptraining.domain.model.Song

fun SongEntity.toDomain(): Song {
    return Song(
        songId = songId,
        songName = songName,
        songPath = songPath,
        songArtist = songArtist,
        songDuration = songDuration,
        songAlbum = songAlbum,
        songDateAdded = songDateAdded,
        songArt = songArt,
        songMimeType = songMimeType
    )
}
fun Song.toEntity(): SongEntity {
    return SongEntity(
        songId = songId,
        songName = songName,
        songPath = songPath,
        songArtist = songArtist,
        songDuration = songDuration,
        songAlbum = songAlbum,
        songDateAdded = songDateAdded,
        songArt = songArt,
        songMimeType = songMimeType
    )
}
fun List<SongEntity>.toDomain():List<Song> = this.map { it.toDomain() }
fun List<Song>.toEntity():List<SongEntity> = this.map { it.toEntity() }