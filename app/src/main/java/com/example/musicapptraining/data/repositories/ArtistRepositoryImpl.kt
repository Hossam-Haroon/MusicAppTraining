package com.example.musicapptraining.data.repositories

import com.example.musicapptraining.data.mappers.toDomain
import com.example.musicapptraining.data.mappers.toEntity
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.domain.utils.DataFetcher
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.repositories.ArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val musicDao: MusicDao,
    private val artistFetcher: DataFetcher<Artist>
):ArtistRepository {
    override fun getArtists(): Flow<List<Artist>> {
        return musicDao.getAllArtists()
            .map { it.toDomain() }
            .onStart {
                val artists = musicDao.getAllArtists().first()
                if (artists.isEmpty()){
                    val fetchedArtists = artistFetcher.fetchDataFromDevice().toEntity()
                    musicDao.insertAllArtists(fetchedArtists)
                }
            }
    }
    override fun searchArtistByName(text: String): Flow<List<Artist>> {
        return musicDao.searchArtistName(text).map{ it.toDomain() }
    }
    override fun getArtistSongs(artistName: String): Flow<Artist> {
        return musicDao.getArtistByName(artistName).map {it.toDomain()}
    }
    companion object{
        private const val ERROR_MESSAGE = "Error Fetching Music"
    }
}