package com.example.musicapptraining.ui.playedSongBottomSheet

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
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.data.repositories.PlayListRepository
import com.example.musicapptraining.databinding.FragmentPlayedSongBottomSheetBinding
import com.example.musicapptraining.ui.BaseBottomSheetDialogFragment
import com.example.musicapptraining.ui.allSongsBottomSheet.AllSongsBottomSheet
import com.example.musicapptraining.ui.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.ui.musicPlayer.MusicPlayerViewModel
import com.example.musicapptraining.ui.playlistFragment.PlaylistViewModel
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.UiState
import com.example.musicapptraining.utilities.getParcelableCompat
import com.example.musicapptraining.utilities.handleUiState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okio.FileNotFoundException
import javax.inject.Inject


@AndroidEntryPoint
class PlayedSongBottomSheet:
    BaseBottomSheetDialogFragment<FragmentPlayedSongBottomSheetBinding>(
        FragmentPlayedSongBottomSheetBinding::inflate
    ) {
    private val playerViewModel : MusicPlayerViewModel by activityViewModels()
    private val playlistViewModel : PlaylistViewModel by activityViewModels()
    private var isSongLiked : Boolean = false
    private lateinit var song: Song
    private var likedPlayedList : PlayList? = null
    private val currentSong = playerViewModel.currentSong.value
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
        playerViewModel.currentMediaProgressionInMinutes.collect{state->
            binding.currentSongProgressTv.text =
                playerViewModel.formatDuration(state)
            binding.musicProgressSeekbar.progress = state.toInt()
        }
    }
    private suspend fun setIsPausePlayClickedObserver(){
        playerViewModel.isPausePlayClicked.collect{state->
            setCorrectImageBasedOnIsPausePlayClickedValue(state)
        }
    }
    private fun setCorrectImageBasedOnIsPausePlayClickedValue(state:Boolean){
        if (state){
            binding.playPauseImage.setImageResource(R.drawable.play_svgrepo_com)
        }else{
            binding.playPauseImage.setImageResource(R.drawable.pause_svgrepo_com)
        }
    }
    private suspend fun setCurrentSongObserver(){
        playerViewModel.currentSong.collect{ currentSong->
            Log.d(CHECK_CURRENT_SONG,"$currentSong")
            setSongDetailsAfterCollectingCurrentSong(currentSong)
            //setHeartIconForSavedSong()
            setCorrectHeartIconAfterCheckingIfTheListOfSongsContainsCurrentSong(currentSong)
        }
    }
    private fun setCorrectHeartIconAfterCheckingIfTheListOfSongsContainsCurrentSong(
        currentSong: Song
    ){
        likedPlayedList?.let {
            when{
                it.playlistSongs.contains(currentSong) ->{
                    setHeartIconDetails(
                        icon = R.drawable.yellow_heart_icon,
                        isLiked = true,
                        color = R.color.main_color
                    )
                }
                else ->{
                    setHeartIconDetails(
                        icon = R.drawable.heart,
                        isLiked = false,
                        color = R.color.white
                    )
                }
            }
        }
    }
    private fun setSongDetailsAfterCollectingCurrentSong(currentSong:Song){
        binding.apply {
            songNameTv.text = currentSong.songName
            songArtistTv.text = currentSong.songArtist
            Log.d(
                SEEK_DURATION,
                playerViewModel.formatDuration(
                    playerViewModel.currentMediaDurationInMinutes.value
                )
            )
            currentSongProgressTv.text =
                playerViewModel.formatDuration(
                    playerViewModel.currentMediaProgressionInMinutes.value
                )
            fullSongLengthTv.text =
                playerViewModel.formatDuration(
                    playerViewModel.currentMediaDurationInMinutes.value
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
                playerViewModel.currentMediaProgressionInMinutes.value.toInt()
            musicProgressSeekbar.max =
                playerViewModel.currentMediaDurationInMinutes.value.toInt()
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
                isSongPlayedOrPaused()
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
            }
            )
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
                playerViewModel.isShufflingClicked.value ->{
                    playedModeImage.setImageResource(R.drawable.shuffle)
                }
                playerViewModel.isRepeatingClicked.value ->{
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
            playerViewModel.isShufflingClicked.value -> {
                playerViewModel.getEvent(PlayerEvents.Shuffle)
                playerViewModel.getEvent(PlayerEvents.Repeat)
                binding.playedModeImage.setImageResource(R.drawable.loop_1)
            }
            playerViewModel.isRepeatingClicked.value -> {
                playerViewModel.getEvent(PlayerEvents.Repeat)
                binding.playedModeImage.setImageResource(R.drawable.loop_list)
            }
            else -> {
                playerViewModel.getEvent(PlayerEvents.Shuffle)
                binding.playedModeImage.setImageResource(R.drawable.shuffle)
            }
        }
    }
    private fun showMoreButtonBottomSheet(){
        val moreButtonBottomSheet = MoreButtonBottomSheet.newInstance(
            currentSong
        )
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
    private fun setIfPlayedAudioLikedOrNot(likedPlayedList:PlayList){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                if (!isSongLiked){
                    playlistViewModel.addSongToPlaylist(song,likedPlayedList)
                    addSongToPlaylistUiStateObserver()
                }else{
                    playlistViewModel.deleteSongFromPlaylist(song,likedPlayedList)
                    removeSongFromPlaylistObserver()
                }
            }
        }
    }
    private suspend fun addSongToPlaylistUiStateObserver(){
        playlistViewModel.addSongToPlaylistUiState.collect{uiState->
            handleUiState(
                uiState = uiState,
                successState = {
                    setHeartIconDetails(
                        icon = R.drawable.yellow_heart_icon,
                        isLiked = !isSongLiked,
                        color = R.color.main_color
                    )
                },
                errorState = {
                    Toast.makeText(context,uiState.getMessage(),Toast.LENGTH_LONG).show()
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
                        isLiked = !isSongLiked,
                        color = R.color.white
                    )
                },
                errorState = {
                    Toast.makeText(context,uiState.getMessage(),Toast.LENGTH_LONG).show()
                }
            )
        }
    }
    private fun isSongPlayedOrPaused(){
        if (playerViewModel.isPausePlayClicked.value){
            binding.playPauseImage.setImageResource(R.drawable.play_svgrepo_com)
        }else{
            binding.playPauseImage.setImageResource(R.drawable.pause_svgrepo_com)
        }
    }
    companion object{
        const val CHECK_CURRENT_SONG = "checkCurrentSong"
        const val SONG_ART_CHECK = "songArtCheck"
        const val SEEK_DURATION = "seekDuration"
        const val LIKED_PLAYLIST = "likedPlayList"
        const val SONG_REQUIRED = "song required"
        const val ARG_SONG = "song"
        fun newInstance(song: Song):PlayedSongBottomSheet{
            return PlayedSongBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SONG,song)
                }
            }
        }
    }
}