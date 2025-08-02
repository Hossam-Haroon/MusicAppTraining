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
import com.example.musicapptraining.data.mappers.toDomain
import com.example.musicapptraining.data.mappers.toEntity
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.repositories.SongRepository
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

class SongRepositoryImpl @Inject constructor(
    private val musicDao: MusicDao,
    private val context: Context
):SongRepository {
    override fun getAllSongs(): Flow<List<Song>> {
        return musicDao.getAllSongs()
            .map { it.toDomain() }
            .onStart {
                val songs = musicDao.getAllSongs().first()
                if (songs.isEmpty()){
                    val fetchedSongs = fetchAllAudiosFromDevice().toEntity()
                    musicDao.deleteSongs()
                    musicDao.insertAllSongs(fetchedSongs)
                }
            }
    }
    override fun searchSong(songName: String): Flow<List<Song>> {
        return musicDao.searchSongsName(songName).map{it.toDomain()}
    }
    override fun getAlbumSongs(albumName: String): Flow<Album> {
        return musicDao.getAlbumByName(albumName).map { it.toDomain() }

    }
    override fun getArtistSongs(artistName: String): Flow<Artist> {
        return musicDao.getArtistByName(artistName).map {it.toDomain()}
    }
    override fun getPlaylistSongs(playListSong: String): Flow<Playlist?> {
        return musicDao.getPlayListByName(playListSong).map { it?.toDomain() }
    }
    override suspend fun checkAndRefresh(): List<Song> {
        val deviceSongs = fetchAllAudiosFromDevice()
        val localSongs = musicDao.getAllSongs().first().toDomain()
        val sortedDeviceSongs = deviceSongs.sortedBy { it.songId }
        val sortedLocalSongs = localSongs.sortedBy { it.songId }
        return checkIfLocalDataBaseHasTheSameDataAsTheDevice(sortedLocalSongs, sortedDeviceSongs)
    }
    private suspend fun checkIfLocalDataBaseHasTheSameDataAsTheDevice(
        localSongs: List<Song>,
        deviceSongs:List<Song>
    ): List<Song> {
        return if (localSongs != deviceSongs) {
            musicDao.deleteSongs()
            musicDao.insertAllSongs(deviceSongs.toEntity())
            deviceSongs
        }else{
            localSongs
        }
    }
    private fun fetchAllAudiosFromDevice(): List<Song> {
        val audioFiles = mutableListOf<Song>()
        val cursor = getCursorFromContentResolverAfterQueryingForTheRequiredAudios()
        cursor?.let {
            setCursorResultAfterMovingThroughAllData(it,audioFiles)
        }
        return audioFiles

    }
    private fun setCursorResultAfterMovingThroughAllData(
        cursor: Cursor,
        audioFiles:MutableList<Song>
    ){
        cursor.use {
            while (it.moveToNext()){
                val song = getSongDataFromCursorAndMakeAnInstanceOfSong(it)
                checkIfSongPathIsValidateAndAddItToTheListOfSongs(song,audioFiles)
            }
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
        ) == PackageManager.PERMISSION_GRANTED
    }
    companion object{
        private const val ERROR_MESSAGE = "Error Fetching Music"
        private const val PERMISSION_DISALLOWED = "Permission is not allowed"
        private const val URI_STRING = "content://media/external/audio/albumart"
        private const val UNFOUNDED_FILE = "File Not Found"
        private const val OPUS = "opus"
        private const val AUD = "AUD"
    }
}