package com.example.musicapptraining.ui.songsFragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.R
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.databinding.FragmentSongsBinding
import com.example.musicapptraining.ui.BaseFragment
import com.example.musicapptraining.ui.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.ui.musicPlayer.MusicPlayerViewModel
import com.example.musicapptraining.ui.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.ui.sortOptionBottomSheet.SortOptionBottomSheet
import com.example.musicapptraining.utilities.OnOptionSelected
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.SortOptions
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

@AndroidEntryPoint
class SongsFragment :
    BaseFragment<FragmentSongsBinding>(FragmentSongsBinding::inflate),
    OnOptionSelected
{
    private lateinit var adapter: SongAdapter
    private val songsViewModel: SongsViewModel by viewModels()
    private val playerViewModel: MusicPlayerViewModel by activityViewModels()
    private var permissionContinuation: Continuation<Boolean>? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkRequestPermissionLauncher()

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapterForListOfSongs()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                if (requestReadExternalStoragePermission()) {
                    songsViewModel.fetchAllMusic()
                    songsViewModel.songListState.collect { uiState ->
                        handleUiState(uiState)
                    }
                } else {
                    Log.i(
                        PERMISSION_TAG,
                        "onViewCreated: not granted permission"
                    )
                }
            }
        }
        adapter.apply {
            setOnItemClickListener{song->
               playerViewModel.getEvent(
                   PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(song.songId)
               )
                val bottomSheetSong = PlayedSongBottomSheet(song)
                parentFragmentManager.let {
                    bottomSheetSong.show(it,bottomSheetSong.tag)
                }
            }
            setOnMoreButtonClickListener { song->
                val moreButtonBottomSheet = MoreButtonBottomSheet(song)
                parentFragmentManager.let {
                    moreButtonBottomSheet.show(it,moreButtonBottomSheet.tag)
                }
            }
        }
        binding.apply{
            playAllTv.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            playAllImg.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            sortOptions.setOnClickListener {
                val bottomSheet = SortOptionBottomSheet(this@SongsFragment)
                parentFragmentManager.let{ bottomSheet.show(it,bottomSheet.tag)}
            }
        }
    }
    override fun onOptionSelected(sortOptions: SortOptions) {
        when(sortOptions){
            SortOptions.SONG_NAME -> {
                sortOptionsInBottomSheetBasedOnUserChoice(SortOptions.SONG_NAME){songList->
                    songList.sortedByDescending {
                        it.songName
                    }
                }
            }
            SortOptions.ARTIST_NAME -> {
                //SortOptionBottomSheet.sortOption = SortOptions.ARTIST_NAME
                sortOptionsInBottomSheetBasedOnUserChoice(SortOptions.ARTIST_NAME){songList->
                    songList.sortedByDescending {
                        it.songArtist
                    }
                }
            }
            SortOptions.DATE_ADDED -> {
                sortOptionsInBottomSheetBasedOnUserChoice(SortOptions.DATE_ADDED){ songList ->
                    songList.sortedByDescending {
                        it.songDateAdded
                    }
                }
            }
        }
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
    private fun sortOptionsInBottomSheetBasedOnUserChoice(
        sortOptions: SortOptions,
        enteredSortedList : (List<Song>)-> List<Song>
    ){
        val sortedList = enteredSortedList(adapter.asyncListDiffer.currentList)
        SortOptionBottomSheet.sortOption = sortOptions
        adapter.asyncListDiffer.submitList(sortedList)
    }
    private fun handleUiState(uiState:UiState<List<Song>>){
        when (uiState) {
            is UiState.Error -> {
                Log.e(
                    ERROR_WARNING,
                    "Error fetching songs: ${uiState.message}"
                )
            }

            UiState.Loading -> {}
            is UiState.Success -> {
                val sortedList = uiState.data.sortedByDescending { it.songDateAdded }
                adapter.asyncListDiffer.submitList(sortedList)
                binding.songsCountTv.text = uiState.data.size.toString()
                playerViewModel.getEvent(
                    PlayerEvents.AddPlayList(
                        sortedList,
                        false
                    )
                )
            }
        }
    }
    private fun setAdapterForListOfSongs() {
        adapter = SongAdapter()
        binding.songsRv.apply {
            adapter = adapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }
    companion object{
        const val ERROR_WARNING = "Error warning"
        const val PERMISSION_TAG = "permission request"
    }

}









