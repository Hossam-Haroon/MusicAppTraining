package com.example.musicapptraining.presentation.bottomSheetFragments.moreButtonBottomSheet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.databinding.FragmentMoreButtonBottomSheetBinding
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.musicPlayer.PlaybackViewModel
import com.example.musicapptraining.presentation.bottomSheetFragments.baseBottomSheet.BaseBottomSheetDialogFragment
import com.example.musicapptraining.presentation.bottomSheetFragments.addToPlayListBottomSheet.AddToPlayListBottomSheetFragment
import com.example.musicapptraining.presentation.fragments.homeFragment.HomeFragmentDirections
import com.example.musicapptraining.presentation.musicPlayer.PlayerViewModel
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.getParcelableCompat
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MoreButtonBottomSheet :
    BaseBottomSheetDialogFragment<FragmentMoreButtonBottomSheetBinding>(
        FragmentMoreButtonBottomSheetBinding::inflate
    ) {
    private val playerViewModel : PlayerViewModel by activityViewModels()
    private lateinit var  song: Song
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        song = arguments?.getParcelableCompat<Song>(ARG_SONG)
            ?: throw IllegalArgumentException("song required")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.artistSongMoreButton.text = song.songArtist
        setCLickListeners()
    }
    private fun setCLickListeners(){
        binding.apply {
            artistSongMoreButton.setOnClickListener {
                val action = HomeFragmentDirections.
                actionHomeFragmentToArtistsAndAlbumsAndPlaylistsFragment(
                    artistName = song.songArtist,
                    albumName = EMPTY_STRING,
                    playListName = EMPTY_STRING
                )
                findNavController().navigateUp()
                findNavController().navigate(action)
                dismiss()
            }
            playNextSongMoreButton.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.AddSongToPlayNext(song.songId))
                Toast.makeText(requireContext(), MUSIC_WILL_PLAY_NEXT,Toast.LENGTH_SHORT).show()
                dismiss()
            }
            songInfoSongMoreButton.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToSongInfoFragment(song)
                findNavController().navigate(action)
                dismiss()
            }
            addToPlaylistSongMoreButton.setOnClickListener {
                if (isAdded){
                    showAddToPlayListBottomSheetFragment(song)
                    dismiss()
                }
            }
            shareSongMoreButton.setOnClickListener {
                shareAudio()
                dismiss()
            }
        }
    }
    private fun showAddToPlayListBottomSheetFragment(song: Song){
        val bottomSheet = AddToPlayListBottomSheetFragment.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    private fun shareAudio(){
        val file = File(song.songPath)
        val authority = APP_ADDRESS + FILE_PROVIDER
        val path = FileProvider.getUriForFile(requireContext(),authority,file)
        val intent = Intent()
        intent.setAction(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM,path)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setType(AUDIO_MP3)
        startActivity(Intent.createChooser(intent, INTENT_TITLE))
    }
    companion object{
        const val ARG_SONG = "song"
        const val MUSIC_WILL_PLAY_NEXT = "this song will play next"
        const val AUDIO_MP3 = "audio/mp3"
        const val INTENT_TITLE = "share audio"
        const val FILE_PROVIDER = ".fileprovider"
        const val APP_ADDRESS = "com.example.musicapptraining"
        const val EMPTY_STRING = ""
        fun newInstance(song: Song): MoreButtonBottomSheet {
            return MoreButtonBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SONG,song)
                }
            }
        }
    }
}