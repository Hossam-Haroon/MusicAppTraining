package com.example.musicapptraining.ui.addToPlayListBottomSheet

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.R
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.databinding.FragmentAddToPlayListBottomSheetBinding
import com.example.musicapptraining.ui.songsFragment.SongAdapter
import com.example.musicapptraining.utilities.UiState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddToPlayListBottomSheetFragment(private val song : Song) : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentAddToPlayListBottomSheetBinding
    private val viewModel: AddToPlayListBottomSheetViewModel by viewModels()
    private lateinit var adapter : AddToPlayListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getPlayLists()
            viewModel.getPlayLists.collect{uiState->
                when(uiState){
                    is UiState.Error -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        adapter.asyncListDiffer.submitList(uiState.data)
                        adapter.asyncListDiffer.currentList.sortedByDescending { it.playlistName }
                    }
                }
            }
        }
        adapter.setOnItemClickListener { playList ->
            viewModel.addSongToPlayList(song, playList)
            dismiss()
        }




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddToPlayListBottomSheetBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun setAdapter() {
        adapter = AddToPlayListAdapter()
        binding.playlistNamesRv.adapter =adapter
        binding.playlistNamesRv.layoutManager = LinearLayoutManager(context)
        binding.playlistNamesRv.setHasFixedSize(true)
    }
}