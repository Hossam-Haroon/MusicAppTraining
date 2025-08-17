package com.example.musicapptraining.data.mappers

import com.example.musicapptraining.data.utils.SongEntityMock.validSongEntity
import com.example.musicapptraining.data.utils.SongEntityMock.validSongModel
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class SongMappersKtTest{

    @Test
    fun `Song model should return song entity while using toEntity()`(){
        //Given
        val songEntity = validSongEntity
        val songModel = validSongModel
        //When
        val toEntity = songModel.toEntity()
        //Then
        assertThat(toEntity).isEqualTo(songEntity)
    }
    @Test
    fun `song entity should return song model while using toDomain()`(){
        //Given
        val songEntity = validSongEntity
        val songModel = validSongModel
        //When
        val toModel = songEntity.toDomain()
        //Then
        assertThat(toModel).isEqualTo(songModel)
    }
    @Test
    fun `list of song models should return list of song entities while using toEntity()`(){
        //Given
        val songEntities = listOf(validSongEntity)
        val songModels = listOf(validSongModel)
        //When
        val toEntity = songModels.toEntity()
        //Then
        assertThat(toEntity).isEqualTo(songEntities)
    }
    @Test
    fun `list of song entities should return list of song models while using toDomain()`(){
        //Given
        val songEntities = listOf(validSongEntity)
        val songModels = listOf(validSongModel)
        //When
        val toDomain = songEntities.toDomain()
        //Then
        assertThat(toDomain).isEqualTo(songModels)
    }
}