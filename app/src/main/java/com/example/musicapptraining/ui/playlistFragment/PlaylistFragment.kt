package com.example.musicapptraining.ui.playlistFragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentPlaylistBinding
import com.example.musicapptraining.ui.BaseFragment
import com.example.musicapptraining.ui.homeFragment.HomeFragmentDirections
import com.example.musicapptraining.ui.songsFragment.SongAdapter
import com.example.musicapptraining.utilities.UiState
import com.example.musicapptraining.utilities.handleUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistFragment : BaseFragment<FragmentPlaylistBinding>(
    FragmentPlaylistBinding::inflate
) {
    private val playlistViewModel: PlaylistViewModel by activityViewModels()
    private val playlistAdapter by lazy { PlayListAdapter() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setViewModelObservers()
        setAdapterCLickListeners()
    }
    private fun setViewModelObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                playlistViewModel.playListsState.collect { uiState ->
                    with(playlistAdapter.asyncListDiff){
                        handleUiState(
                            uiState = uiState,
                            successState = {playLists ->
                                submitList(playLists)
                                Log.d(CHECK_PLAYLISTS_RESULT, "Inserted playlist: $playLists")
                            },
                            errorState = {message->
                                Log.d(CHECK_PLAYLISTS_EXISTENCE,message)
                            }
                        )
                    }
                }
            }
        }
    }
    private fun setAdapterCLickListeners(){
        playlistAdapter.apply {
            setOnItemClickListener { playList ->
                try {
                    val action = HomeFragmentDirections.
                    actionHomeFragmentToArtistsAndAlbumsAndPlaylistsFragment(
                        artistName = "",
                        playListName = playList.playlistName,
                        albumName = ""
                    )
                    findNavController().navigate(action)
                }catch (e: Exception) {
                    Log.d(CHECK_PLAYLIST_ERROR, "${e.message}")
                }
            }
            setOnNewPlaListClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToAddNewPlayListFragment()
                findNavController().navigate(action)
            }
        }
    }
    private fun setAdapter() {
        binding.playlistRv.apply {
            adapter = this@PlaylistFragment.playlistAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }
    companion object{
        private const val CHECK_PLAYLIST_ERROR = "checkListErrors"
        private const val CHECK_PLAYLISTS_EXISTENCE = "check playlists existence"
        private const val CHECK_PLAYLISTS_RESULT = "playlists"
    }
}