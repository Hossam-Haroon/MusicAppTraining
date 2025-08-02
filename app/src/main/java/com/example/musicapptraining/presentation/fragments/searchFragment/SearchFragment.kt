package com.example.musicapptraining.presentation.fragments.searchFragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapptraining.databinding.FragmentSearchBinding
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.musicPlayer.PlaybackViewModel
import com.example.musicapptraining.presentation.fragments.artistFragment.ArtistAdapter
import com.example.musicapptraining.presentation.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.presentation.fragments.songsFragment.SongAdapter
import com.example.musicapptraining.presentation.bottomSheetFragments.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.presentation.bottomSheetFragments.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.handleUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private val searchViewModel: SearchViewModel by viewModels()
    private val playerViewModel : PlaybackViewModel by activityViewModels()
    private val songAdapter by lazy { SongAdapter() }
    private val artistAdapter by lazy { ArtistAdapter() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSongAdapter()
        setArtistsAdapter()
        setViewmodelObservers()
        setSongAdapterClickListeners()
        setArtistAdapterClickListeners()
        setUiClickListeners()
    }
    private fun setSongAdapterClickListeners(){
        songAdapter.apply {
            setOnItemClickListener{song->
                playerViewModel.getEvent(
                    PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(song.songId)
                )
                showPlayedSongBottomSheet(song)
            }
            setOnMoreButtonClickListener { song->
                showMoreButtonBottomSheet(song)
            }
        }
    }
    private fun setArtistAdapterClickListeners(){
        artistAdapter.setOnItemClickListener {
            val action = SearchFragmentDirections
                .actionSearchFragmentToArtistsAndAlbumsAndPlaylistsFragment(
                    artistName = it.artistName,
                    playListName = EMPTY_STRING,
                    albumName = EMPTY_STRING
            )
            findNavController().navigate(action)
        }
    }
    private fun setViewmodelObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    setSongViewmodelObserver()
                }
                launch {
                    setArtistViewmodelObserver()
                }
            }
        }
    }
    private suspend fun setSongViewmodelObserver(){
        searchViewModel.songListState.collect{uiState->
            with(songAdapter){
                handleUiState(
                    uiState = uiState,
                    successState = {songs->
                        val sortedList = songs.sortedByDescending { it.songDateAdded }
                        submitList(sortedList)
                    },
                    errorState = {
                        Log.d(
                            CHECK_SONGS_SEARCHING,
                            "Error: can't find the required song")
                    }
                )
            }
        }
    }
    private suspend fun setArtistViewmodelObserver(){
        searchViewModel.artistListState.collect{uiState->
            with(artistAdapter){
                handleUiState(
                    uiState = uiState,
                    successState = {artists->
                        val sortedList = artists.sortedByDescending { it.artistName }
                        submitList(sortedList)
                    },
                    errorState = {
                        Log.d(
                            CHECK_ARTIST_SEARCHING,
                            "Error: can't find the required artist")
                    }
                )
            }
        }
    }
    private fun setUiClickListeners(){
        binding.songMoreTv.setOnClickListener {
            try {
                val action = SearchFragmentDirections
                    .actionSearchFragmentToSearchMoreButtonFragment(
                        searchViewModel.songListState.value.toData()!!.toTypedArray(),
                        arrayOf(),
                        arrayOf()
                    )
                findNavController().navigate(action)
            }catch (e: Exception){
                Log.e(NAVIGATING_TO_SEE_MORE_SONGS, "Navigation error: ${e.message}")
            }

        }
        binding.artistMoreTv.setOnClickListener {
            try {
                val action = SearchFragmentDirections
                    .actionSearchFragmentToSearchMoreButtonFragment(
                        arrayOf(),
                        searchViewModel.artistListState.value.toData()!!.toTypedArray(),
                        arrayOf()
                )
                findNavController().navigate(action)
            }catch (e: Exception){
                Log.e(NAVIGATING_TO_SEE_MORE_ARTISTS, "Navigation error: ${e.message}")
            }
        }
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.searchEt.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(letter: CharSequence?, p1: Int, p2: Int, p3: Int) {
                letter?.let {
                    if (it.isNotEmpty()){
                        searchViewModel.getSearchedSongs(it.toString())
                        searchViewModel.getSearchedArtists(it.toString())
                    }else{
                        searchViewModel.clearData()
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        }
        )
    }
    private fun showPlayedSongBottomSheet(song: Song){
        val bottomSheet = PlayedSongBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    private fun showMoreButtonBottomSheet(song: Song){
        val bottomSheet = MoreButtonBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    private fun setSongAdapter() {
        binding.songsRv.apply {
            adapter = this@SearchFragment.songAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }
    private fun setArtistsAdapter() {
        binding.artistsRv.apply {
            adapter = this@SearchFragment.artistAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }
    companion object{
        private const val CHECK_SONGS_SEARCHING = "check songs searching"
        private const val CHECK_ARTIST_SEARCHING = "check artist searching"
        private const val EMPTY_STRING = ""
        private const val NAVIGATING_TO_SEE_MORE_SONGS = "navigating to see more songs"
        private const val NAVIGATING_TO_SEE_MORE_ARTISTS = "navigating to see more artists"
    }
}