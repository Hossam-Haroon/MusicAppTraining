package com.example.musicapptraining.presentation.fragments.artistsAndAlbumsFragment

import android.util.Log
import com.example.musicapptraining.utilities.UiState
import com.example.musicapptraining.utilities.handleUiState
import kotlinx.coroutines.flow.Flow

class AudioStateHandler {
    suspend fun <T>collectAndHandleAudioState(
        flow : Flow<UiState<T>>,
        successHandler : (T)->Unit,
        errorTag : String
    ){
        flow.collect{uiState->
            handleUiState(
                uiState = uiState,
                successState = successHandler,
                errorState = {message ->
                    Log.d(errorTag,"ERROR is : $message")
                }
            )
        }
    }
}