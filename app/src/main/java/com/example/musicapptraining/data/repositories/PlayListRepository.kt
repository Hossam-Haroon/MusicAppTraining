package com.example.musicapptraining.data.repositories
import android.content.Context
import android.util.Log
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlayListRepository @Inject constructor(
    val musicDao: MusicDao,
    val context: Context
) {
    fun getPlayLists():Flow<UiState<List<PlayList>>> =
         flow {
             emit(UiState.Loading)
             try {
                 val allPlayLists = musicDao.getAllPlaylists()
                 val checkedList = checkIfPlaylistsAreEmptyOrNot(allPlayLists)
                 emit(UiState.Success(checkedList))
             }catch (e:Exception){
                 e.message?.let {
                     emit(UiState.Error(it))
                 }
             }
        }
    private suspend fun checkIfPlaylistsAreEmptyOrNot(
        playlists: Flow<List<PlayList>>
    ):List<PlayList>{
        val list = playlists.first()
        return list.ifEmpty {
            makeDefaultPlaylists()
        }
    }
    private suspend fun makeDefaultPlaylists():List<PlayList>{
        val defaultPlaylists = listOf(
            PlayList(LIKED_AUDIOS, mutableListOf()),
            PlayList(RECENTLY_PLAYED, mutableListOf())
        )
        defaultPlaylists.forEach {
            Log.d(
                PLAYLIST_REPOSITORY,
                "Inserting playlist: ${it.playlistName}"
            )
            musicDao.insertPlayList(it)
        }
        return defaultPlaylists
    }
    fun addNewPlayList(playlistName : String){
        CoroutineScope(Dispatchers.IO).launch {
            musicDao.insertPlayList(PlayList(playlistName, mutableListOf()))
        }
    }
    suspend fun addSongToPlayList(song : Song, playList: PlayList){
        withContext(Dispatchers.IO){
            val updatedSongs = playList.playlistSongs.toMutableList().apply { add(song) }
            musicDao.insertPlayList(playList.copy(playlistSongs = updatedSongs))
        }
    }
    fun getLikedPlaylist(): Flow<UiState<PlayList>>{
        return flow {
            emit(UiState.Loading)
            try {
                val likedPlaylist = musicDao.getPlayListByName(LIKED_AUDIOS)
                emit(UiState.Success(likedPlaylist))
            }catch (e:Exception){
                e.message?.let {
                    emit(UiState.Error(it))
                }
            }
        }
    }
    suspend fun deleteSongFromPlayList(song: Song, playList: PlayList){
        withContext(Dispatchers.IO) {
            val updatedSongs = playList.playlistSongs.toMutableList().apply { remove(song) }
            musicDao.insertPlayList(playList.copy(playlistSongs = updatedSongs))
        }
    }
    companion object{
        private const val PLAYLIST_REPOSITORY = "playlist repository"
        private const val LIKED_AUDIOS = "liked"
        private const val RECENTLY_PLAYED = "recently played"
    }
}