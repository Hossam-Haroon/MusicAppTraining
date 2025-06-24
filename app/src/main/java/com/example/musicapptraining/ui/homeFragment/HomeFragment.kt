package com.example.musicapptraining.ui.homeFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.R
import com.example.musicapptraining.ViewPagerAdapter
import com.example.musicapptraining.databinding.FragmentHomeBinding
import com.example.musicapptraining.ui.BaseFragment
import com.example.musicapptraining.ui.artistFragment.ArtistFragment
import com.example.musicapptraining.ui.musicPlayer.MusicPlayerViewModel
import com.example.musicapptraining.ui.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.ui.playlistFragment.PlaylistFragment
import com.example.musicapptraining.ui.songsFragment.SongsFragment
import com.example.musicapptraining.utilities.PlayerEvents
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    //private lateinit var binding : FragmentHomeBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    val playerViewModel : MusicPlayerViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = listOf(
            SongsFragment() to SONGS,
            ArtistFragment() to ARTISTS,
            PlaylistFragment() to PLAYLISTS
        )

        val fragmentInstances = fragmentList.map { it.first }
        val fragmentTitles = fragmentList.map { it.second }

        viewPagerAdapter = ViewPagerAdapter(
            this@HomeFragment,
            fragmentInstances,
            fragmentTitles
        )
        binding.vpViewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tlButtons,binding.vpViewPager){tab, position->
            tab.text = fragmentTitles[position]
        }.attach()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    playerViewModel.currentSong.collect{song->
                        binding.apply {
                            tvSongName.text = song.songName
                            tvArtistName.text = song.songArtist
                            ivSongImage.setImageURI(song.songArt?.toUri())
                        }
                    }
                }

                launch {
                    playerViewModel.isPausePlayClicked.collect{state->
                        if (state){
                            binding.ivPlayPause.setImageResource(R.drawable.play_svgrepo_com)
                        }else{
                            binding.ivPlayPause.setImageResource(R.drawable.pause_svgrepo_com)
                        }
                    }
                }
            }
        }

        binding.apply {
            tvSongName.setOnClickListener {
                playerViewModel.getEvent(
                    PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(
                        playerViewModel.currentSong.value.songId
                    )
                )
                val bottomSheetSong = PlayedSongBottomSheet(playerViewModel.currentSong.value)
                parentFragmentManager.let { bottomSheetSong.show(it,bottomSheetSong.tag) }
            }

            ibMore.setOnClickListener {
                showMenuForMoreOptions(it)
            }
            ibSearch.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }
            ivNextSong.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.Next)
            }
            ivPlayPause.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.PausePlay)
            }
        }

    }

    private fun showMenuForMoreOptions(view : View){
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_items, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {item->
            when(item.itemId){
                R.id.find_local_songs -> {
                    findNavController().navigate(
                        R.id.action_homeFragment_to_scanLocalAudiosFromDeviceFragment
                    )
                    true
                }
                R.id.settings ->{
                    true
                }

                else -> false
            }

        }
        popupMenu.show()
    }

    companion object{
        const val SONGS = "Songs"
        const val ARTISTS = "Artists"
        const val PLAYLISTS = "Playlists"
    }
}