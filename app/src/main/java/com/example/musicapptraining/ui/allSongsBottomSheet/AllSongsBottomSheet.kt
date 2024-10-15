package com.example.musicapptraining.ui.allSongsBottomSheet

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentAllSongsBottomSheetBinding
import com.example.musicapptraining.ui.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.ui.musicPlayer.MusicPlayerViewModel
import com.example.musicapptraining.ui.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.ui.songsFragment.SongAdapter
import com.example.musicapptraining.ui.songsFragment.SongsViewModel
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.UiState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllSongsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentAllSongsBottomSheetBinding
    private val playerViewModel : MusicPlayerViewModel by viewModels()
    private val songViewModel : SongsViewModel by viewModels()
    private lateinit var adapter : SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()

        songViewModel.fetchAllMusic()
        viewLifecycleOwner.lifecycleScope.launch{
            songViewModel.songListState.collect{uiState->
                when(uiState){
                    is UiState.Error -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        adapter.asyncListDiffer.submitList(uiState.data)
                        adapter.asyncListDiffer.currentList.sortedByDescending { it.songDateAdded }
                        binding.songsCount.text = uiState.data.size.toString()
                    }
                }

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
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
         val dialog =  super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? ViewGroup
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.peekHeight = 1200
                it.layoutParams.height = 1200
                it.layoutParams = it.layoutParams
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllSongsBottomSheetBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun setAdapter() {
        adapter = SongAdapter()
        binding.songsRv.adapter = adapter
        binding.songsRv.layoutManager = LinearLayoutManager(context)
        binding.songsRv.setHasFixedSize(true)
    }




}