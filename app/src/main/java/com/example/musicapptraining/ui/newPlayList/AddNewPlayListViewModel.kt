package com.example.musicapptraining.ui.newPlayList

import androidx.lifecycle.ViewModel
import com.example.musicapptraining.data.repositories.PlayListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddNewPlayListViewModel @Inject constructor(
    private val playListRepository: PlayListRepository
) : ViewModel() {


    fun addNewPlayList(name : String){
        playListRepository.addNewPlayList(name)
    }
}