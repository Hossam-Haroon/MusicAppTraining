package com.example.musicapptraining.data.dataFetcher

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore.Audio.Media
import android.util.Log
import com.example.musicapptraining.domain.utils.DataFetcher
import com.example.musicapptraining.domain.model.Song

class AudioFetcherFromDevice(
    private val context :Context
): DataFetcher<Song> {
    override fun fetchDataFromDevice(): List<Song> {
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
    companion object{
        private const val URI_STRING = "content://media/external/audio/albumart"
        private const val UNFOUNDED_FILE = "File Not Found"
        private const val OPUS = "opus"
        private const val AUD = "AUD"
    }
}