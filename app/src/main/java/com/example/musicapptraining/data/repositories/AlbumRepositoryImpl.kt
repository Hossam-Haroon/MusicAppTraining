package com.example.musicapptraining.data.repositories

import com.example.musicapptraining.data.mappers.toDomain
import com.example.musicapptraining.data.mappers.toEntity
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.domain.utils.DataFetcher
import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.repositories.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject


class AlbumRepositoryImpl @Inject constructor(
    private val musicDao: MusicDao,
    private val albumFetcher: DataFetcher<Album>
):AlbumRepository {
    override fun getAllAlbums(): Flow<List<Album>> {
        return musicDao.getAllAlbums()
            .map { it.toDomain() }
            .onStart {
                val albums = musicDao.getAllAlbums().first()
                if (albums.isEmpty()){
                    val fetchedAlbums = albumFetcher.fetchDataFromDevice().toEntity()
                    musicDao.insertAllAlbums(fetchedAlbums)
                }
            }
    }
    override fun searchAlbumByName(text: String):Flow<List<Album>> {
        return flow{
            val cachedAlbum = musicDao.searchAlbumName(text).toDomain()
            if (cachedAlbum.isNotEmpty()){
                emit(cachedAlbum)
                return@flow
            }
        }
    }
    override fun getAlbumSongs(albumName: String): Flow<Album> {
        return musicDao.getAlbumByName(albumName).map { it.toDomain() }
    }
    companion object{
        private const val ERROR_MESSAGE = "Error Fetching Music"
    }
}