package com.example.musicapptraining.data.repositories

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    val musicDao: MusicDao,
    val context : Context
) {
    val _songsGroupedByAlbum : MutableStateFlow<UiState<List<Album>>>
        =  MutableStateFlow(UiState.Loading)



    fun getAllAlbums(): Flow<UiState<List<Album>>>{
        return flow {

            val albums = musicDao.getAllAlbums()
            if (albums.isNotEmpty()){
                emit(UiState.Success(albums))
                return@flow
            }

            try {
                fetchAllAlbumsFromDevice()
                _songsGroupedByAlbum.collect{
                    if (it is UiState.Success){
                        musicDao.insertAllAlbums(it.data)
                        emit(it)
                    }
                }
            }catch (e : Exception){
                emit(UiState.Error("catching has failed"))
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun fetchAllAlbumsFromDevice() {
        val albumHashMap = HashMap<String,Album>()
        val albumList = mutableListOf<Album>()

        CoroutineScope(Dispatchers.IO).launch {
            _songsGroupedByAlbum.value = UiState.Loading
            val projection = arrayOf(
                Media.TITLE,
                Media._ID,
                Media.DISPLAY_NAME,
                Media.DATA,
                Media.ARTIST,
                Media.DURATION,
                Media.ALBUM,
                Media.DATE_ADDED,
                Media.MIME_TYPE
            )
            val selection = "${Media.IS_MUSIC} != 0 AND (" + "${Media.MIME_TYPE} = 'audio/mpeg' OR " +
                    "${Media.MIME_TYPE} = 'audio/mp4' OR " +
                    "${Media.MIME_TYPE} = 'audio/aac' OR " +
                    "${Media.MIME_TYPE} = 'audio/ogg')"

            val sortOrder = "${Media.ALBUM} ASC"

            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(
                Media.EXTERNAL_CONTENT_URI,projection,selection,null,sortOrder
            )

            cursor?.use {
                while (it.moveToNext()){
                    val songPath =
                        it.getString(it.getColumnIndexOrThrow(Media.DATA))
                    val songId = it.getString(it.getColumnIndexOrThrow(Media._ID))
                    val songName =
                        it.getString(it.getColumnIndexOrThrow(Media.DISPLAY_NAME))
                    val songArtist =
                        it.getString(it.getColumnIndexOrThrow(Media.ARTIST))
                    val songDuration =
                        it.getLong(it.getColumnIndexOrThrow(Media.DURATION))
                    val songAlbum =
                        it.getString(it.getColumnIndexOrThrow(Media.ALBUM))
                    val songDateAdded =
                        it.getLong(it.getColumnIndexOrThrow(Media.DATE_ADDED))
                    val songMimeType =
                        it.getString(it.getColumnIndexOrThrow(Media.MIME_TYPE))
                    val songArtUri =
                        getSongArtUri(songId.toLong())

                    val song = Song(
                        songId,
                        songName,
                        songPath,
                        songArtist,
                        songDuration,
                        songAlbum,
                        songDateAdded,
                        songArtUri.toString(),
                        songMimeType
                    )
                    val albumId =
                        it.getString(it.getColumnIndexOrThrow(Media.ALBUM_ID))
                    val albumArtUri = getSongArtUri(albumId.toLong())
                    if (!song.songPath.contains("opus") && !song.songName.contains("AUD")){
                        if (!albumHashMap.containsKey(albumId)){
                            albumHashMap[albumId] = Album(
                                songAlbum,
                                albumId,
                                albumArtUri.toString(),
                                mutableListOf(),
                                songArtist
                            )
                        }
                        albumHashMap[albumId]?.albumSongs?.add(song)
                    }
                }
            }
            albumList.addAll(albumHashMap.values)
            _songsGroupedByAlbum.value = UiState.Success(albumList.toSet().toList())
        }
    }

    fun getSongArtUri(songId : Long): Uri {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/media"),songId)
    }

    fun searchAlbumByName(text : String): Flow<UiState<List<Album>>>{
        return flow{
            emit(UiState.Loading)
            val cachedAlbum = musicDao.searchAlbumName(text)
            if (cachedAlbum.isNotEmpty()){
                emit(UiState.Success(cachedAlbum))
                return@flow
            }
        }
    }
}