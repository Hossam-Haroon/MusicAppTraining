package com.example.musicapptraining.di

import android.content.Context
import com.example.musicapptraining.data.repositories.AlbumRepository
import com.example.musicapptraining.data.repositories.ArtistRepository
import com.example.musicapptraining.data.repositories.PlayListRepository
import com.example.musicapptraining.data.repositories.SongRepository
import com.example.musicapptraining.data.source.MusicDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModel {


    @Provides
    fun songRepository(musicDao: MusicDao, @ApplicationContext context: Context): SongRepository{
        return SongRepository(musicDao,context)
    }

    @Provides
    fun artistRepository(musicDao: MusicDao, @ApplicationContext context: Context): ArtistRepository{
        return ArtistRepository(musicDao, context)
    }
    @Provides
    fun albumRepository(musicDao: MusicDao, @ApplicationContext context: Context): AlbumRepository{
        return AlbumRepository(musicDao, context)
    }
    @Provides
    fun playlistRepository(musicDao: MusicDao, @ApplicationContext context: Context): PlayListRepository{
        return PlayListRepository(musicDao, context)
    }
}