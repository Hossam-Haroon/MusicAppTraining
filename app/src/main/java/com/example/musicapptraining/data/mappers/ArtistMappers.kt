package com.example.musicapptraining.data.mappers

import com.example.musicapptraining.data.entities.ArtistEntity
import com.example.musicapptraining.domain.model.Artist

fun ArtistEntity.toDomain():Artist{
    return Artist(
        this.artistName,
        this.artistSongs.toDomain().toMutableList()
    )
}
fun Artist.toEntity():ArtistEntity{
    return ArtistEntity(
        this.artistName,
        this.artistSongs.toEntity().toMutableList()
    )
}
fun List<ArtistEntity>.toDomain():List<Artist> = this.map { it.toDomain() }
fun List<Artist>.toEntity():List<ArtistEntity> = this.map { it.toEntity() }