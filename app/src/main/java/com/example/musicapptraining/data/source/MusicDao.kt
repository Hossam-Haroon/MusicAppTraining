package com.example.musicapptraining.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicapptraining.data.entities.AlbumEntity
import com.example.musicapptraining.data.entities.ArtistEntity
import com.example.musicapptraining.data.entities.PlaylistEntity
import com.example.musicapptraining.data.entities.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("SELECT * FROM SongEntity")
    fun getAllSongs() : Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSongs(songs: List<SongEntity>)

    @Query("DELETE FROM SongEntity")
    suspend fun deleteSongs()

    @Query("SELECT * FROM SongEntity WHERE songId = :songId")
    suspend fun getSongById(songId : String): SongEntity
//--------------------------------------------------
    @Query("SELECT * FROM ArtistEntity")
    fun getAllArtists(): Flow<List<ArtistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllArtists(artists: List<ArtistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: ArtistEntity)

    @Query("SELECT * FROM ArtistEntity WHERE artistName = :name")
    fun getArtistByName(name : String): Flow<ArtistEntity>
//-------------------------------------------------------
    @Query("SELECT * FROM AlbumEntity")
    fun getAllAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM AlbumEntity WHERE albumName= :name")
    fun getAlbumByName(name: String):Flow<AlbumEntity>

    @Query("SELECT * FROM AlbumEntity WHERE albumID= :albumId")
    suspend fun getAlbumById(albumId: String):AlbumEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAlbums(albums : List<AlbumEntity>)
//---------------------------------------------------------
    @Query("SELECT * FROM PlaylistEntity")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM PlaylistEntity WHERE playlistName = :name")
    fun getPlayListByName(name: String): Flow<PlaylistEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayList(playList: PlaylistEntity)
//------------------------------------------------------------
    @Query("SELECT * FROM SongEntity WHERE songName LIKE '%' || :text || '%'")
    fun searchSongsName(text : String): Flow<List<SongEntity>>

    @Query("SELECT * FROM ArtistEntity WHERE artistName like '%' || :text || '%'")
    fun searchArtistName(text : String): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM AlbumEntity WHERE albumName LIKE '%' || :text || '%'")
    suspend fun searchAlbumName(text : String): List<AlbumEntity>

}

