package com.example.musicapptraining.presentation.fragments.songsFragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.musicapptraining.databinding.FragmentSongsBinding
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.presentation.bottomSheetFragments.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.presentation.bottomSheetFragments.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.presentation.bottomSheetFragments.sortOptionBottomSheet.SortOptionBottomSheet
import com.example.musicapptraining.presentation.playerControllerViewModel.PlayerControllerViewModel
import com.example.musicapptraining.utilities.MoreButtonBottomSheetHandler
import com.example.musicapptraining.utilities.OnOptionSelected
import com.example.musicapptraining.utilities.PlayedSongBottomSheetHandler
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.SortOptionBottomSheetHandler
import com.example.musicapptraining.utilities.SortOptions
import com.example.musicapptraining.utilities.handleUiState
import com.example.musicapptraining.utilities.setAdapterData
import com.example.musicapptraining.utilities.sortComparator
import com.example.musicapptraining.utilities.sortOptionsInBottomSheetBasedOnUserChoice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SongsFragment :
    BaseFragment<FragmentSongsBinding>(FragmentSongsBinding::inflate),
    OnOptionSelected,PlayedSongBottomSheetHandler,
    MoreButtonBottomSheetHandler,SortOptionBottomSheetHandler
{
    private val songAdapter by lazy { SongAdapter() }
    private val songsViewModel: SongsViewModel by activityViewModels()
    private val playerViewModel: PlayerControllerViewModel by activityViewModels()
    private lateinit var songsFragmentClickBinder: SongsFragmentClickBinder
    private lateinit var permissionRequestForDeviceAudios: PermissionRequestForDeviceAudios
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionRequestForDeviceAudios = PermissionRequestForDeviceAudios(
            requireContext(),this
        )
        permissionRequestForDeviceAudios.checkRequestPermissionLauncher()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        viewLifecycleOwner.lifecycleScope.launch {
            permissionRequestForDeviceAudios.checkIfPermissionGrantedOrNotToFetchAllAudios()
            setViewModelObservers()
        }
    }
    private fun setupUI(){
        binding.songsRv.setAdapterData(songAdapter)
        songsFragmentClickBinder = SongsFragmentClickBinder(
            playerViewModel,
            songAdapter,
            binding,
            this,
            this,
            this,
            viewLifecycleOwner.lifecycleScope
        )
        permissionRequestForDeviceAudios.setSongsViewModel(songsViewModel)
        songsFragmentClickBinder.setupUIClicks()
    }
    private suspend fun setViewModelObservers(){
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            songsViewModel.songListState.collect { uiState ->
                handleUiState(
                    uiState =  uiState,
                    successState = { songList->
                        val sortedList = songList.sortedByDescending {
                            it.songDateAdded
                        }
                        Log.d("SongsFragment", "Songs loaded: ${songList.size}")
                        songAdapter.submitList(sortedList)
                        binding.songsCountTv.text = songList.size.toString()
                        playerViewModel.getEvent(
                            PlayerEvents.AddPlayList(songList)
                        )
                    },
                    errorState = {errorMessage ->
                        Log.e(
                            ERROR_WARNING,
                            "Error fetching songs: $errorMessage"
                        )
                    }
                )
            }
        }
    }
    override fun onOptionSelected(sortOptions: SortOptions) {
        val comparator  = sortComparator[sortOptions] ?: return
        with(songAdapter){
            sortOptionsInBottomSheetBasedOnUserChoice(
                currentList,
                sortOptions,
                this,
                comparator
            )
        }
    }
    override fun openPlayedSongBottomSheet(song: Song) {
        val bottomSheet = PlayedSongBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    override fun openMoreButtonBottomSheet(song: Song) {
        val bottomSheet = MoreButtonBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    override fun openSortOptionBottomSheet() {
        val bottomSheet = SortOptionBottomSheet.newInstance(this)
        bottomSheet.show(parentFragmentManager,tag)
    }
    companion object{
        const val ERROR_WARNING = "Error warning"
        const val PERMISSION_TAG = "permission request"
    }
}









