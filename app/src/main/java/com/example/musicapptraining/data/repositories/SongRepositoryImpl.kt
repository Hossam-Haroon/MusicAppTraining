package com.example.musicapptraining.data.repositories

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.musicapptraining.data.mappers.toDomain
import com.example.musicapptraining.data.mappers.toEntity
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.domain.utils.DataFetcher
import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.repositories.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val musicDao: MusicDao,
    private val context: Context,
    private val audioFetcher: DataFetcher<Song>
):SongRepository {
    override fun getAllSongs(): Flow<List<Song>> {
        return musicDao.getAllSongs()
            .map { it.toDomain() }
            .onStart {
                val songs = musicDao.getAllSongs().first()
                if (songs.isEmpty()){
                    val fetchedSongs = audioFetcher.fetchDataFromDevice().toEntity()
                    musicDao.deleteSongs()
                    musicDao.insertAllSongs(fetchedSongs)
                }
            }
    }
    override fun searchSong(songName: String): Flow<List<Song>> {
        return musicDao.searchSongsName(songName).map{it.toDomain()}
    }
    override suspend fun checkAndRefresh(): List<Song> {
        val deviceSongs = audioFetcher.fetchDataFromDevice()
        val localSongs = musicDao.getAllSongs().first().toDomain()
        val sortedDeviceSongs = deviceSongs.sortedBy { it.songId }
        val sortedLocalSongs = localSongs.sortedBy { it.songId }
        return checkIfLocalDataBaseHasTheSameDataAsTheDevice(sortedLocalSongs, sortedDeviceSongs)
    }
    private suspend fun checkIfLocalDataBaseHasTheSameDataAsTheDevice(
        localSongs: List<Song>,
        deviceSongs:List<Song>
    ): List<Song> {
        val localSongsId = localSongs.map { it.songId }.toSet()
        val deviceSongsId = deviceSongs.map { it.songId }.toSet()
        return if (localSongsId != deviceSongsId) {
            musicDao.deleteSongs()
            musicDao.insertAllSongs(deviceSongs.toEntity())
            deviceSongs
        }else{
            localSongs
        }
    }
    private fun isPermissionGranted(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return ContextCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    companion object{
        private const val ERROR_MESSAGE = "Error Fetching Music"
        private const val PERMISSION_DISALLOWED = "Permission is not allowed"
    }
}