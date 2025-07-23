package com.example.musicapptraining.data.repositories

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore.Audio.Media
import android.util.Log
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    val musicDao: MusicDao,
    val context : Context
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllAlbums(): Flow<UiState<List<Album>>>{
        return flow {
            emit(UiState.Loading)
            val albums = musicDao.getAllAlbums().first()
            if (albums.isNotEmpty()){
                emit(UiState.Success(albums))
                return@flow
            }
            try {
                val fetchedAlbums = fetchAllAlbumsFromDevice()
                musicDao.insertAllAlbums(fetchedAlbums)
            }catch (e : Exception){
                emit(UiState.Error(ERROR_MESSAGE))
            }
        }.flatMapLatest {
            musicDao.getAllAlbums()
                .map { UiState.Success(it) }
                .catch { UiState.Error(ERROR_MESSAGE) }
        }
    }
    private suspend fun fetchAllAlbumsFromDevice(): List<Album> {
        return withContext(Dispatchers.IO){
            val albumHashMap = HashMap<String,Album>()
            val albumList = mutableListOf<Album>()
            val cursor = getCursorFromContentResolverAfterQueryingForTheRequiredAudios()
            cursor?.let {
                setCursorResultAfterMovingThroughAllData(it,albumHashMap)
            }
            albumList.addAll(albumHashMap.values)
            return@withContext albumList
        }
    }
    private fun setCursorResultAfterMovingThroughAllData(
        cursor: Cursor,
        albumHashMap: HashMap<String, Album>
    ){
        cursor.use {
            while (it.moveToNext()){
                val song = getSongDataFromCursorAndMakeAnInstanceOfSong(it)
                val albumId =
                    it.getString(it.getColumnIndexOrThrow(Media.ALBUM_ID))
                val albumArtUri = getSongArtUri(albumId.toLong())
                checkIfSongPathIsValidateAndCheckAlbumExistenceAndAddItToList(
                    song,
                    albumId,
                    albumArtUri.toString(),
                    albumHashMap
                )
            }
        }
    }
    private fun checkIfSongPathIsValidateAndCheckAlbumExistenceAndAddItToList(
        song: Song,
        albumId : String,
        albumArtUri : String,
        albumHashMap: HashMap<String, Album>
    ){
        if (!song.songPath.contains(OPUS) && !song.songName.contains(AUD)){
            if (!albumHashMap.containsKey(albumId)){
                albumHashMap[albumId] = Album(
                    song.songAlbum,
                    albumId,
                    albumArtUri,
                    mutableListOf(),
                    song.songArtist
                )
            }
            albumHashMap[albumId]?.albumSongs?.add(song)
        }else {
            Log.e(UNFOUNDED_FILE, "File not found: ${song.songPath}")
        }
    }
    private fun getSongArtUri(songId : Long): Uri {
        return ContentUris.withAppendedId(
            Uri.parse(URI_STRING),
            songId
        )
    }
    private fun getSongDataFromCursorAndMakeAnInstanceOfSong(cursor: Cursor):Song{
        val songPath =
            cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))
        val songId = cursor.getString(cursor.getColumnIndexOrThrow(Media._ID))
        val songName =
            cursor.getString(cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME))
        val songArtist =
            cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST))
        val songDuration =
            cursor.getLong(cursor.getColumnIndexOrThrow(Media.DURATION))
        val songAlbum =
            cursor.getString(cursor.getColumnIndexOrThrow(Media.ALBUM))
        val songDateAdded =
            cursor.getLong(cursor.getColumnIndexOrThrow(Media.DATE_ADDED))
        val songMimeType =
            cursor.getString(cursor.getColumnIndexOrThrow(Media.MIME_TYPE))
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
        return song
    }
    private fun getCursorFromContentResolverAfterQueryingForTheRequiredAudios(): Cursor? {
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
        return cursor
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
    companion object{
        private const val ERROR_MESSAGE = "Error Fetching Music"
        private const val URI_STRING = "content://media/external/audio/media"
        private const val UNFOUNDED_FILE = "File Not Found"
        private const val OPUS = "opus"
        private const val AUD = "AUD"
    }
}