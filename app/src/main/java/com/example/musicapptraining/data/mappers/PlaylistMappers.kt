package com.example.musicapptraining.data.mappers

import com.example.musicapptraining.data.entities.PlaylistEntity
import com.example.musicapptraining.domain.model.Playlist

fun PlaylistEntity.toDomain():Playlist{
    return Playlist(
        this.playlistName,
        this.playlistSongs.toDomain().toMutableList()
    )
}
fun Playlist.toEntity():PlaylistEntity{
    return PlaylistEntity(
        this.playlistName,
        this.playlistSongs.toEntity().toMutableList()
    )
}
fun List<PlaylistEntity>.toDomain():List<Playlist> = this.map { it.toDomain() }
fun List<Playlist>.toEntity():List<PlaylistEntity> = this.map { it.toEntity() }