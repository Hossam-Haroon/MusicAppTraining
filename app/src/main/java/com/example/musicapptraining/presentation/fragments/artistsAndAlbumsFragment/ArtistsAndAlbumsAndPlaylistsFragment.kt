package com.example.musicapptraining.presentation.fragments.artistsAndAlbumsFragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.musicapptraining.databinding.FragmentArtistsAndAlbumsAndPlaylistsBinding
import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.musicPlayer.PlaybackViewModel
import com.example.musicapptraining.presentation.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.presentation.fragments.songsFragment.SongAdapter
import com.example.musicapptraining.presentation.bottomSheetFragments.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.presentation.bottomSheetFragments.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.presentation.bottomSheetFragments.sortOptionBottomSheet.SortOptionBottomSheet
import com.example.musicapptraining.utilities.OnOptionSelected
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.SortOptions
import com.example.musicapptraining.utilities.handleUiState
import com.example.musicapptraining.utilities.sortComparator
import com.example.musicapptraining.utilities.sortOptionsInBottomSheetBasedOnUserChoice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ArtistsAndAlbumsAndPlaylistsFragment :
    BaseFragment<FragmentArtistsAndAlbumsAndPlaylistsBinding>(
    FragmentArtistsAndAlbumsAndPlaylistsBinding::inflate
    ),
    OnOptionSelected {
    private val playerViewModel: PlaybackViewModel by activityViewModels()
    private val artistAndAlbumViewModel: ArtistAndAlbumViewModel by viewModels()
    private val args: ArtistsAndAlbumsAndPlaylistsFragmentArgs by navArgs()
    private val songAdapter by lazy { SongAdapter() }
    var artistName = ""
    var albumName = ""
    var playListName = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setArgsResultForArtistOrAlbumOrPlaylist()
        getAudioListBasedOnArtistOrPlayListOrAlbum()
        setClickListeners()
        setAdapterCLickListeners()
    }
    private fun setArgsResultForArtistOrAlbumOrPlaylist(){
        artistName = args.artistName
        albumName = args.albumName
        playListName = args.playListName
    }
    private fun setClickListeners(){
        binding.apply {
            playAllTv.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            playAllImg.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            sortOptions.setOnClickListener {
                showSortOptionBottomSheet()
            }
        }
    }
    private fun setAdapterCLickListeners(){
        songAdapter.apply {
            setOnItemClickListener { song ->
                playerViewModel.getEvent(
                    PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(song.songId)
                )
                showPlayedSongBottomSheet(song)
            }
            setOnMoreButtonClickListener { song ->
                showMoreButtonBottomSheet(song)
            }
        }
    }
    private fun getAudioListBasedOnArtistOrPlayListOrAlbum(){
        when {
            albumName.isNotEmpty() -> {
                artistAndAlbumViewModel.getAlbumAudioList(albumName)
                viewLifecycleOwner.lifecycleScope.launch {
                    setAlbumAudioListObserver()
                }
            }
            artistName.isNotEmpty() -> {
                artistAndAlbumViewModel.getArtistAudioList(artistName)
                viewLifecycleOwner.lifecycleScope.launch {
                    setArtistAudioListObserver()
                }
            }
            else -> {
                artistAndAlbumViewModel.getPlaylistAudioList(playListName)
                viewLifecycleOwner.lifecycleScope.launch {
                    setPlaylistAudioListObserver()
                }
            }
        }
    }
    private suspend fun setAlbumAudioListObserver(){
        artistAndAlbumViewModel.albumAudioList.collect { uiState ->
            handleUiState(
                uiState = uiState,
                successState = {album ->
                    updateAdapterWithAudioList(
                        songsCount = album.albumSongs.size.toString(),
                        songList = album.albumSongs,
                        listName = album.albumName
                    )
                    setGlideForAlbumImage(album)
                },
                errorState = {message ->
                    Log.d(ALBUM_ERROR_TAG,"ERROR is : $message")
                }
            )
        }
    }
    private suspend fun setArtistAudioListObserver(){
        artistAndAlbumViewModel.artistAudioList.collect { uiState ->
            handleUiState(
                uiState = uiState,
                successState = {artist ->
                    updateAdapterWithAudioList(
                        songsCount = artist.artistSongs.size.toString(),
                        songList = artist.artistSongs,
                        listName = artist.artistName
                    )
                },
                errorState = {message ->
                    Log.d(ARTIST_ERROR_TAG,"ERROR is : $message")
                }
            )
        }
    }
    private suspend fun setPlaylistAudioListObserver(){
        artistAndAlbumViewModel.playListAudioList.collect { uiState ->
            handleUiState(
                uiState = uiState,
                successState = {playListResult ->
                    updateAdapterWithAudioList(
                        songsCount = playListResult.playlistSongs.size.toString(),
                        songList = playListResult.playlistSongs,
                        listName = playListResult.playlistName
                    )
                },
                errorState = {message ->
                    Log.d(PLAYLIST_ERROR_TAG,"ERROR is : $message")
                }
            )
        }
    }
    private fun showPlayedSongBottomSheet(song: Song){
        val bottomSheet = PlayedSongBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    private fun showMoreButtonBottomSheet(song: Song){
        val bottomSheet = MoreButtonBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    private fun showSortOptionBottomSheet(){
        val bottomSheet = SortOptionBottomSheet.newInstance(this)
        bottomSheet.show(parentFragmentManager,tag)
    }
    private fun updateAdapterWithAudioList(
        songsCount: String,
        songList: List<Song>,
        listName: String
    ){
        binding.songsCountTv.text = songsCount
        val sortedList = songList.sortedByDescending { it.songDateAdded }
        songAdapter.submitList(sortedList)
        binding.name.text = listName
    }
    private fun setGlideForAlbumImage(album: Album) {
        Glide.with(this@ArtistsAndAlbumsAndPlaylistsFragment)
            .load(Uri.parse(album.albumArt))
            .into(binding.image)
        binding.name.text = album.albumName
    }
    private fun setAdapter() {
        binding.songsRv.apply {
            adapter = this@ArtistsAndAlbumsAndPlaylistsFragment.songAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }
    override fun onOptionSelected(sortOptions: SortOptions) {
        val comparator = sortComparator[sortOptions] ?: return
        with(songAdapter){
            sortOptionsInBottomSheetBasedOnUserChoice(
                currentList,
                sortOptions,
                this,
                comparator
            )
        }
    }
    companion object{
        private const val PLAYLIST_ERROR_TAG = "playList Error detected"
        private const val ARTIST_ERROR_TAG = "artist Error detected"
        private const val ALBUM_ERROR_TAG = "artist Error detected"
    }
}
