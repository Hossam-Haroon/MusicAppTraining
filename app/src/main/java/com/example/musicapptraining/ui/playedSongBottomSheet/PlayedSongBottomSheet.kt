package com.example.musicapptraining.ui.playedSongBottomSheet

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.musicapptraining.R
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.data.repositories.PlayListRepository
import com.example.musicapptraining.databinding.FragmentPlayedSongBottomSheetBinding
import com.example.musicapptraining.ui.allSongsBottomSheet.AllSongsBottomSheet
import com.example.musicapptraining.ui.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.ui.musicPlayer.MusicPlayerViewModel
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.UiState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okio.FileNotFoundException
import javax.inject.Inject


@AndroidEntryPoint
class PlayedSongBottomSheet(var song : Song): BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPlayedSongBottomSheetBinding
    private val playerViewModel : MusicPlayerViewModel by activityViewModels()

    @Inject
     lateinit var playListRepository: PlayListRepository

    var isSongLiked : Boolean = false

    /*override fun getTheme(): Int {
        return R.style.FullScreenBottomSheetDialog
    }*/




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // playerViewModel.forceUpdateMediaInfo()

        var likedPlayedList : PlayList? = null
        viewLifecycleOwner.lifecycleScope.launch {
                playListRepository.getLikedPlaylist().collect{uiState->
                    when(uiState){
                        is UiState.Error -> Log.d("likedPlayList",uiState.message)
                        UiState.Loading -> {}
                        is UiState.Success -> {
                            likedPlayedList = uiState.data
                        }
                    }

                }
        }.invokeOnCompletion {
            if (likedPlayedList != null && likedPlayedList!!.playlistSongs.contains(song)){
                binding.loveSongImage.setImageResource(R.drawable.yellow_heart_icon)
                val color = ContextCompat.getColor(requireContext(),R.color.main_color)
                val colorStateList  =ColorStateList.valueOf(color)
                binding.loveSongImage.imageTintList = colorStateList
                isSongLiked = true

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            playerViewModel.currentSong.collect{ currentSong->
                binding.apply {
                    songNameTv.text = playerViewModel.currentSong.value.songName
                    songArtistTv.text = playerViewModel.currentSong.value.songArtist
                    currentSongProgressTv.text =
                        playerViewModel.formatDuration(playerViewModel.currentMediaProgressionInMinutes.value)
                    fullSongLengthTv.text =
                        playerViewModel.formatDuration(playerViewModel.currentMediaDurationInMinutes.value)
                    try {
                        imageView3.setImageURI(playerViewModel.currentSong.value.songArt?.toUri())
                        Log.d("songArtCheck", "Album art found and set")
                    }catch (e:FileNotFoundException){
                        imageView3.setImageResource(R.drawable.songicon)
                        Log.d("songArtCheck", "FileNotFoundException: Album art not found, fallback image set")
                    }


                    musicProgressSeekbar.progress =
                        playerViewModel.currentMediaProgressionInMinutes.value.toInt()
                    musicProgressSeekbar.max =
                        playerViewModel.currentMediaDurationInMinutes.value.toInt()
                }
                if (likedPlayedList != null && likedPlayedList!!.playlistSongs.contains(currentSong)) {
                    binding.loveSongImage.setImageResource(R.drawable.yellow_heart_icon)
                    val color = ContextCompat.getColor(requireContext(), R.color.main_color)
                    val colorStateList = ColorStateList.valueOf(color)
                    binding.loveSongImage.imageTintList = colorStateList
                    isSongLiked = true
                } else {
                    binding.loveSongImage.setImageResource(R.drawable.heart) // Default heart
                    val color = ContextCompat.getColor(requireContext(), R.color.white)
                    val colorStateList = ColorStateList.valueOf(color)
                    binding.loveSongImage.imageTintList = colorStateList
                    isSongLiked = false
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launch{
            playerViewModel.currentMediaProgressionInMinutes.collect{
                Log.d("checkprogressnow","${playerViewModel.currentMediaProgressionInMinutes.value}")
                binding.currentSongProgressTv.text = playerViewModel.formatDuration(it)
                binding.musicProgressSeekbar.progress = it.toInt()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch{
            playerViewModel.currentMediaDurationInMinutes.collect{
                Log.d("checkdurationnow","${playerViewModel.currentMediaDurationInMinutes.value}")
                binding.currentSongProgressTv.text = playerViewModel.formatDuration(it)
                binding.musicProgressSeekbar.max = it.toInt()
            }
        }


        if(playerViewModel.isShufflingClicked.value){
            binding.playedModeImage.setImageResource(R.drawable.shuffle)
        }else if (playerViewModel.isRepeatingClicked.value){
            binding.playedModeImage.setImageResource(R.drawable.loop_1)
        }else{
            binding.playedModeImage.setImageResource(R.drawable.loop_list)
        }

        viewLifecycleOwner.lifecycleScope.launch{
            playerViewModel.isPausePlayClicked.collect{state->
                if (state){
                    binding.playPauseImage.setImageResource(R.drawable.play_svgrepo_com)
                }else{
                    binding.playPauseImage.setImageResource(R.drawable.pause_svgrepo_com)
                }
            }
        }


        /*currentSongProgressTv.text =
            playerViewModel.formatDuration(playerViewModel.currentMediaProgressionInMinutes.value)
        fullSongLengthTv.text =
            playerViewModel.formatDuration(playerViewModel.currentMediaDurationInMinutes.value)*/

        binding.apply {
            currentSongProgressTv.text = playerViewModel.formatDuration(playerViewModel.currentMediaProgressionInMinutes.value)
            fullSongLengthTv.text = playerViewModel.formatDuration(playerViewModel.currentMediaDurationInMinutes.value)
            hideBottomSheetButton.setOnClickListener { dismiss() }
            moreButton.setOnClickListener {
                val moreButtonBottomSheet = MoreButtonBottomSheet(playerViewModel.currentSong.value)
                parentFragmentManager.let { moreButtonBottomSheet.show(it,moreButtonBottomSheet.tag) }

             }
            playPauseImage.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.PausePlay)
                if (playerViewModel.isPausePlayClicked.value){
                    playPauseImage.setImageResource(R.drawable.play_svgrepo_com)
                }else{
                    playPauseImage.setImageResource(R.drawable.pause_svgrepo_com)
                }
            }
            nextSongImage.setOnClickListener { playerViewModel.getEvent(PlayerEvents.Next) }
            previousSongImage.setOnClickListener { playerViewModel.getEvent(PlayerEvents.Previous) }
            rewind.setOnClickListener { playerViewModel.getEvent(PlayerEvents.SeekBackward) }
            forward.setOnClickListener { playerViewModel.getEvent(PlayerEvents.SeekForward) }
            loveSongImage.setOnClickListener {
                if (likedPlayedList != null){

                    if (!isSongLiked){
                        playListRepository.addSongToPlayList(song,likedPlayedList!!)
                        binding.loveSongImage.setImageResource(R.drawable.yellow_heart_icon)
                        val color = ContextCompat.getColor(requireContext(),R.color.main_color)
                        val colorStateList  =ColorStateList.valueOf(color)
                        binding.loveSongImage.imageTintList = colorStateList
                        isSongLiked = true
                    }else{
                        playListRepository.deleteSongFromPlayList(song,likedPlayedList!!)
                        binding.loveSongImage.setImageResource(R.drawable.heart)
                        val color = ContextCompat.getColor(requireContext(),R.color.white)
                        val colorStateList = ColorStateList.valueOf(color)
                        binding.loveSongImage.imageTintList = colorStateList
                        isSongLiked = false
                    }
                }
            }
            playedModeImage.setOnClickListener {
                // shuffle , repeat once ,repeat
                if (playerViewModel.isShufflingClicked.value){
                    playerViewModel.getEvent(PlayerEvents.Shuffle)
                    playerViewModel.getEvent(PlayerEvents.Repeat)
                    binding.playedModeImage.setImageResource(R.drawable.loop_1)
                }else if (playerViewModel.isRepeatingClicked.value){
                    playerViewModel.getEvent(PlayerEvents.Repeat)
                    binding.playedModeImage.setImageResource(R.drawable.loop_list)

                }else{
                    playerViewModel.getEvent(PlayerEvents.Shuffle)
                    binding.playedModeImage.setImageResource(R.drawable.shuffle)
                }

            }
            listImage.setOnClickListener {

                val allSongsBottomSheet = AllSongsBottomSheet()
                parentFragmentManager.let { allSongsBottomSheet.show(it,allSongsBottomSheet.tag) }
                // bottom sheet for all songs
            }
            musicProgressSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, position: Int, fromUser: Boolean) {
                    if (fromUser){
                        playerViewModel.getEvent(
                            PlayerEvents.GoToSpecificPosition(
                                position.toLong()
                            )
                        )
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })




        }


    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? ViewGroup
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
               // behavior.peekHeight = 1200
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.layoutParams = it.layoutParams
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayedSongBottomSheetBinding.inflate(inflater,container,false)
        return binding.root

    }

    private fun setHeartIconDetails(song: Song, icon: Int, playList: PlayList, isLiked : Boolean, color : Int){
        playListRepository.deleteSongFromPlayList(song,playList)
        binding.loveSongImage.setImageResource(icon)
        val color = ContextCompat.getColor(requireContext(),color)
        val colorStateList = ColorStateList.valueOf(color)
        binding.loveSongImage.imageTintList = colorStateList
        isSongLiked = isLiked
    }

}