package com.example.musicapptraining.di

import android.content.Context
import com.example.musicapptraining.data.mediaController.MediaControllerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MediaControllerManagerModule {
    @Provides
    fun setMediaControllerManager(@ApplicationContext context: Context):MediaControllerManager{
        return MediaControllerManager(context)
    }
}