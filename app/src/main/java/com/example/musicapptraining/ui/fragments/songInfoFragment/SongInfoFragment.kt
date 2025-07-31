package com.example.musicapptraining.ui.fragments.songInfoFragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.musicapptraining.databinding.FragmentSongInfoBinding
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.ui.fragments.baseFragment.BaseFragment
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