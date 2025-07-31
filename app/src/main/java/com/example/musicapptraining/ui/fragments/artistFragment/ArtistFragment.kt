package com.example.musicapptraining.ui.fragments.artistFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentArtistBinding
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.ui.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.ui.fragments.homeFragment.HomeFragmentDirections
import com.example.musicapptraining.utilities.handleUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArtistFragment : BaseFragment<FragmentArtistBinding>(FragmentArtistBinding::inflate) {
    private val artistViewModel: ArtistViewModel by activityViewModels()
    private val artistAdapter by lazy { ArtistAdapter() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setViewModelObservers()
        setAdapterClickListeners()
    }
    private fun setViewModelObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                artistViewModel.artistListState.collect{uiState->
                    with(artistAdapter){
                        binding.apply {
                            handleUiState(
                                uiState = uiState,
                                successState = {
                                    val sortedList = currentList.sortedByDescending {it.artistName}
                                    submitList(sortedList)
                                    artistsCountTv.text = getString(
                                        R.string.artist_count,
                                        currentList.size
                                    )
                                },
                                errorState = {
                                    artistsCountTv.text = getString(R.string.error_loading_artists)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    private fun setAdapterClickListeners(){
        artistAdapter.setOnItemClickListener {artist->
            navigateToArtistsAndAlbumsAndPlaylistsFragmentWithArtistName(artist)
        }
    }
    private fun navigateToArtistsAndAlbumsAndPlaylistsFragmentWithArtistName(artist:Artist){
        val action = HomeFragmentDirections
            .actionHomeFragmentToArtistsAndAlbumsAndPlaylistsFragment(
                artistName = artist.artistName,
                albumName = "",
                playListName = ""
        )
        findNavController().navigate(action)
    }
    private fun setAdapter() {
        binding.artistsRv.apply {
            adapter = this@ArtistFragment.artistAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }
}