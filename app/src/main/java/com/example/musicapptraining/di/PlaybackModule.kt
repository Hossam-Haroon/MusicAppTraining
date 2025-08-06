package com.example.musicapptraining.di

import android.content.Context
import com.example.musicapptraining.presentation.musicPlayer.MediaControllerListener
import com.example.musicapptraining.presentation.musicPlayer.MediaControllerListenerProvider
import com.example.musicapptraining.presentation.musicPlayer.MediaControllerManager
import com.example.musicapptraining.presentation.musicPlayer.MediaPlayerController
import com.example.musicapptraining.presentation.musicPlayer.PlaybackStateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PlaybackModule {

    @Provides
    fun getPlaybackStateManager():PlaybackStateManager{
        return PlaybackStateManager()
    }
    @Provides
    fun getMediaControllerListenerProvider():MediaControllerListenerProvider{
        return MediaControllerListenerProvider()
    }
    @Provides
    fun getMediaPlayerController(
        stateManager: PlaybackStateManager
    ):MediaPlayerController{
        return MediaPlayerController(stateManager)
    }
    @Provides
    fun getMediaControllerManager(
        stateManager: PlaybackStateManager,
        @ApplicationContext context :Context,
        mediaControllerListener: MediaControllerListener
    ):MediaControllerManager{
        return MediaControllerManager(
            stateManager,mediaControllerListener,context
        )
    }
    @Provides
    fun getMediaControllerListener(
        playerController: MediaPlayerController,
        stateManager: PlaybackStateManager
    ):MediaControllerListener{
        return MediaControllerListener(stateManager,playerController)
    }
}