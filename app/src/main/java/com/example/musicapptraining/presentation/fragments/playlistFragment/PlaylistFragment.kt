package com.example.musicapptraining.presentation.fragments.playlistFragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.databinding.FragmentPlaylistBinding
import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.presentation.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.presentation.fragments.homeFragment.HomeFragmentDirections
import com.example.musicapptraining.presentation.navigation.DefaultMediaNavigator
import com.example.musicapptraining.presentation.navigation.NavigableMedia
import com.example.musicapptraining.utilities.PlaylistItem
import com.example.musicapptraining.utilities.UiState
import com.example.musicapptraining.utilities.handleUiState
import com.example.musicapptraining.utilities.setAdapterData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistFragment : BaseFragment<FragmentPlaylistBinding>(
    FragmentPlaylistBinding::inflate
) {
    private val playlistViewModel: PlaylistViewModel by activityViewModels()
    private val playlistAdapter by lazy { PlayListAdapter() }
    private lateinit var defaultMediaNavigator : DefaultMediaNavigator
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defaultMediaNavigator = DefaultMediaNavigator(findNavController())
        setPlaylistAdapterClickListeners(defaultMediaNavigator)
        setViewModelObservers()
    }
    private fun setViewModelObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                playlistViewModel.playListsState.collect { uiState ->
                    setPlaylistStateObserver(uiState)
                }
            }
        }
    }
    private fun setPlaylistStateObserver(uiState: UiState<List<Playlist>>){
        with(playlistAdapter){
            handleUiState(
                uiState = uiState,
                successState = {playLists ->
                    val itemsForAdapter = playLists.map {
                        PlaylistItem.PlaylistContent(it)
                    } + PlaylistItem.AddPlaylistButton
                    submitList(itemsForAdapter)
                    Log.d(CHECK_PLAYLISTS_RESULT, "Inserted playlist: $playLists")
                },
                errorState = {message->
                    Log.d(CHECK_PLAYLISTS_EXISTENCE,message)
                }
            )
        }
    }
    private fun setPlaylistAdapterClickListeners(defaultMediaNavigator:DefaultMediaNavigator){
        binding.playlistRv.setAdapterData(playlistAdapter)
        playlistAdapter.apply {
            setOnItemClickListener { playList ->
                defaultMediaNavigator.openSelectedMedia(
                    NavigableMedia.PlaylistMedia(playList.playlistName)
                )
            }
            setOnNewPlaListClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToAddNewPlayListFragment()
                findNavController().navigate(action)
            }
        }
    }
    companion object{
        private const val CHECK_PLAYLISTS_EXISTENCE = "check playlists existence"
        private const val CHECK_PLAYLISTS_RESULT = "playlists"
    }
}