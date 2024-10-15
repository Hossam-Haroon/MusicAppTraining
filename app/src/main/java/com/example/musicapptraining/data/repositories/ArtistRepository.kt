package com.example.musicapptraining.data.repositories

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import com.example.musicapptraining.data.model.Artist
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

class ArtistRepository @Inject constructor(
    val musicDao: MusicDao,
    val context : Context
) {

    private val _artistAudioCollection : MutableStateFlow<UiState<List<Artist>>>
    = MutableStateFlow(UiState.Loading)

    fun getArtists(): Flow<UiState<List<Artist>>>{
        return flow {
            emit(UiState.Loading)
            val artists = musicDao.getAllArtists()
            if (artists.isNotEmpty()){
                emit(UiState.Success(artists))
            }

            try {
                fetchAllArtistsFromDevice()
                _artistAudioCollection.collect{
                    if (it is UiState.Success && it.data.isNotEmpty()){
                        musicDao.insertAllArtists(it.data)
                        emit(it)
                    }

                }

            }catch (e: Exception){
                emit(UiState.Error(" Error fetching data"))
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun fetchAllArtistsFromDevice() {
        val artistHashMap = HashMap<String,Artist>()
        val artists = mutableListOf<Artist>()

        CoroutineScope(Dispatchers.IO).launch {
            _artistAudioCollection.value = UiState.Loading
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

            val sortOrder = "${Media.ARTIST} ASC"

            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI,projection,selection,null,sortOrder)
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
                    if (!song.songPath.contains("opus") && !song.songName.contains("AUD")){
                        if (!artistHashMap.containsKey(songArtist)){
                            artistHashMap[songArtist] = Artist(songArtist, mutableListOf())
                        }
                        artistHashMap[songArtist]?.artistSongs?.add(song)

                    }
                    artists.addAll(artistHashMap.values)
                    _artistAudioCollection.value = UiState.Success(artists.toSet().toList())
                }
            }
        }
    }

    fun getSongArtUri(songId : Long): Uri?{
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/media"), songId)
    }


    fun searchArtistByName(text : String): Flow<UiState<List<Artist>>> {
        return flow {
            emit(UiState.Loading)
            val cachedArtist = musicDao.searchArtistName(text)
           if (cachedArtist.isNotEmpty()){
               emit(UiState.Success(cachedArtist))
               return@flow
           }
        }


    }
}