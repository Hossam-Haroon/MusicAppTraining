package com.example.musicapptraining.utilities

import com.example.musicapptraining.data.model.Song

sealed class PlayerEvents {
    data class AddPlayList(var songs: List<Song>, var isUpdatePlaylistRequired : Boolean): PlayerEvents()
    data class GoToSpecificItem(var index : Int): PlayerEvents()
    data class GoToSpecificPosition(var position : Long): PlayerEvents()
    data class AddSongToPlayNext(var songId: String): PlayerEvents()
    data class GetThePositionOfSpecificSongInsideThePlayList(var id : String): PlayerEvents()

    object PausePlay : PlayerEvents()
    object Repeat : PlayerEvents()
    object Shuffle: PlayerEvents()
    object Next: PlayerEvents()
    object Previous: PlayerEvents()
    object SeekForward: PlayerEvents()
    object SeekBackward: PlayerEvents()
    object ClearMediaItems: PlayerEvents()

}