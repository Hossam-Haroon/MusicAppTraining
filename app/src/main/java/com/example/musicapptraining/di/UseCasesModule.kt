package com.example.musicapptraining.di

import com.example.musicapptraining.domain.repositories.AlbumRepository
import com.example.musicapptraining.domain.repositories.ArtistRepository
import com.example.musicapptraining.domain.repositories.MediaRepository
import com.example.musicapptraining.domain.repositories.PlaylistRepository
import com.example.musicapptraining.domain.repositories.SongRepository
import com.example.musicapptraining.domain.usecases.albumUseCases.GetAllAlbumsUseCase
import com.example.musicapptraining.domain.usecases.albumUseCases.SearchAlbumByNameUseCase
import com.example.musicapptraining.domain.usecases.artistUseCases.GetAllArtistsUseCase
import com.example.musicapptraining.domain.usecases.artistUseCases.SearchArtistByNameUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.AddPlaylistToPlayerUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.ClearPlayerUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.CycleShuffleRepeatUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.GetPositionOfSongInsidePlaylistUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SeekBackwardUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SeekForwardUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SeekToNextTrackUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SeekToPositionUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SeekToPreviousTrackUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SeekToTrackUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.SetSongToPlayNextUseCase
import com.example.musicapptraining.domain.usecases.mediaControllerUseCases.TogglePlaybackUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.AddNewPlaylistUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.AddSongToPlaylistUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.DeleteSongFromPlaylistUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.GetAllPlaylistsUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.GetLikedPlaylistUseCase
import com.example.musicapptraining.domain.usecases.songUseCases.CheckAndRefreshUseCase
import com.example.musicapptraining.domain.usecases.albumUseCases.GetAlbumSongsUseCase
import com.example.musicapptraining.domain.usecases.songUseCases.GetAllSongsUseCase
import com.example.musicapptraining.domain.usecases.artistUseCases.GetArtistSongsUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.GetPlaylistSongsUseCase
import com.example.musicapptraining.domain.usecases.songUseCases.SearchSongUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


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
    fun getAlbumSongs(albumRepository: AlbumRepository): GetAlbumSongsUseCase {
        return GetAlbumSongsUseCase(albumRepository)
    }
    @Provides
    fun getArtistSongs(artistRepository: ArtistRepository): GetArtistSongsUseCase {
        return GetArtistSongsUseCase(artistRepository)
    }
    @Provides
    fun getPlaylistSongs(playlistRepository: PlaylistRepository): GetPlaylistSongsUseCase {
        return GetPlaylistSongsUseCase(playlistRepository)
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
    @Provides
    fun togglePlayback(
        mediaRepository: MediaRepository
    ):TogglePlaybackUseCase{
        return TogglePlaybackUseCase(mediaRepository)
    }
    @Provides
    fun seekToItem(mediaRepository: MediaRepository):SeekToTrackUseCase{
        return SeekToTrackUseCase(mediaRepository)
    }
    @Provides
    fun seekForward(mediaRepository: MediaRepository):SeekForwardUseCase{
        return SeekForwardUseCase(mediaRepository)
    }
    @Provides
    fun seekBackward(mediaRepository: MediaRepository):SeekBackwardUseCase{
        return SeekBackwardUseCase(mediaRepository)
    }
    @Provides
    fun addPlaylist(mediaRepository: MediaRepository):AddPlaylistToPlayerUseCase{
        return AddPlaylistToPlayerUseCase(mediaRepository)
    }
    @Provides
    fun clearPlayer(mediaRepository: MediaRepository):ClearPlayerUseCase{
        return ClearPlayerUseCase(mediaRepository)
    }
    @Provides
    fun seekToNextTrack(mediaRepository: MediaRepository):SeekToNextTrackUseCase{
        return SeekToNextTrackUseCase(mediaRepository)
    }
    @Provides
    fun seekToPreviousTrack(mediaRepository: MediaRepository):SeekToPreviousTrackUseCase{
        return SeekToPreviousTrackUseCase(mediaRepository)
    }
    @Provides
    fun seekToPosition(mediaRepository: MediaRepository):SeekToPositionUseCase{
        return SeekToPositionUseCase(mediaRepository)
    }
    @Provides
    fun setSongToPlayNext(mediaRepository: MediaRepository):SetSongToPlayNextUseCase{
        return SetSongToPlayNextUseCase(mediaRepository)
    }
    @Provides
    fun getPositionOfSongInsidePlaylist(
        mediaRepository: MediaRepository
    ):GetPositionOfSongInsidePlaylistUseCase{
        return GetPositionOfSongInsidePlaylistUseCase(mediaRepository)
    }
    @Provides
    fun cycleShuffleRepeat(mediaRepository: MediaRepository):CycleShuffleRepeatUseCase{
        return CycleShuffleRepeatUseCase(mediaRepository)
    }
}