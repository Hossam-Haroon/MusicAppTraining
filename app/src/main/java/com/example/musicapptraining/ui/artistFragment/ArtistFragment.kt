package com.example.musicapptraining.ui.artistFragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentArtistBinding
import com.example.musicapptraining.ui.songsFragment.SongAdapter
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArtistFragment : Fragment() {

   private lateinit var binding : FragmentArtistBinding
    private val viewModel: ArtistViewModel by viewModels()
    private lateinit var adapter : ArtistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        // adapter fun here


        viewModel.getAllArtists()
        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.artistListState.collect{uiState->
                when(uiState){
                    is UiState.Error -> {
                        binding.artistsCountTv.text = "Error Loading Artists"
                    }
                    UiState.Loading -> {
                        binding.artistsCountTv.text = "Loading artists..."
                    }
                    is UiState.Success -> {
                        adapter.asyncListDiffer.submitList(uiState.data)
                        adapter.asyncListDiffer.currentList.sortedByDescending { it.artistName }
                    }
                }
            }
        }
        binding.artistsCountTv.text = "${adapter.asyncListDiffer.currentList.size} Artists"

        adapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putString("artistName",it.artistName)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_artistsAndAlbumsAndPlaylistsFragment,bundle
            )
            // navigate to artists and albums sheet
            // also send the artist name to show his songs
        }

        // click on artist lambda fun

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArtistBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun setAdapter() {
        adapter = ArtistAdapter()
        binding.artistsRv.adapter = adapter
        binding.artistsRv.layoutManager = LinearLayoutManager(context)
        binding.artistsRv.setHasFixedSize(true)
    }
}