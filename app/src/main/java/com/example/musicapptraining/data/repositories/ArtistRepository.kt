package com.example.musicapptraining.data.repositories

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore.Audio.Media
import android.util.Log
import com.example.musicapptraining.data.model.Artist
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArtistRepository @Inject constructor(
    val musicDao: MusicDao,
    val context : Context
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getArtists(): Flow<UiState<List<Artist>>>{
        return flow {
            emit(UiState.Loading)
            val artists = musicDao.getAllArtists().first()
            if (artists.isNotEmpty()){
                emit(UiState.Success(artists))
            }
            try {
                val fetchedArtists = fetchAllArtistsFromDevice()
                musicDao.insertAllArtists(fetchedArtists)
            }catch (e: Exception){
                emit(UiState.Error(ERROR_MESSAGE))
            }
        }.flatMapLatest {
            musicDao.getAllArtists()
                .map { UiState.Success(it) }
                .catch { UiState.Error(ERROR_MESSAGE) }
        }
    }
    private suspend fun fetchAllArtistsFromDevice(): List<Artist> {
        return withContext(Dispatchers.IO){
            val artistHashMap = HashMap<String,Artist>()
            val artists = mutableListOf<Artist>()
            val cursor = getCursorFromContentResolverAfterQueryingForTheRequiredAudios()
            cursor?.let {
                setCursorResultAfterMovingThroughAllData(it,artistHashMap)
            }
            artists.addAll(artistHashMap.values)
            return@withContext artists
        }
    }
    private fun setCursorResultAfterMovingThroughAllData(
        cursor: Cursor,
        artistHashMap: HashMap<String, Artist>
    ){
        cursor.use {
            while (it.moveToNext()){
                val song = getSongDataFromCursorAndMakeAnInstanceOfSong(it)
                checkIfSongPathIsValidateAndCheckAlbumExistenceAndAddItToList(
                    song,
                    song.songArtist,
                    artistHashMap
                )
            }
        }
    }
    private fun checkIfSongPathIsValidateAndCheckAlbumExistenceAndAddItToList(
        song: Song,
        songArtist : String,
        artistHashMap: HashMap<String, Artist>
    ){
        if (!song.songPath.contains(OPUS) && !song.songName.contains(AUD)){
            if (!artistHashMap.containsKey(songArtist)){
                artistHashMap[songArtist] = Artist(songArtist, mutableListOf())
            }
            artistHashMap[songArtist]?.artistSongs?.add(song)
        }else {
            Log.e(UNFOUNDED_FILE, "File not found: ${song.songPath}")
        }
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
        val selection = "${Media.IS_MUSIC} != 0 AND (" + "${Media.MIME_TYPE} = 'audio/mpeg' OR "+
                "${Media.MIME_TYPE} = 'audio/mp4' OR " +
                "${Media.MIME_TYPE} = 'audio/aac' OR " +
                "${Media.MIME_TYPE} = 'audio/ogg')"

        val sortOrder = "${Media.ARTIST} ASC"

        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            Media.EXTERNAL_CONTENT_URI,projection,selection,null,sortOrder
        )
        return cursor
    }
    private fun getSongArtUri(songId : Long): Uri {
        return ContentUris.withAppendedId(
            Uri.parse(URI_STRING),
            songId
        )
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
    fun insertArtist(artistName: String){
        CoroutineScope(Dispatchers.IO).launch {
            musicDao.insertArtist(Artist(artistName, mutableListOf()))
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