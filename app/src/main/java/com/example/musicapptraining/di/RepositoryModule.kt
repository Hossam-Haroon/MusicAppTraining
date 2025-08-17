package com.example.musicapptraining.di

import android.content.Context
import com.example.musicapptraining.data.mediaController.MediaControllerManager
import com.example.musicapptraining.data.repositories.AlbumRepositoryImpl
import com.example.musicapptraining.data.repositories.ArtistRepositoryImpl
import com.example.musicapptraining.data.repositories.MediaRepositoryImpl
import com.example.musicapptraining.data.repositories.PlaylistRepositoryImpl
import com.example.musicapptraining.data.repositories.SongRepositoryImpl
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.domain.utils.DataFetcher
import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.repositories.AlbumRepository
import com.example.musicapptraining.domain.repositories.ArtistRepository
import com.example.musicapptraining.domain.repositories.MediaRepository
import com.example.musicapptraining.domain.repositories.PlaylistRepository
import com.example.musicapptraining.domain.repositories.SongRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent



@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun songRepository(
        musicDao: MusicDao,
        @ApplicationContext context: Context,
        audioFetcher: DataFetcher<Song>
    ): SongRepository{
        return SongRepositoryImpl(musicDao,context,audioFetcher)
    }
    @Provides
    fun artistRepository(
        musicDao: MusicDao,
        artistFetcher: DataFetcher<Artist>
    ): ArtistRepository {
        return ArtistRepositoryImpl(musicDao, artistFetcher)
    }
    @Provides
    fun albumRepository(
        musicDao: MusicDao,
        albumFetcher: DataFetcher<Album>
    ): AlbumRepository{
        return AlbumRepositoryImpl(musicDao, albumFetcher)
    }
    @Provides
    fun playlistRepository(
        musicDao: MusicDao
    ): PlaylistRepository {
        return PlaylistRepositoryImpl(musicDao)
    }
    @Provides
    fun mediaRepository(mediaControllerManager: MediaControllerManager):MediaRepository{
        return MediaRepositoryImpl(mediaControllerManager)
    }
}