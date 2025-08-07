package com.example.musicapptraining.di

import android.content.Context
import com.example.musicapptraining.data.mediaController.MediaControllerManager
import com.example.musicapptraining.data.repositories.AlbumRepositoryImpl
import com.example.musicapptraining.data.repositories.ArtistRepositoryImpl
import com.example.musicapptraining.data.repositories.MediaRepositoryImpl
import com.example.musicapptraining.data.repositories.PlaylistRepositoryImpl
import com.example.musicapptraining.data.repositories.SongRepositoryImpl
import com.example.musicapptraining.data.source.MusicDao
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
    fun songRepository(musicDao: MusicDao, @ApplicationContext context: Context): SongRepository{
        return SongRepositoryImpl(musicDao,context)
    }
    @Provides
    fun artistRepository(
        musicDao: MusicDao,
        @ApplicationContext context: Context
    ): ArtistRepository {
        return ArtistRepositoryImpl(musicDao, context)
    }
    @Provides
    fun albumRepository(musicDao: MusicDao, @ApplicationContext context: Context): AlbumRepository{
        return AlbumRepositoryImpl(musicDao, context)
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