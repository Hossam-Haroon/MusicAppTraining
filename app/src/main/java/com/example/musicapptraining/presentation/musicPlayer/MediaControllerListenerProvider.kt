package com.example.musicapptraining.presentation.musicPlayer

import javax.inject.Inject

class MediaControllerListenerProvider @Inject constructor() {
    private var _listener : MediaControllerListener? = null
    fun setListener(listener: MediaControllerListener){
        _listener = listener
    }
    fun getListener() = _listener
}