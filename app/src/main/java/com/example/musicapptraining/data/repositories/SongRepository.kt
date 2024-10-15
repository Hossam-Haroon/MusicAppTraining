package com.example.musicapptraining.data.repositories

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.data.model.Artist
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class SongRepository @Inject constructor(
    val musicDao : MusicDao,
    val context : Context
) {

   private var _audioList : MutableStateFlow<UiState<List<Song>>>  = MutableStateFlow(UiState.Loading)

    fun getAllSongs(): Flow<UiState<List<Song>>>{
        return flow {
            emit(UiState.Loading)
            val databaseSongs = musicDao.getAllSongs()
            if (databaseSongs.isNotEmpty()){
                emit(UiState.Success(databaseSongs))
                return@flow
            }


            if (!isPermissionGranted()){
                emit(UiState.Error("Permission is not allowed"))
                return@flow
            }

            try {
                fetchAllAudiosFromDevice()
                coroutineScope {
                    _audioList.collect{
                        if (it is UiState.Success){
                            musicDao.deleteSongs()
                            musicDao.insertAllSongs(it.data)
                            emit(it)
                        }
                    }

                }

            }catch (e: Exception){
                emit(UiState.Error("Error Fetching Music"))
            }
        }.flowOn(Dispatchers.IO)

    }

    fun getAlbumArtUri(albumId : Long): Uri?{
        //  content://media/external/audio/albumart/{albumId}
        return Uri.parse("content://media/external/audio/albumart").buildUpon()
            .appendPath(albumId.toString()).build()
    }

    private fun fetchAllAudiosFromDevice() {
        val files = mutableListOf<Song>()

        CoroutineScope(Dispatchers.IO).launch {
            _audioList.value = UiState.Loading
            val projection = arrayOf(
                Media._ID,
                Media.DISPLAY_NAME,
                Media.ALBUM,
                Media.ARTIST,
                Media.ALBUM_ID,
                Media.DURATION,
                Media.DATA,
                Media.DATE_ADDED,
                Media.MIME_TYPE
            )

            val selection =
                "${Media.IS_MUSIC} != 0 AND (" + "${Media.MIME_TYPE} = 'audio/mpeg' OR " +
                        "${Media.MIME_TYPE} = 'audio/mp4' OR " +
                        "${Media.MIME_TYPE} = 'audio/aac' OR " +
                        "${Media.MIME_TYPE} = 'audio/ogg')"
            val contentResolver  = context.contentResolver
            val sortOrder = "${Media.DATE_ADDED} DESC"
            val cursor = contentResolver.query(
                Media.EXTERNAL_CONTENT_URI,projection,selection,null,sortOrder)
            cursor.use { cursor ->
                while (cursor!!.moveToNext()){

                    //val songPath =
                       // cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))
                   // val file = File(songPath)
                    val songId =
                        cursor.getString(cursor.getColumnIndexOrThrow(Media._ID))
                    val artist =
                        cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST))
                    val album =
                        cursor.getString(cursor.getColumnIndexOrThrow(Media.ALBUM))
                    val songName =
                        cursor.getString(cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME))
                    val songDuration =
                        cursor.getLong(cursor.getColumnIndexOrThrow(Media.DURATION))
                    val albumId =
                        cursor.getString(cursor.getColumnIndexOrThrow(Media.ALBUM_ID))
                    val songMimeType =
                        cursor.getLong(cursor.getColumnIndexOrThrow(Media.MIME_TYPE))
                    val songDateAdded =
                        cursor.getLong(cursor.getColumnIndexOrThrow(Media.DATE_ADDED))
                    val songArt = getAlbumArtUri(albumId.toLong())
                    val songUri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, songId.toLong())

                    //val songUri = Uri.fromFile(file)
                    val song =Song(
                        songId,
                        songName,
                        songUri.toString(),
                        artist,
                        songDuration,
                        album,
                        songDateAdded,
                        songArt.toString(),
                        songMimeType.toString()
                    )
                    if (!song.songPath.contains("opus") && !song.songName.contains("AUD")){
                        files.add(song)
                    }else{
                            Log.e("FileNotFound", "File not found: $songUri")

                    }
                }
            }
            _audioList.value = UiState.Success(files)
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
        ) ==PackageManager.PERMISSION_GRANTED
    }


    fun searchSong(songName : String): Flow<UiState<List<Song>>> {
        return flow {
            emit(UiState.Loading)
            val searchedSongs =  musicDao.searchSongsName(songName)
            if (searchedSongs.isNotEmpty()){
                emit(UiState.Success(searchedSongs))
                return@flow
            }

        }
    }

    fun getAlbumSongs(albumName : String): Flow<UiState<Album>>{
        return flow{
            emit(UiState.Loading)
            val album = musicDao.getAlbumByName(albumName)
            emit(UiState.Success(album))
        }.flowOn(Dispatchers.IO)
    }

    fun  getArtistSongs(artistName: String): Flow<UiState<Artist>>{
        return flow{
            emit(UiState.Loading)
            val artist = musicDao.getArtistByName(artistName)
            emit(UiState.Success(artist))
        }.flowOn(Dispatchers.IO)
    }

    fun getPlaylistSongs(playListSong : String): Flow<UiState<PlayList>>{
        return flow {
            emit(UiState.Loading)
            val playList = musicDao.getPlayListByName(playListSong)
            emit(UiState.Success(playList))
        }.flowOn(Dispatchers.IO)
    }

    fun checkAndRefresh(): Flow<UiState<List<Song>>>{
        return flow {
            if (!isPermissionGranted()){
                emit(UiState.Error("Permission is not allowed"))
                return@flow
            }

            try {
                fetchAllAudiosFromDevice()
                coroutineScope {
                    _audioList.collect{
                        if (it is UiState.Success){
                            musicDao.deleteSongs()
                            musicDao.insertAllSongs(it.data)
                            emit(it)
                        }
                    }

                }

            }catch (e: Exception){
                emit(UiState.Error("Error Fetching Music"))
            }
        }.flowOn(Dispatchers.IO)

    }


}