package com.example.musicapptraining.ui.playlistFragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentPlaylistBinding
import com.example.musicapptraining.ui.songsFragment.SongAdapter
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistFragment : Fragment() {

    private lateinit var binding : FragmentPlaylistBinding
    private val viewModel: PlaylistViewModel by activityViewModels()
    private lateinit var adapter : PlayListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        viewModel.getAllPlayLists()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playListsState.collect{uiState->
                when(uiState){
                    is UiState.Error -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        Log.d("playlists", "Inserting playlist: ${uiState.data}")
                        adapter.asyncListDiff.submitList(uiState.data)

                    }
                }

            }
        }

        adapter.setOnItemClickListener { playList ->
            if (!playList.playlistName.isNotEmpty()){
                val bundle = Bundle().apply {
                    putString("playListName",playList.playlistName)
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_artistsAndAlbumsAndPlaylistsFragment,
                    bundle
                )
            }else{
                Log.e("SongsFragment", "Playlist name is null or empty.")
            }


        }
        /*binding.addPlaylistImage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addNewPlayListFragment)
        }*/
        adapter.setOnNewPlaListClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addNewPlayListFragment)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun setAdapter() {
        adapter = PlayListAdapter()
        binding.playlistRv.adapter = adapter
        binding.playlistRv.layoutManager = LinearLayoutManager(context)

    }
}