package com.example.musicapptraining.data.repositories

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore.Audio.Media
import android.util.Log
import com.example.musicapptraining.data.mappers.toDomain
import com.example.musicapptraining.data.mappers.toEntity
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.repositories.ArtistRepository
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val musicDao: MusicDao,
    private val context: Context
):ArtistRepository {
    override fun getArtists(): Flow<List<Artist>> {
        return musicDao.getAllArtists()
            .map { it.toDomain() }
            .onStart {
                val artists = musicDao.getAllArtists().first()
                if (artists.isEmpty()){
                    val fetchedArtists = fetchAllArtistsFromDevice().toEntity()
                    musicDao.insertAllArtists(fetchedArtists)
                }
            }
    }
    override fun searchArtistByName(text: String): Flow<List<Artist>> {
        return musicDao.searchArtistName(text).map{ it.toDomain() }
    }
    private fun fetchAllArtistsFromDevice(): List<Artist> {
        val artistHashMap = HashMap<String,Artist>()
        val artists = mutableListOf<Artist>()
        val cursor = getCursorFromContentResolverAfterQueryingForTheRequiredAudios()
        cursor?.let {
            setCursorResultAfterMovingThroughAllData(it,artistHashMap)
        }
        artists.addAll(artistHashMap.values)
        return artists

    }
    private fun setCursorResultAfterMovingThroughAllData(
        cursor: Cursor,
        artistHashMap: HashMap<String, Artist>
    ){
        cursor.use {
            var songCount = 0
            while (it.moveToNext()){
                songCount++
                val song = getSongDataFromCursorAndMakeAnInstanceOfSong(it)
                checkIfSongPathIsValidateAndCheckArtistExistenceAndAddItToList(
                    song,
                    song.songArtist,
                    artistHashMap
                )
            }
        }
    }
    private fun checkIfSongPathIsValidateAndCheckArtistExistenceAndAddItToList(
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
    private fun getSongDataFromCursorAndMakeAnInstanceOfSong(cursor: Cursor): Song {
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
    companion object{
        private const val ERROR_MESSAGE = "Error Fetching Music"
        private const val URI_STRING = "content://media/external/audio/media"
        private const val UNFOUNDED_FILE = "File Not Found"
        private const val OPUS = "opus"
        private const val AUD = "AUD"
    }
}