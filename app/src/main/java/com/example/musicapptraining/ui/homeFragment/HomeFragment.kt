package com.example.musicapptraining.ui.homeFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.R
import com.example.musicapptraining.ViewPagerAdapter
import com.example.musicapptraining.databinding.FragmentHomeBinding
import com.example.musicapptraining.ui.albumFragment.AlbumFragment
import com.example.musicapptraining.ui.artistFragment.ArtistFragment
import com.example.musicapptraining.ui.musicPlayer.MusicPlayerViewModel
import com.example.musicapptraining.ui.playlistFragment.PlaylistFragment
import com.example.musicapptraining.ui.songsFragment.SongsFragment
import com.example.musicapptraining.utilities.PlayerEvents
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    val playerViewModel : MusicPlayerViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = listOf(
            SongsFragment(),
            ArtistFragment(),
           // AlbumFragment(),
            PlaylistFragment()
        )

        // Titles for the tabs
        val fragmentTitles = listOf(
            "Songs",
            "Artists",
            //"Albums",
            "Playlists"
        )

        viewPagerAdapter = ViewPagerAdapter(this@HomeFragment,fragmentList,fragmentTitles)
        binding.vpViewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tlButtons,binding.vpViewPager){tab, position->
            tab.text = fragmentTitles[position]
        }.attach()

        viewLifecycleOwner.lifecycleScope.launch {
            playerViewModel.currentSong.collect{song->
                binding.apply {
                tvSongName.text = song.songName
                tvArtistName.text = song.songArtist
                ivSongImage.setImageURI(song.songArt?.toUri())
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch{
            playerViewModel.isPausePlayClicked.collect{state->
                if (state){
                   binding.ivPlayPause.setImageResource(R.drawable.play_svgrepo_com)
                }else{
                    binding.ivPlayPause.setImageResource(R.drawable.pause_svgrepo_com)
                }
            }
        }

        binding.apply {



            ibMore.setOnClickListener {
                showMenu(it)
            }
            ibSearch.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }
            ivNextSong.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.Next)
            }
            ivPlayPause.setOnClickListener {
                if (playerViewModel.isPausePlayClicked.value){
                    ivPlayPause.setImageResource(R.drawable.play_svgrepo_com)
                }else{
                    ivPlayPause.setImageResource(R.drawable.pause_svgrepo_com)
                }

                playerViewModel.getEvent(PlayerEvents.PausePlay)
            }
        }

    }

    private fun showMenu(view : View){
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_items, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {item->
            when(item.itemId){
                R.id.find_local_songs -> {
                    findNavController().navigate(R.id.action_homeFragment_to_scanLocalAudiosFromDeviceFragment)
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





}