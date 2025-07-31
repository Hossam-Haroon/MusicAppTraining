package com.example.musicapptraining.di

import com.example.musicapptraining.domain.repositories.AlbumRepository
import com.example.musicapptraining.domain.repositories.ArtistRepository
import com.example.musicapptraining.domain.repositories.PlaylistRepository
import com.example.musicapptraining.domain.repositories.SongRepository
import com.example.musicapptraining.domain.usecases.albumUseCases.GetAllAlbumsUseCase
import com.example.musicapptraining.domain.usecases.albumUseCases.SearchAlbumByNameUseCase
import com.example.musicapptraining.domain.usecases.artistUseCases.GetAllArtistsUseCase
import com.example.musicapptraining.domain.usecases.artistUseCases.SearchArtistByNameUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.AddNewPlaylistUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.AddSongToPlaylistUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.DeleteSongFromPlaylistUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.GetAllPlaylistsUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.GetLikedPlaylistUseCase
import com.example.musicapptraining.domain.usecases.songUseCases.CheckAndRefreshUseCase
import com.example.musicapptraining.domain.usecases.songUseCases.GetAlbumSongsUseCase
import com.example.musicapptraining.domain.usecases.songUseCases.GetAllSongsUseCase
import com.example.musicapptraining.domain.usecases.songUseCases.GetArtistSongsUseCase
import com.example.musicapptraining.domain.usecases.songUseCases.GetPlaylistSongsUseCase
import com.example.musicapptraining.domain.usecases.songUseCases.SearchSongUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {
    @Provides
    fun getAllSongs(songRepository: SongRepository):GetAllSongsUseCase{
        return GetAllSongsUseCase(songRepository)
    }
    @Provides
    fun searchSong(songRepository: SongRepository):SearchSongUseCase{
        return SearchSongUseCase(songRepository)
    }
    @Provides
    fun getAlbumSongs(songRepository: SongRepository):GetAlbumSongsUseCase{
        return GetAlbumSongsUseCase(songRepository)
    }
    @Provides
    fun getArtistSongs(songRepository: SongRepository):GetArtistSongsUseCase{
        return GetArtistSongsUseCase(songRepository)
    }
    @Provides
    fun getPlaylistSongs(songRepository: SongRepository):GetPlaylistSongsUseCase{
        return GetPlaylistSongsUseCase(songRepository)
    }
    @Provides
    fun checkAndRefresh(songRepository: SongRepository):CheckAndRefreshUseCase{
        return CheckAndRefreshUseCase(songRepository)
    }
    @Provides
    fun getAllArtists(artistRepository: ArtistRepository):GetAllArtistsUseCase{
        return GetAllArtistsUseCase(artistRepository)
    }
    @Provides
    fun searchForArtist(artistRepository: ArtistRepository):SearchArtistByNameUseCase{
        return SearchArtistByNameUseCase(artistRepository)
    }
    @Provides
    fun getAllAlbums(albumRepository: AlbumRepository):GetAllAlbumsUseCase{
        return GetAllAlbumsUseCase(albumRepository)
    }
    @Provides
    fun searchForAlbum(albumRepository: AlbumRepository):SearchAlbumByNameUseCase{
        return SearchAlbumByNameUseCase(albumRepository)
    }
    @Provides
    fun getAllPlaylists(playlistRepository: PlaylistRepository):GetAllPlaylistsUseCase{
        return GetAllPlaylistsUseCase(playlistRepository)
    }
    @Provides
    fun addNewPlaylist(playlistRepository: PlaylistRepository):AddNewPlaylistUseCase{
        return AddNewPlaylistUseCase(playlistRepository)
    }
    @Provides
    fun addSongToPlaylist(playlistRepository: PlaylistRepository):AddSongToPlaylistUseCase{
        return AddSongToPlaylistUseCase(playlistRepository)
    }
    @Provides
    fun getLikedPlaylist(playlistRepository: PlaylistRepository):GetLikedPlaylistUseCase{
        return GetLikedPlaylistUseCase(playlistRepository)
    }
    @Provides
    fun deleteSongFromPlaylist(
        playlistRepository: PlaylistRepository
    ):DeleteSongFromPlaylistUseCase{
        return DeleteSongFromPlaylistUseCase(playlistRepository)
    }
}