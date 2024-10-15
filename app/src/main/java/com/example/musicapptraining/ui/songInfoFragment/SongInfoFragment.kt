package com.example.musicapptraining.ui.songInfoFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.musicapptraining.databinding.FragmentSongInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongInfoFragment : Fragment() {

    private lateinit var binding : FragmentSongInfoBinding

    val args : SongInfoFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val song = args.song

        binding.songNameInfo.text = song.songName
        binding.songArtistInfo.text = song.songArtist
        binding.songAlbumInfo.text = song.songAlbum
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongInfoBinding.inflate(inflater,container,false)
        return binding.root
    }


}