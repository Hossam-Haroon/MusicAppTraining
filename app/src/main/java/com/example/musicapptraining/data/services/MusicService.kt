package com.example.musicapptraining.data.services
import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaSessionService() {
    @Inject
    lateinit var player: ExoPlayer
    @Inject
    lateinit var mediaSession: MediaSession
    private var serviceActive = false
    override fun onCreate() {
        super.onCreate()
        initializeService()
    }
    private fun initializeService() {
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.playWhenReady = false
        serviceActive = true
    }
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession
    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!player.isPlaying && player.mediaItemCount == 0){
            stopSelf()
        }
        super.onTaskRemoved(rootIntent)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }
    override fun onDestroy() {
        mediaSession.release()
        player.release()
        serviceActive = false
        super.onDestroy()
    }
}