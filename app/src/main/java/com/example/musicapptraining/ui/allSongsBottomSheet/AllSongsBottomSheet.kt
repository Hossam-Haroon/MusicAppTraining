package com.example.musicapptraining.ui.allSongsBottomSheet

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.data.model.Song
import com.google.android.material.R
import com.example.musicapptraining.databinding.FragmentAllSongsBottomSheetBinding
import com.example.musicapptraining.ui.BaseBottomSheetDialogFragment
import com.example.musicapptraining.ui.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.ui.musicPlayer.MusicPlayerViewModel
import com.example.musicapptraining.ui.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.ui.songsFragment.SongAdapter
import com.example.musicapptraining.ui.songsFragment.SongsViewModel
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.UiState
import com.example.musicapptraining.utilities.handleUiState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllSongsBottomSheet :
    BaseBottomSheetDialogFragment<FragmentAllSongsBottomSheetBinding>(
        FragmentAllSongsBottomSheetBinding::inflate
    ) {
    private val playerViewModel : MusicPlayerViewModel by activityViewModels()
    private val songViewModel : SongsViewModel by activityViewModels()
    private val songAdapter by lazy { SongAdapter() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        songViewModel.fetchAllMusic()
        setViewmodelObservers()
        setAdapterClickListeners()
    }
    private fun setViewmodelObservers(){
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                songViewModel.songListState.collect{uiState->
                    with(songAdapter.asyncListDiffer){
                        handleUiState(
                            uiState = uiState,
                            successState = {songs ->
                                val sortedList = songs.sortedByDescending { it.songDateAdded }
                                submitList(sortedList)
                                binding.songsCount.text = songs.size.toString()
                            },
                            errorState = {
                                Log.d(ERROR_FETCHING_AUDIOS,
                                    "Error: can't fetch audio in AllSongsBottomSheet")
                            }
                        )
                    }
                }
            }
        }
    }
    private fun setAdapterClickListeners(){
        songAdapter.apply {
            setOnItemClickListener{song->
                playerViewModel.getEvent(
                    PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(song.songId)
                )
                setPlayedSongBottomSheet(song)
            }
            setOnMoreButtonClickListener { song->
                setMoreButtonBottomSheet(song)
            }
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                R.id.design_bottom_sheet
            ) as? ViewGroup
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.peekHeight = 1200
                it.layoutParams.height = 1200
                it.layoutParams = it.layoutParams
            }
        }
        return dialog
    }
    private fun setPlayedSongBottomSheet(song: Song){
        val bottomSheet = PlayedSongBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    private fun setMoreButtonBottomSheet(song: Song){
        val bottomSheet = MoreButtonBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    private fun setAdapter() {
        binding.songsRv.apply {
            adapter = this@AllSongsBottomSheet.songAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }
    companion object{
        const val ERROR_FETCHING_AUDIOS = "error fetching audios"
    }
}