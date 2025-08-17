package com.example.musicapptraining.di

import android.content.Context
import com.example.musicapptraining.data.dataFetcher.AlbumFetcherFromDevice
import com.example.musicapptraining.data.dataFetcher.ArtistFetcherFromDevice
import com.example.musicapptraining.data.dataFetcher.AudioFetcherFromDevice
import com.example.musicapptraining.domain.utils.DataFetcher
import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.model.Song
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataFetcherModule {

    @Provides
    fun getAudioFetcherFromDevice(@ApplicationContext context: Context): DataFetcher<Song> {
        return AudioFetcherFromDevice(context)
    }
    @Provides
    fun getArtistFetcherFromDevice(@ApplicationContext context: Context): DataFetcher<Artist> {
        return ArtistFetcherFromDevice(context)
    }
    @Provides
    fun getAlbumFetcherFromDevice(@ApplicationContext context: Context): DataFetcher<Album> {
        return AlbumFetcherFromDevice(context)
    }
}