package com.example.musicapptraining.data.mappers

import com.example.musicapptraining.data.entities.AlbumEntity
import com.example.musicapptraining.domain.model.Album

fun AlbumEntity.toDomain():Album{
    return Album(
        this.albumName,
        this.albumID,
        this.albumArt,
        this.albumSongs.toDomain().toMutableList(),
        this.albumCreator
    )
}
fun Album.toEntity():AlbumEntity{
    return AlbumEntity(
        this.albumName,
        this.albumID,
        this.albumArt,
        this.albumSongs.toEntity().toMutableList(),
        this.albumCreator,
    )
}
fun List<AlbumEntity>.toDomain():List<Album> = this.map { it.toDomain() }
fun List<Album>.toEntity():List<AlbumEntity> = this.map { it.toEntity() }