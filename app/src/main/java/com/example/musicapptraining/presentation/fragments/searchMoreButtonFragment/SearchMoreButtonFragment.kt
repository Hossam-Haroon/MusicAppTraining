package com.example.musicapptraining.presentation.fragments.searchMoreButtonFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.musicapptraining.databinding.FragmentSearchMoreButtonBinding
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.fragments.albumFragment.AlbumAdapter
import com.example.musicapptraining.presentation.fragments.artistFragment.ArtistAdapter
import com.example.musicapptraining.presentation.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.presentation.fragments.songsFragment.SongAdapter
import com.example.musicapptraining.presentation.bottomSheetFragments.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.presentation.bottomSheetFragments.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.presentation.playerControllerViewModel.PlayerControllerViewModel
import com.example.musicapptraining.utilities.MoreButtonBottomSheetHandler
import com.example.musicapptraining.utilities.PlayedSongBottomSheetHandler
import com.example.musicapptraining.utilities.setAdapterData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchMoreButtonFragment : BaseFragment<FragmentSearchMoreButtonBinding>(
        FragmentSearchMoreButtonBinding::inflate
    ),MoreButtonBottomSheetHandler,PlayedSongBottomSheetHandler{
    private val songAdapter by lazy { SongAdapter() }
    private val artistAdapter by lazy { ArtistAdapter() }
    private val albumAdapter by lazy { AlbumAdapter() }
    private val navArgs: SearchMoreButtonFragmentArgs by navArgs()
    private val playerViewModel : PlayerControllerViewModel by activityViewModels()
    private lateinit var searchMoreButtonAdaptersClickHandler: SearchMoreButtonAdaptersClickHandler
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkValidListAndSetTheSuitableAdapterBasedOnResult()
        setAdaptersForSearchMoreButtonFragment()
    }
    private fun setAdaptersForSearchMoreButtonFragment(){
        searchMoreButtonAdaptersClickHandler = SearchMoreButtonAdaptersClickHandler(
            songAdapter,
            artistAdapter,playerViewModel,findNavController(),this,
            this
        )
        searchMoreButtonAdaptersClickHandler.setAdaptersClickListeners()
    }
    private fun checkValidListAndSetTheSuitableAdapterBasedOnResult(){
        when{
            navArgs.songList.isNotEmpty() -> {
                binding.Rv.setAdapterData(songAdapter)
                val sortedList = navArgs.songList.toList().sortedByDescending { it.songDateAdded }
                songAdapter.submitList(sortedList)
            }
            navArgs.artistList.isNotEmpty() -> {
                binding.Rv.setAdapterData(artistAdapter)
                val sortedList = navArgs.artistList.toList().sortedByDescending { it.artistName }
                artistAdapter.submitList(sortedList)
            }
            else -> {
                binding.Rv.setAdapterData(albumAdapter)
                val sortedList = navArgs.albumList.toList().sortedByDescending { it.albumName }
                albumAdapter.asyncListDiffer.submitList(sortedList)
            }
        }
    }
    override fun openMoreButtonBottomSheet(song: Song) {
        val bottomSheet = MoreButtonBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    override fun openPlayedSongBottomSheet(song: Song) {
        val bottomSheet = PlayedSongBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
}



