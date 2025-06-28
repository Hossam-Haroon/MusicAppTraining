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
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.data.model.Artist
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongRepository @Inject constructor(
    val musicDao : MusicDao,
    val context : Context
) {
     fun getAllSongs(): Flow<UiState<List<Song>>>{
        return flow {
            emit(UiState.Loading)
            val databaseSongs = musicDao.getAllSongs()
            if (databaseSongs.isNotEmpty()){
                emit(UiState.Success(databaseSongs))
                return@flow
            }
            if (!isPermissionGranted()){
                emit(UiState.Error(PERMISSION_DISALLOWED))
                return@flow
            }
            try {
                val fetchedSongs = fetchAllAudiosFromDevice()
                    musicDao.deleteSongs()
                    musicDao.insertAllSongs(fetchedSongs)
                    emit(UiState.Success(fetchedSongs))
            }catch (e: Exception){
                emit(UiState.Error(ERROR_MESSAGE))
            }
        }.flowOn(Dispatchers.IO)

    }

    fun searchSong(songName : String): Flow<UiState<List<Song>>> {
        return flow {
            emit(UiState.Loading)
            val searchedSongs =  musicDao.searchSongsName(songName)
            if (searchedSongs.isNotEmpty()){
                emit(UiState.Success(searchedSongs))
                return@flow
            }
        }.flowOn(Dispatchers.IO)
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

    suspend fun checkAndRefresh(): UiState<List<Song>>{
            if (!isPermissionGranted()){
                return UiState.Error(PERMISSION_DISALLOWED)
            }
            return try{
                val deviceSongs =  fetchAllAudiosFromDevice()
                val localSongs = musicDao.getAllSongs()
                val sortedDeviceSongs = deviceSongs.sortedBy { it.songId }
                val sortedLocalSongs = localSongs.sortedBy { it.songId }
                checkIfLocalDataBaseHasTheSameDataAsTheDevice(sortedLocalSongs, sortedDeviceSongs)
            }catch (e: Exception){
                 UiState.Error(e.localizedMessage ?: ERROR_MESSAGE)
            }
    }

    private suspend fun checkIfLocalDataBaseHasTheSameDataAsTheDevice(
        localSongs: List<Song>,
        deviceSongs:List<Song>
    ): UiState.Success<List<Song>> {
       return if (localSongs != deviceSongs) {
            musicDao.deleteSongs()
            musicDao.insertAllSongs(deviceSongs)
            UiState.Success(deviceSongs)
        }else{
            UiState.Success(localSongs)
        }
    }

    private suspend fun fetchAllAudiosFromDevice(): List<Song> {
        return withContext(Dispatchers.IO){
            val audioFiles = mutableListOf<Song>()
            val cursor = getCursorFromContentResolverAfterQueryingForTheRequiredAudios()
            cursor.use {
                while (it?.moveToNext() == true){
                    val song = getSongDataFromCursorAndMakeAnInstanceOfSong(it)
                    checkIfSongPathIsValidateAndAddItToTheListOfSongs(song,audioFiles)
                }
            }
            return@withContext audioFiles
        }
    }
    private fun getSongDataFromCursorAndMakeAnInstanceOfSong(cursor: Cursor):Song{
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
        return song
    }
    private fun checkIfSongPathIsValidateAndAddItToTheListOfSongs(
        song: Song,
        songs:MutableList<Song>
    ){
        if (!song.songPath.contains(OPUS) && !song.songName.contains(AUD)){
            songs.add(song)
        }else {
            Log.e(UNFOUNDED_FILE, "File not found: ${song.songPath}")
        }
    }
    private fun getCursorFromContentResolverAfterQueryingForTheRequiredAudios(): Cursor? {
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
        return cursor
    }
    private fun getAlbumArtUri(albumId : Long): Uri?{
        return Uri.parse(URI_STRING).buildUpon()
            .appendPath(albumId.toString()).build()
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
    companion object{
        const val ERROR_MESSAGE = "Error Fetching Music"
        const val PERMISSION_DISALLOWED = "Permission is not allowed"
        const val URI_STRING = "content://media/external/audio/albumart"
        const val UNFOUNDED_FILE = "File Not Found"
        const val OPUS = "opus"
        const val AUD = "AUD"
    }
}