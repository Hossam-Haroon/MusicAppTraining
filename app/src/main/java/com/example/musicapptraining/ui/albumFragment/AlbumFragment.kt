package com.example.musicapptraining.ui.albumFragment

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
import com.example.musicapptraining.databinding.FragmentAlbumBinding
import com.example.musicapptraining.ui.artistFragment.ArtistAdapter
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AlbumFragment : Fragment() {

   private lateinit var binding: FragmentAlbumBinding
   private lateinit var adapter: AlbumAdapter

    private val viewModel: AlbumViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        viewModel.getAllAlbums()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.albumsListState.collect{uiState->
                when(uiState){
                    is UiState.Error -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        adapter.asyncListDiffer.submitList(uiState.data)
                        adapter.asyncListDiffer.currentList.sortedByDescending { it.albumName }
                    }
                }

            }
        }

        binding.albumsCountTv.text  = "${adapter.asyncListDiffer.currentList.size} Albums"

        adapter.setOnItemClickListener { album ->
            val bundle = Bundle().apply {
                putString("albumName",album.albumName)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_artistsAndAlbumsAndPlaylistsFragment,bundle
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun setAdapter() {
        adapter = AlbumAdapter()
        binding.albumsRv.adapter = adapter
        binding.albumsRv.layoutManager = LinearLayoutManager(context)
        binding.albumsRv.setHasFixedSize(true)
    }
}