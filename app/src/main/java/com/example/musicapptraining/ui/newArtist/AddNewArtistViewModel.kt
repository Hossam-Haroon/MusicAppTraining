package com.example.musicapptraining.ui.newArtist

import androidx.lifecycle.ViewModel
import com.example.musicapptraining.data.repositories.ArtistRepository
import javax.inject.Inject

class AddNewArtistViewModel @Inject constructor(
    private val artistRepository: ArtistRepository
) : ViewModel() {


    fun insertArtist(name:String){
        artistRepository.insertArtist(name)
    }
}