package com.example.musicapptraining.ui.artistsAndAlbumsFragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentArtistsAndAlbumsAndPlaylistsBinding
import com.example.musicapptraining.ui.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.ui.musicPlayer.MusicPlayerViewModel
import com.example.musicapptraining.ui.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.ui.songsFragment.SongAdapter
import com.example.musicapptraining.ui.sortOptionBottomSheet.SortOptionBottomSheet
import com.example.musicapptraining.utilities.OnOptionSelected
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.SortOptions
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ArtistsAndAlbumsAndPlaylistsFragment : Fragment(),OnOptionSelected {

    private lateinit var binding: FragmentArtistsAndAlbumsAndPlaylistsBinding
    private val playerViewModel : MusicPlayerViewModel by activityViewModels()
    private val artistAndAlbumViewModel : ArtistAndAlbumViewModel by viewModels()
    val args : ArtistsAndAlbumsAndPlaylistsFragmentArgs by navArgs()
    private lateinit var adapter : SongAdapter

    var artistName = ""
    var albumName = ""
    var playListName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        artistName = args.artistName
        albumName = args.albumName
        playListName = args.playListName

        if (!albumName.equals("")){
            artistAndAlbumViewModel.getAlbumAudioList(albumName)
            viewLifecycleOwner.lifecycleScope.launch{
                artistAndAlbumViewModel.albumAudioList.collect{uiState->
                    when(uiState){
                        is UiState.Error -> {}
                        UiState.Loading -> {}
                        is UiState.Success ->{
                            binding.songsCountTv.text = uiState.data.albumSongs.size.toString()
                            adapter.asyncListDiffer.submitList(uiState.data.albumSongs)
                            adapter.asyncListDiffer.currentList.sortedByDescending { it.songDateAdded }
                            Glide.with(this@ArtistsAndAlbumsAndPlaylistsFragment)
                                .load(Uri.parse(uiState.data.albumArt))
                                .into(binding.image)
                            binding.name.text = uiState.data.albumName
                        }
                    }

                }
            }

        }else if(!artistName.equals("")){
            artistAndAlbumViewModel.getArtistAudioList(artistName)
            viewLifecycleOwner.lifecycleScope.launch {
                artistAndAlbumViewModel.artistAudioList.collect{uiState->
                    when(uiState){
                        is UiState.Error -> TODO()
                        UiState.Loading -> TODO()
                        is UiState.Success ->{
                            binding.songsCountTv.text = uiState.data.artistSongs.size.toString()
                            adapter.asyncListDiffer.submitList(uiState.data.artistSongs)
                            adapter.asyncListDiffer.currentList.sortedByDescending { it.songDateAdded }
                            binding.name.text = uiState.data.artistName
                        }
                    }
                }
            }

        }else{
            artistAndAlbumViewModel.getPlaylistAudioList(playListName)
            viewLifecycleOwner.lifecycleScope.launch {
                artistAndAlbumViewModel.playListAudioList.collect{uiState->
                    when(uiState){
                        is UiState.Error -> TODO()
                        UiState.Loading -> TODO()
                        is UiState.Success ->{
                            binding.songsCountTv.text = uiState.data.playlistSongs.size.toString()
                            adapter.asyncListDiffer.submitList(uiState.data.playlistSongs)
                            adapter.asyncListDiffer.currentList.sortedByDescending { it.songDateAdded }
                            binding.name.text = uiState.data.playlistName
                        }
                    }
                }
            }
        }

        binding.apply {
            playAllTv.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            playAllImg.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            sort.setOnClickListener {
                val sortOptionBottomSheet = SortOptionBottomSheet(
                    this@ArtistsAndAlbumsAndPlaylistsFragment
                )
                parentFragmentManager.let { sortOptionBottomSheet.show(it,sortOptionBottomSheet.tag) }
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistsAndAlbumsAndPlaylistsBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun setAdapter() {
        adapter = SongAdapter()
        binding.songsRv.adapter = adapter
        binding.songsRv.layoutManager = LinearLayoutManager(context)
        binding.songsRv.setHasFixedSize(true)
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
