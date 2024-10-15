package com.example.musicapptraining.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.data.model.Artist
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.data.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    @Query("SELECT * FROM Song")
    suspend fun getAllSongs() : List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSongs(songs: List<Song>)

    @Query("DELETE FROM Song")
    suspend fun deleteSongs()

    @Query("SELECT * FROM Song WHERE songId = :songId")
    suspend fun getSongById(songId : String): Song
//--------------------------------------------------
    @Query("SELECT * FROM Artist")
    suspend fun getAllArtists(): List<Artist>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllArtists(artists: List<Artist>)

    @Query("SELECT * FROM Artist WHERE artistName = :name")
    suspend fun getArtistByName(name : String): Artist
//-------------------------------------------------------
    @Query("SELECT * FROM Album")
    suspend fun getAllAlbums(): List<Album>

    @Query("SELECT * FROM Album WHERE albumName= :name")
    suspend fun getAlbumByName(name: String):Album

    @Query("SELECT * FROM Album WHERE albumID= :albumId")
    suspend fun getAlbumById(albumId: String):Album

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAlbums(albums : List<Album>)
//---------------------------------------------------------
    @Query("SELECT * FROM PlayList")
     fun getAllPlaylists(): Flow<List<PlayList>>

    @Query("SELECT * FROM PlayList WHERE playlistName = :name")
    suspend fun getPlayListByName(name: String): PlayList

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayList(playList: PlayList)
//------------------------------------------------------------

    // search methods
    @Query("SELECT * FROM Song WHERE songName LIKE '%' || :text || '%'")
    suspend fun searchSongsName(text : String): List<Song>

    @Query("SELECT * FROM Artist WHERE artistName like '%' || :text || '%'")
    suspend fun searchArtistName(text : String): List<Artist>

    @Query("SELECT * FROM Album WHERE albumName LIKE '%' || :text || '%'")
    suspend fun searchAlbumName(text : String): List<Album>

}

