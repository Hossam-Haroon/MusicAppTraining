package com.example.musicapptraining.ui.moreButtonBottomSheet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.R
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.databinding.FragmentMoreButtonBottomSheetBinding
import com.example.musicapptraining.ui.addToPlayListBottomSheet.AddToPlayListBottomSheetFragment
import com.example.musicapptraining.ui.musicPlayer.MusicPlayerViewModel
import com.example.musicapptraining.utilities.PlayerEvents
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MoreButtonBottomSheet(val song : Song) : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentMoreButtonBottomSheetBinding
    private val playerViewModel : MusicPlayerViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.artistSongMoreButton.text = song.songArtist
        binding.albumSongMoreButton.text = song.songAlbum
        binding.apply {
            artistSongMoreButton.setOnClickListener {
                val bundle = Bundle().apply {
                        putString("artistName", song.songArtist)
                }
                findNavController().navigateUp()
                findNavController().navigate(
                    R.id.action_homeFragment_to_artistsAndAlbumsAndPlaylistsFragment,
                    bundle
                )
                dismiss()
            }
            albumSongMoreButton.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("artistName", song.songAlbum)
                }
                findNavController().navigateUp()
                findNavController().navigate(
                    R.id.action_homeFragment_to_artistsAndAlbumsAndPlaylistsFragment,
                    bundle
                )
                dismiss()
            }
            playNextSongMoreButton.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.AddSongToPlayNext(song.songId))
                Toast.makeText(requireContext(),"this song will play next", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            songInfoSongMoreButton.setOnClickListener {
                val bundle = Bundle().apply {
                    putParcelable("song",song)
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_songInfoFragment,bundle
                )
                dismiss()
            }
            addToPlaylistSongMoreButton.setOnClickListener {
                val addToPlayListBottomSheet = AddToPlayListBottomSheetFragment(song)
                parentFragmentManager.let { addToPlayListBottomSheet.show(it,addToPlayListBottomSheet.tag) }
                dismiss()
            }
            shareSongMoreButton.setOnClickListener {
                shareAudio()
                dismiss()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreButtonBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun shareAudio(){
        val file = File(song.songPath)
        val authority ="com.example.musicapptraining"+ ".fileprovider"
        val path = FileProvider.getUriForFile(requireContext(),authority,file)
        val intent = Intent()
        intent.setAction(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM,path)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setType("audio/mp3")
        startActivity(Intent.createChooser(intent,"share audio"))

    }

}