package com.example.musicapptraining.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.example.musicapptraining.data.model.Song
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicServiceTest : MediaSessionService() {
    @Inject
    lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession
    override fun onTaskRemoved(rootIntent: Intent?) {
        player.release()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }
    override fun onDestroy() {
        mediaSession.release()
        player.release()
        super.onDestroy()
    }
}
