package com.example.musicapptraining.ui.searchMoreButtonFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.R
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.data.model.Artist
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.databinding.FragmentSearchBinding
import com.example.musicapptraining.databinding.FragmentSearchMoreButtonBinding
import com.example.musicapptraining.ui.albumFragment.AlbumAdapter
import com.example.musicapptraining.ui.artistFragment.ArtistAdapter
import com.example.musicapptraining.ui.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.ui.musicPlayer.MusicPlayerViewModel
import com.example.musicapptraining.ui.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.ui.songsFragment.SongAdapter
import com.example.musicapptraining.utilities.PlayerEvents
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchMoreButtonFragment : Fragment() {
    private lateinit var songAdapter: SongAdapter
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var binding : FragmentSearchMoreButtonBinding
    private val navArgs: SearchMoreButtonFragmentArgs by navArgs()

    private val playerViewModel : MusicPlayerViewModel by viewModels()

    private var songList = emptyArray<Song>()
    private var artistList = emptyArray<Artist>()
    private var albumList = emptyArray<Album>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songList = navArgs.songList
        artistList = navArgs.artistList
        albumList = navArgs.albumList

        if (songList.isNotEmpty()){
            setSongAdapter()
            songAdapter.asyncListDiffer.submitList(songList.toList())
            songAdapter.asyncListDiffer.currentList.sortedByDescending { it.songDateAdded }

        }else if(artistList.isNotEmpty()){
            setArtistsAdapter()
            artistAdapter.asyncListDiffer.submitList(artistList.toList())
            artistAdapter.asyncListDiffer.currentList.sortedByDescending { it.artistName }
        }else if(albumList.isNotEmpty()){
            setAlbumAdapter()
            albumAdapter.asyncListDiffer.submitList(albumList.toList())
            albumAdapter.asyncListDiffer.currentList.sortedByDescending { it.albumName }
        }



        songAdapter.apply {
            setOnItemClickListener{song->
                playerViewModel.getEvent(
                    PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(song.songId)
                )
                val bottomSheetSong = PlayedSongBottomSheet(song)
                parentFragmentManager.let { bottomSheetSong.show(it,bottomSheetSong.tag) }
            }
            setOnMoreButtonClickListener { song->
                val moreButtonBottomSheet = MoreButtonBottomSheet(song)
                parentFragmentManager.let { moreButtonBottomSheet.show(it,moreButtonBottomSheet.tag) }

            }
        }
        artistAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putString("artistName",it.artistName)
            }
            findNavController().navigate(
                R.id.action_searchMoreButtonFragment_to_artistsAndAlbumsAndPlaylistsFragment,bundle
            )

        }
        albumAdapter.setOnItemClickListener { album ->
            val bundle = Bundle().apply {
                putString("albumName",album.albumName)
            }
            findNavController().navigate(
                R.id.action_searchMoreButtonFragment_to_artistsAndAlbumsAndPlaylistsFragment,bundle
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchMoreButtonBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun setSongAdapter() {
        songAdapter = SongAdapter()
        binding.Rv.adapter = songAdapter
        binding.Rv.layoutManager = LinearLayoutManager(context)
    }
    private fun setArtistsAdapter() {
        artistAdapter = ArtistAdapter()
        binding.Rv.adapter = artistAdapter
        binding.Rv.layoutManager = LinearLayoutManager(context)
    }
    private fun setAlbumAdapter() {
        albumAdapter = AlbumAdapter()
        binding.Rv.adapter = albumAdapter
        binding.Rv.layoutManager = LinearLayoutManager(context)
    }



}