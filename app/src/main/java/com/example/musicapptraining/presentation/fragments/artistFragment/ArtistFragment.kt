package com.example.musicapptraining.presentation.fragments.artistFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentArtistBinding
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.presentation.navigation.DefaultMediaNavigator
import com.example.musicapptraining.presentation.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.presentation.navigation.NavigableMedia
import com.example.musicapptraining.utilities.UiState
import com.example.musicapptraining.utilities.handleUiState
import com.example.musicapptraining.utilities.setAdapterData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArtistFragment : BaseFragment<FragmentArtistBinding>(FragmentArtistBinding::inflate) {
    private val artistViewModel: ArtistViewModel by activityViewModels()
    private val artistAdapter by lazy { ArtistAdapter() }
    private lateinit var defaultMediaNavigator : DefaultMediaNavigator
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defaultMediaNavigator = DefaultMediaNavigator(findNavController())
        setArtistAdapterClickListeners(defaultMediaNavigator)
        setViewModelObservers()

    }
    private fun setViewModelObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                artistViewModel.artistListState.collect{uiState->
                    setArtistListStateObserver(uiState)
                }
            }
        }
    }
    private fun setArtistListStateObserver(uiState:UiState<List<Artist>>){
        with(artistAdapter){
            binding.apply {
                handleUiState(
                    uiState = uiState,
                    successState = {artists->
                        val sortedList = artists.sortedByDescending {it.artistName}
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
    private fun setArtistAdapterClickListeners(defaultMediaNavigator: DefaultMediaNavigator){
        binding.artistsRv.setAdapterData(artistAdapter)
        artistAdapter.setOnItemClickListener {artist->
            defaultMediaNavigator.openSelectedMedia(
                NavigableMedia.ArtistMedia(artist.artistName)
            )
        }
    }
}