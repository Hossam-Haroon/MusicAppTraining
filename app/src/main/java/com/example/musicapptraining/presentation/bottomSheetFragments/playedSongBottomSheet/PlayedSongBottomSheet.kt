package com.example.musicapptraining.presentation.bottomSheetFragments.playedSongBottomSheet

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentPlayedSongBottomSheetBinding
import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.bottomSheetFragments.baseBottomSheet.BaseBottomSheetDialogFragment
import com.example.musicapptraining.presentation.bottomSheetFragments.allSongsBottomSheet.AllSongsBottomSheet
import com.example.musicapptraining.presentation.bottomSheetFragments.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.presentation.fragments.playlistFragment.PlaylistViewModel
import com.example.musicapptraining.presentation.PlayerControllerViewModel.PlayerControllerViewModel
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.formatDuration
import com.example.musicapptraining.utilities.getParcelableCompat
import com.example.musicapptraining.utilities.handleUiState
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayedSongBottomSheet:
    BaseBottomSheetDialogFragment<FragmentPlayedSongBottomSheetBinding>(
        FragmentPlayedSongBottomSheetBinding::inflate
    ) {
    private val playerViewModel : PlayerControllerViewModel by activityViewModels()
    private val playlistViewModel : PlaylistViewModel by activityViewModels()
    private var isSongLiked : Boolean = false
    private lateinit var song: Song
    private var likedPlayedList : Playlist? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        song = arguments?.getParcelableCompat<Song>(ARG_SONG)
            ?: throw IllegalArgumentException(SONG_REQUIRED)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModelObserving()
        setUpUi()
        setUpClickListeners()
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) as? ViewGroup
            bottomSheet?.let {
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.layoutParams = it.layoutParams
            }
        }
        return dialog
    }
    private fun setUpViewModelObserving(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    getLikedPlayListObserver()
                }
                launch {
                    setCurrentSongObserver()
                }
                launch {
                    setIsPausePlayClickedObserver()
                }
                launch {
                    setMediaProgressionInMinutesObserver()
                }
                launch {
                    setMediaDurationInMsObserver()
                }
                launch {
                    addSongToPlaylistUiStateObserver()
                }
                launch {
                    removeSongFromPlaylistObserver()
                }
            }
        }
    }
    private suspend fun getLikedPlayListObserver(){
        playlistViewModel.likedPlaylistState.collect { uiState ->
            handleUiState(
                uiState = uiState,
                successState = {playList ->
                    likedPlayedList = playList
                    setHeartIconForSavedSong()
                },
                errorState = {message ->
                    Log.d(LIKED_PLAYLIST, message)
                }
            )
        }
    }
    private suspend fun setMediaProgressionInMinutesObserver(){
        playerViewModel.playbackProgress.collect{playbackProgress->
            binding.currentSongProgressTv.text = formatDuration(
                playbackProgress.currentMediaProgressInMs
            )
            binding.musicProgressSeekbar.progress =
                playbackProgress.currentMediaProgressInMs.toInt()
        }
    }
    private suspend fun setMediaDurationInMsObserver(){
        playerViewModel.playbackProgress.collect{playbackProgress->
            binding.fullSongLengthTv.text = formatDuration(
                playbackProgress.currentMediaDurationInMs
            )
            binding.musicProgressSeekbar.max = playbackProgress.currentMediaDurationInMs.toInt()
        }
    }
    private suspend fun setIsPausePlayClickedObserver(){
        playerViewModel.playbackState.collect{state->
            setCorrectImageBasedOnIsPausePlayClickedValue(state.isPlaying)
        }
    }
    private fun setCorrectImageBasedOnIsPausePlayClickedValue(state:Boolean){
        if (state){
            binding.playPauseImage.setImageResource(R.drawable.pause_svgrepo_com)
        }else{
            binding.playPauseImage.setImageResource(R.drawable.play_svgrepo_com)
        }
    }
    private suspend fun setCurrentSongObserver(){
        playerViewModel.currentSong.collect{ currentSong->
            Log.d(CHECK_CURRENT_SONG,"$currentSong")
            Log.d("STATE_FLOW", "current song to: ${currentSong.songName}")
            setSongDetailsAfterCollectingCurrentSong(currentSong)

        }
    }
    private suspend fun addSongToPlaylistUiStateObserver(){
        playlistViewModel.addSongToPlaylistUiState.collect{uiState->
            handleUiState(
                uiState = uiState,
                successState = {
                    setHeartIconDetails(
                        icon = R.drawable.yellow_heart_icon,
                        isLiked = true,
                        color = R.color.main_color
                    )
                },
                errorState = {message->
                    Toast.makeText(context,message,Toast.LENGTH_LONG).show()
                }
            )
        }
    }
    private suspend fun removeSongFromPlaylistObserver(){
        playlistViewModel.deleteSongFromPlaylistUiState.collect{uiState->
            handleUiState(
                uiState = uiState,
                successState = {
                    setHeartIconDetails(
                        icon = R.drawable.heart,
                        isLiked = false,
                        color = R.color.white
                    )
                },
                errorState = { message->
                    Toast.makeText(context,message,Toast.LENGTH_LONG).show()
                }
            )
        }
    }
    private fun setSongDetailsAfterCollectingCurrentSong(currentSong:Song){
        binding.apply {
            songNameTv.text = currentSong.songName
            songArtistTv.text = currentSong.songArtist
            Log.d(
                SEEK_DURATION,
                formatDuration(
                    playerViewModel.playbackProgress.value.currentMediaDurationInMs
                )
            )
            currentSongProgressTv.text =
                formatDuration(
                    playerViewModel.playbackProgress.value.currentMediaProgressInMs
                )
            fullSongLengthTv.text =
                formatDuration(
                    playerViewModel.playbackProgress.value.currentMediaDurationInMs
                )
            try {
                imageView3.setImageURI(
                    currentSong.songArt?.toUri()
                )
                Log.d(SONG_ART_CHECK, "Album art found and set")
            }catch (e:Exception){
                imageView3.setImageResource(R.drawable.songicon)
                Log.d(
                    SONG_ART_CHECK,
                    "album art not found: ${e.message}"
                )
            }
            musicProgressSeekbar.progress =
                playerViewModel.playbackProgress.value.currentMediaProgressInMs.toInt()
            musicProgressSeekbar.max =
                playerViewModel.playbackProgress.value.currentMediaDurationInMs.toInt()
        }
    }
    private fun setUpClickListeners() {
        binding.apply {
            hideBottomSheetButton.setOnClickListener { dismiss() }
            moreButton.setOnClickListener {
                showMoreButtonBottomSheet()
            }
            playPauseImage.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.PausePlay)
            }
            nextSongImage.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.Next)
            }
            previousSongImage.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.Previous)
            }
            rewind.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.SeekBackward)
            }
            forward.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.SeekForward)
            }
            loveSongImage.setOnClickListener {
                likedPlayedList?.let { setIfPlayedAudioLikedOrNot(it) }
            }
            playedModeImage.setOnClickListener {
                setPlayedModeState()
            }
            listImage.setOnClickListener {
                showAllSongsBottomSheet()
            }
            musicProgressSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    position: Int,
                    fromUser: Boolean
                ) {
                    goToSpecificPositionInSong(fromUser, position)
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            })
        }
    }
    private fun setHeartIconForSavedSong(){
        likedPlayedList?.let {
            if (it.playlistSongs.contains(song)){
                setHeartIconDetails(
                    icon = R.drawable.yellow_heart_icon,
                    isLiked = true,
                    color = R.color.main_color
                )
            }
        }
    }
    private fun setHeartIconDetails(
        icon: Int,
        isLiked : Boolean,
        color : Int
    ){
        binding.loveSongImage.setImageResource(icon)
        val setColor = ContextCompat.getColor(requireContext(),color)
        val colorStateList = ColorStateList.valueOf(setColor)
        binding.loveSongImage.imageTintList = colorStateList
        isSongLiked = isLiked
    }
    private fun showAllSongsBottomSheet(){
        AllSongsBottomSheet().show(parentFragmentManager,tag)
    }
    private fun setUpUi(){
        binding.apply {
            when{
                playerViewModel.playbackState.value.isShufflingClicked ->{
                    playedModeImage.setImageResource(R.drawable.shuffle)
                }
                playerViewModel.playbackState.value.isRepeatingClicked ->{
                    playedModeImage.setImageResource(R.drawable.loop_1)
                }
                else ->{
                    playedModeImage.setImageResource(R.drawable.loop_list)
                }
            }
        }
    }
    private fun setPlayedModeState() {
        when {
            playerViewModel.playbackState.value.isShufflingClicked -> {
                Log.d("checkMode","shuffle:${playerViewModel.playbackState.value.isShufflingClicked}")
                playerViewModel.getEvent(PlayerEvents.Shuffle)
                playerViewModel.getEvent(PlayerEvents.Repeat)
                binding.playedModeImage.setImageResource(R.drawable.loop_1)
            }
            playerViewModel.playbackState.value.isRepeatingClicked -> {
                Log.d("checkMode","repeat:${playerViewModel.playbackState.value.isRepeatingClicked}")
                playerViewModel.getEvent(PlayerEvents.Repeat)
                binding.playedModeImage.setImageResource(R.drawable.loop_list)
            }
            else -> {
                Log.d("checkMode1","shuffle:${playerViewModel.playbackState.value.isShufflingClicked}")
                Log.d("checkMode","repeat:${playerViewModel.playbackState.value.isRepeatingClicked}")
                playerViewModel.getEvent(PlayerEvents.Shuffle)
                binding.playedModeImage.setImageResource(R.drawable.shuffle)
            }
        }
    }
    private fun showMoreButtonBottomSheet(){
        val moreButtonBottomSheet = MoreButtonBottomSheet.newInstance(song)
        moreButtonBottomSheet.show(parentFragmentManager,tag)
    }
    private fun goToSpecificPositionInSong(fromUser : Boolean, position:Int){
        if (fromUser){
            playerViewModel.getEvent(
                PlayerEvents.GoToSpecificPosition(
                    position.toLong()
                )
            )
        }
    }
    private fun setIfPlayedAudioLikedOrNot(likedPlayedList:Playlist){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                if (!isSongLiked){
                    playlistViewModel.addSongToPlaylist(song,likedPlayedList)
                }else{
                    playlistViewModel.deleteSongFromPlaylist(song,likedPlayedList)
                }
            }
        }
    }
    companion object{
        const val CHECK_CURRENT_SONG = "checkCurrentSong"
        const val SONG_ART_CHECK = "songArtCheck"
        const val SEEK_DURATION = "seekDuration"
        const val LIKED_PLAYLIST = "likedPlayList"
        const val SONG_REQUIRED = "song required"
        const val ARG_SONG = "song"
        fun newInstance(song: Song): PlayedSongBottomSheet {
            return PlayedSongBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SONG,song)
                }
            }
        }
    }
}