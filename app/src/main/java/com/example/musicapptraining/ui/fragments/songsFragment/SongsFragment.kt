package com.example.musicapptraining.ui.fragments.songsFragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.databinding.FragmentSongsBinding
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.ui.musicPlayer.PlaybackViewModel
import com.example.musicapptraining.ui.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.ui.bottomSheetFragments.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.ui.bottomSheetFragments.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.ui.bottomSheetFragments.sortOptionBottomSheet.SortOptionBottomSheet
import com.example.musicapptraining.utilities.OnOptionSelected
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.SortOptions
import com.example.musicapptraining.utilities.handleUiState
import com.example.musicapptraining.utilities.sortComparator
import com.example.musicapptraining.utilities.sortOptionsInBottomSheetBasedOnUserChoice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

@AndroidEntryPoint
class SongsFragment :
    BaseFragment<FragmentSongsBinding>(FragmentSongsBinding::inflate),
    OnOptionSelected
{
    private val songAdapter by lazy { SongAdapter() }
    private val songsViewModel: SongsViewModel by activityViewModels()
    private val playerViewModel: PlaybackViewModel by activityViewModels()
    private var permissionContinuation: Continuation<Boolean>? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkRequestPermissionLauncher()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerViewModel.reconnectIfNeeded()
        super.onViewCreated(view, savedInstanceState)
        //playerViewModel.setMediaControllerToConnectToMediaSessionService()
        setAdapterForListOfSongs()
        viewLifecycleOwner.lifecycleScope.launch {
            checkIfPermissionGrantedOrNotToFetchAllAudios()
            setViewModelObservers()
        }
        setAdapterClickListeners()
        setCLickListeners()
    }
    private suspend fun checkIfPermissionGrantedOrNotToFetchAllAudios(){
        if (requestReadExternalStoragePermission()){
            songsViewModel.fetchAllMusic()
        }else {
            Log.i(
                PERMISSION_TAG,
                "onViewCreated: not granted permission"
            )
        }
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
    private fun setAdapterClickListeners(){
        songAdapter.apply {
            setOnItemClickListener{song->
                playerViewModel.reconnectIfNeeded()
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    playerViewModel.getEvent(
                        PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(song.songId)
                    )
                    showPlayedSongBottomSheet(song)
                }
            }
            setOnMoreButtonClickListener { song->
                showMoreButtonBottomSheet(song)
            }
        }
    }
    private fun setCLickListeners(){
        binding.apply{
            playAllTv.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            playAllImg.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            sortOptions.setOnClickListener {
                showSortOptionBottomSheet(this@SongsFragment)
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
    private fun showSortOptionBottomSheet(onOptionSelected: OnOptionSelected){
        val bottomSheet = SortOptionBottomSheet.newInstance(onOptionSelected)
        bottomSheet.show(parentFragmentManager,tag)
    }
    private fun showPlayedSongBottomSheet(song: Song){
        val bottomSheet = PlayedSongBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    private fun showMoreButtonBottomSheet(song: Song){
        val bottomSheet = MoreButtonBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    private fun checkRequestPermissionLauncher(){
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                permissionContinuation?.resume(true)
            } else {
                permissionContinuation?.resume(false)
            }
            permissionContinuation = null
        }
    }
    private suspend fun requestReadExternalStoragePermission(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val permission = checkDeviceVersionForCorrectPermission()
            checkAndRequestPermission(permission,continuation)

        }
    }
    private fun checkDeviceVersionForCorrectPermission(): String{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
    private fun checkAndRequestPermission(
        permission: String,
        continuation: CancellableContinuation<Boolean>
    ){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            continuation.resume(true)
        } else {
            permissionContinuation = continuation
            requestPermissionLauncher.launch(permission)
        }
    }
    private fun setAdapterForListOfSongs() {
        binding.songsRv.apply {
            adapter = this@SongsFragment.songAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }
    companion object{
        const val ERROR_WARNING = "Error warning"
        const val PERMISSION_TAG = "permission request"
    }
}









