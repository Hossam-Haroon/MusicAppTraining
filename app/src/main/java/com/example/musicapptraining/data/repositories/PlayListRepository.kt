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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayListRepository @Inject constructor(
    val musicDao: MusicDao,
    val context: Context
) {

    fun getPlayLists():Flow<UiState<List<PlayList>>> =
         flow {
            emit(UiState.Loading)
            val allPlayLists = musicDao.getAllPlaylists()
            allPlayLists.collect{playlists->
                if (playlists.isEmpty()){
                    val defaultPlaylists = listOf(
                        PlayList("liked", mutableListOf()),
                        PlayList("recently played", mutableListOf())
                    )
                    defaultPlaylists.forEach {
                        Log.d("PlayListRepository", "Inserting playlist: ${it.playlistName}")
                        musicDao.insertPlayList(it) }
                    emit(UiState.Success(defaultPlaylists))

                }else{
                    emit(UiState.Success(playlists))

                }
            }
        }.flowOn(Dispatchers.IO)


    fun addNewPlayList(playlistName : String){
        CoroutineScope(Dispatchers.IO).launch {
            musicDao.insertPlayList(PlayList(playlistName, mutableListOf()))
        }
    }

    fun addSongToPlayList(song : Song, playList: PlayList){
        CoroutineScope(Dispatchers.IO).launch {
            playList.playlistSongs.add(song)
            musicDao.insertPlayList(playList)
        }
    }

    fun getLikedPlaylist(): Flow<UiState<PlayList>>{
        return flow {
            emit(UiState.Loading)
            val likedPlaylist = musicDao.getPlayListByName("liked")
            emit(UiState.Success(likedPlaylist))
        }.flowOn(Dispatchers.IO)
    }

    fun deleteSongFromPlayList(song: Song,playList: PlayList){
        CoroutineScope(Dispatchers.IO).launch {
            playList.playlistSongs.remove(song)
            musicDao.insertPlayList(playList)
        }
    }
}