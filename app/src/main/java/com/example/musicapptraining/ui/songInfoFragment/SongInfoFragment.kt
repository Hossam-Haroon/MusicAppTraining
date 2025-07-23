package com.example.musicapptraining.ui.songInfoFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.databinding.FragmentSongInfoBinding
import com.example.musicapptraining.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongInfoFragment : BaseFragment<FragmentSongInfoBinding>(FragmentSongInfoBinding::inflate){
    private val args : SongInfoFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val song = args.song
        setSongInfoNames(song)
    }
    private fun setSongInfoNames(song: Song){
        binding.apply {
            songNameInfo.text = song.songName
            songArtistInfo.text = song.songArtist
            songAlbumInfo.text = song.songAlbum
        }
    }
}