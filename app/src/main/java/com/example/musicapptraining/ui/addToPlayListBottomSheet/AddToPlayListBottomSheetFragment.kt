package com.example.musicapptraining.ui.addToPlayListBottomSheet

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.databinding.FragmentAddToPlayListBottomSheetBinding
import com.example.musicapptraining.ui.BaseBottomSheetDialogFragment
import com.example.musicapptraining.ui.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.ui.moreButtonBottomSheet.MoreButtonBottomSheet.Companion
import com.example.musicapptraining.ui.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.utilities.getParcelableCompat
import com.google.android.material.R
import com.example.musicapptraining.utilities.handleUiState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddToPlayListBottomSheetFragment :
    BaseBottomSheetDialogFragment<FragmentAddToPlayListBottomSheetBinding>(
        FragmentAddToPlayListBottomSheetBinding::inflate
    ) {
    private val addToPlayListBottomSheetViewModel: AddToPlayListBottomSheetViewModel by viewModels()
    private lateinit var song: Song
    private val addToPlayListAdapter by lazy { AddToPlayListAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        song = arguments?.getParcelableCompat<Song>(ARG_SONG)
            ?: throw IllegalArgumentException("song required")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetBehaviourToExpanded()
        setAdapter()
        setViewmodelObservers()
        setAdapterClickListeners()
    }
    private fun setViewmodelObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                addToPlayListBottomSheetViewModel.getPlayLists.collect{uiState->
                    with(addToPlayListAdapter.asyncListDiffer){
                        handleUiState(
                            uiState,
                            successState = { playLists ->
                                val sortedList = playLists.sortedByDescending  { it.playlistName }
                                submitList(sortedList)
                            },
                            errorState = {
                                Log.d(
                                    ERROR_FETCHING_PLAYLISTS,
                                    "Error : Can't fetch playLists")
                            }
                        )
                    }
                }
            }
        }
    }
    private fun setAdapterClickListeners(){
        addToPlayListAdapter.setOnItemClickListener { playList ->
            addToPlayListBottomSheetViewModel.addSongToPlayList(song, playList)
            dismiss()
        }
    }
    private fun setBottomSheetBehaviourToExpanded(){
        val bottomSheet = dialog?.findViewById<View>(
            R.id.design_bottom_sheet
        )
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
    private fun setAdapter() {
        binding.playlistNamesRv.apply {
            adapter = this@AddToPlayListBottomSheetFragment.addToPlayListAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }
    companion object{
        const val ERROR_FETCHING_PLAYLISTS = "error fetching playLists"
        const val ARG_SONG = "song"
        fun newInstance(song: Song): AddToPlayListBottomSheetFragment {
            return AddToPlayListBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(MoreButtonBottomSheet.ARG_SONG,song)
                }
            }
        }
    }
}