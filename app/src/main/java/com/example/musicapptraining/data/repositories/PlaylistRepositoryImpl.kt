package com.example.musicapptraining.data.repositories

import android.util.Log
import com.example.musicapptraining.data.entities.PlaylistEntity
import com.example.musicapptraining.data.mappers.toDomain
import com.example.musicapptraining.data.mappers.toEntity
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.repositories.PlaylistRepository
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val musicDao: MusicDao
):PlaylistRepository {
    override fun getPlayLists(): Flow<UiState<List<Playlist>>> = flow {
        emit(UiState.Loading)
        try {
            val allPlayLists = musicDao.getAllPlaylists()
            val checkedList = checkIfPlaylistsAreEmptyOrNot(allPlayLists.map {
                it.toDomain()
            })
            emit(UiState.Success(checkedList))
        }catch (e:Exception){
            e.message?.let {
                emit(UiState.Error(it))
            }
        }
    }
    override suspend fun addNewPlayList(playlistName: String) {
        musicDao.insertPlayList(PlaylistEntity(playlistName, mutableListOf()))
    }
    override suspend fun addSongToPlayList(song: Song, playList: Playlist) {
        val updatedSongs = playList.playlistSongs.toMutableList().apply { add(song) }
        musicDao.insertPlayList(playList.copy(playlistSongs = updatedSongs).toEntity())
    }
    override fun getLikedPlaylist(): Flow<UiState<Playlist>> {
        return flow {
            emit(UiState.Loading)
            try {
                val likedPlaylist = musicDao.getPlayListByName(LIKED_AUDIOS)?.toDomain()
                likedPlaylist?.let {
                    emit(UiState.Success(it))
                } ?: emit(UiState.Error("Liked playlist not found"))
            }catch (e:Exception){
                e.message?.let {
                    emit(UiState.Error(it))
                }
            }
        }
    }
    override suspend fun deleteSongFromPlayList(song: Song, playList: Playlist) {
        val updatedSongs = playList.playlistSongs.toMutableList().apply { remove(song) }
        musicDao.insertPlayList(playList.copy(playlistSongs = updatedSongs).toEntity())
    }
    private suspend fun checkIfPlaylistsAreEmptyOrNot(
        playlists: Flow<List<Playlist>>
    ):List<Playlist>{
        val list = playlists.first()
        return list.ifEmpty {
            makeDefaultPlaylists()
        }
    }
    private suspend fun makeDefaultPlaylists():List<Playlist>{
        val defaultPlaylists = listOf(
            Playlist(LIKED_AUDIOS, mutableListOf()),
            Playlist(RECENTLY_PLAYED, mutableListOf())
        )
        defaultPlaylists.forEach {
            Log.d(
                PLAYLIST_REPOSITORY,
                "Inserting playlist: ${it.playlistName}"
            )
            musicDao.insertPlayList(it.toEntity())
        }
        return defaultPlaylists
    }
    companion object{
        private const val PLAYLIST_REPOSITORY = "playlist repository"
        private const val LIKED_AUDIOS = "liked"
        private const val RECENTLY_PLAYED = "recently played"
    }
}