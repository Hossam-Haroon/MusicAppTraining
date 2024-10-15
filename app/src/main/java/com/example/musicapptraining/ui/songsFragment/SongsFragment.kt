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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentSongsBinding
import com.example.musicapptraining.ui.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.ui.musicPlayer.MusicPlayerViewModel
import com.example.musicapptraining.ui.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.ui.sortOptionBottomSheet.SortOptionBottomSheet
import com.example.musicapptraining.utilities.OnOptionSelected
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.SortOptions
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

@AndroidEntryPoint
class SongsFragment : Fragment(R.layout.fragment_songs), OnOptionSelected {

    private lateinit var binding: FragmentSongsBinding
    private lateinit var adapter: SongAdapter

    private val viewModel: SongsViewModel by viewModels()
    private val playerViewModel: MusicPlayerViewModel by activityViewModels()

    private var permissionContinuation: Continuation<Boolean>? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        lifecycleScope.launch {
            if (requestReadExternalStoragePermission()) {
                viewModel.fetchAllMusic()

                viewModel.songListState.collect { uiState ->
                    when (uiState) {
                        is UiState.Error -> {
                            Log.e("Error", "Error fetching songs: ${uiState.message}")
                        }

                        UiState.Loading -> {}
                        is UiState.Success -> {
                            adapter.asyncListDiffer.submitList(
                                uiState.data.toList().sortedByDescending { it.songDateAdded })
                            binding.songsCountTv.text = uiState.data.size.toString()
                            playerViewModel.getEvent(PlayerEvents.AddPlayList(
                                uiState.data.sortedByDescending { it.songDateAdded }, false))
                        }
                    }
                }

            } else {
                Log.i("hello", "onViewCreated: not granted permission")
            }
        }

        adapter.apply {
            setOnItemClickListener{song->
               playerViewModel.getEvent(
                   PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(song.songId)
               )
                val bottomSheetSong = PlayedSongBottomSheet(song)
                parentFragmentManager.let { bottomSheetSong.show(it,bottomSheetSong.tag) }
            }
            setOnMoreButtonClickListener { song->
                val moreButtonBottomSheet = MoreButtonBottomSheet(song)
                parentFragmentManager.let { moreButtonBottomSheet.show(it,moreButtonBottomSheet.tag) }

            }
        }


        binding.apply{
            playAllTv.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            playAllImg.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            sort.setOnClickListener {
                val bottomSheet = SortOptionBottomSheet(this@SongsFragment)
                parentFragmentManager.let{ bottomSheet.show(it,bottomSheet.tag)}
            }
        }

    }

    private fun setAdapter() {
        adapter = SongAdapter()
        binding.songsRv.adapter = adapter
        binding.songsRv.layoutManager = LinearLayoutManager(context)
        binding.songsRv.setHasFixedSize(true)
    }

    private suspend fun requestReadExternalStoragePermission(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

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

    }

    override fun onOptionSelected(sortOptions: SortOptions) {
        when(sortOptions){
            SortOptions.SONG_NAME -> {
                adapter.asyncListDiffer.currentList.sortedByDescending { it.songName }
                SortOptionBottomSheet.sortOption = SortOptions.SONG_NAME
            }
            SortOptions.ARTIST_NAME -> {
                adapter.asyncListDiffer.currentList.sortedByDescending { it.songArtist }
                SortOptionBottomSheet.sortOption = SortOptions.ARTIST_NAME
            }
            SortOptions.DATE_ADDED -> {
                adapter.asyncListDiffer.currentList.sortedByDescending { it.songDateAdded }
                SortOptionBottomSheet.sortOption = SortOptions.DATE_ADDED
            }
        }
    }

}









