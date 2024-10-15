package com.example.musicapptraining.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musicapptraining.data.source.MusicDao
import com.example.musicapptraining.data.source.MusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context : Context): MusicDatabase{
       return  Room.databaseBuilder(
           context,
           MusicDatabase::class.java,
           "MusicDatabase")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideDao(musicDatabase: MusicDatabase): MusicDao{
        return musicDatabase.musicDao()
    }
}