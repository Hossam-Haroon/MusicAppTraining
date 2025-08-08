package com.example.musicapptraining.presentation.fragments.homeFragment

import android.content.Context
import androidx.navigation.NavController
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentHomeBinding
import com.example.musicapptraining.utilities.PlayedSongBottomSheetHandler
import com.example.musicapptraining.presentation.playerControllerViewModel.PlayerControllerViewModel
import com.example.musicapptraining.utilities.PlayerEvents

class HomeFragmentUiListener(
    private val binding: FragmentHomeBinding,
    private val navController: NavController,
    private val playerControllerViewModel: PlayerControllerViewModel,
    private val playedSongBottomSheetHandler: PlayedSongBottomSheetHandler,
    context: Context
) {
    private val menuClickHandler = MenuClickHandler(context,navController)
     fun setCLickListeners(){
        binding.apply {
            tvSongName.setOnClickListener {
                playedSongBottomSheetHandler.openPlayedSongBottomSheet(
                    playerControllerViewModel.currentSong.value

                )
            }
            ibMore.setOnClickListener {
                menuClickHandler.showMenuForMoreOptions(it)
            }
            ibSearch.setOnClickListener {
                navController.navigate(R.id.action_homeFragment_to_searchFragment)
            }
            ivNextSong.setOnClickListener {
                playerControllerViewModel.getEvent(PlayerEvents.Next)
            }
            ivPlayPause.setOnClickListener {
                playerControllerViewModel.getEvent(PlayerEvents.PausePlay)
            }
        }
    }
}