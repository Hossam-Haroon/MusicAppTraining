package com.example.musicapptraining.presentation.fragments.searchFragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.databinding.FragmentSearchBinding
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.musicPlayer.PlaybackViewModel
import com.example.musicapptraining.presentation.fragments.artistFragment.ArtistAdapter
import com.example.musicapptraining.presentation.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.presentation.fragments.songsFragment.SongAdapter
import com.example.musicapptraining.presentation.bottomSheetFragments.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.presentation.bottomSheetFragments.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.presentation.musicPlayer.PlayerViewModel
import com.example.musicapptraining.utilities.MoreButtonBottomSheetHandler
import com.example.musicapptraining.utilities.PlayedSongBottomSheetHandler
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.handleUiState
import com.example.musicapptraining.utilities.setAdapterData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate),
MoreButtonBottomSheetHandler,PlayedSongBottomSheetHandler{
    private val searchViewModel: SearchViewModel by viewModels()
    private val playerViewModel : PlayerViewModel by activityViewModels()
    private val songAdapter by lazy { SongAdapter() }
    private val artistAdapter by lazy { ArtistAdapter() }
    private lateinit var searchFragmentClickBinder: SearchFragmentClickBinder
    private lateinit var searchFragmentViewModelsObservers: SearchFragmentViewModelsObservers
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setViewModelObservers()
    }
    private fun setViewModelObservers(){
        searchFragmentViewModelsObservers = SearchFragmentViewModelsObservers(
            searchViewModel,songAdapter,artistAdapter,
            viewLifecycleOwner.lifecycleScope,viewLifecycleOwner)
        searchFragmentViewModelsObservers.setViewmodelObservers()
    }
    private fun setupUI(){
        searchFragmentClickBinder = SearchFragmentClickBinder(binding,songAdapter,artistAdapter,
            playerViewModel,
            findNavController(),searchViewModel,this,
            this
        )
        binding.songsRv.setAdapterData(songAdapter)
        binding.artistsRv.setAdapterData(artistAdapter)
        searchFragmentClickBinder.setupUIClickBinders()
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