package com.example.musicapptraining.ui.searchFragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentSearchBinding
import com.example.musicapptraining.ui.albumFragment.AlbumAdapter
import com.example.musicapptraining.ui.artistFragment.ArtistAdapter
import com.example.musicapptraining.ui.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.ui.musicPlayer.MusicPlayerViewModel
import com.example.musicapptraining.ui.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.ui.songsFragment.SongAdapter
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()
    private val playerViewModel : MusicPlayerViewModel by viewModels()
    private lateinit var binding : FragmentSearchBinding
    private lateinit var songAdapter: SongAdapter
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSongAdapter()
        setArtistsAdapter()
        setAlbumAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.songListState.collect{uiState->
                when(uiState){
                    is UiState.Error -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        songAdapter.asyncListDiffer.submitList(uiState.data)
                        songAdapter.asyncListDiffer.currentList.sortedByDescending { it.songDateAdded }
                    }
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.artistListState.collect{uiState->
                when(uiState){
                    is UiState.Error -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        artistAdapter.asyncListDiffer.submitList(uiState.data)
                        artistAdapter.asyncListDiffer.currentList.sortedByDescending { it.artistName }
                    }
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.albumListState.collect{uiState->
                when(uiState){
                    is UiState.Error -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        albumAdapter.asyncListDiffer.submitList(uiState.data)
                        albumAdapter.asyncListDiffer.currentList.sortedByDescending { it.albumName }
                    }
                }

            }
        }

        songAdapter.apply {
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
        artistAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putString("artistName",it.artistName)
            }
            findNavController().navigate(
                R.id.action_searchFragment_to_artistsAndAlbumsAndPlaylistsFragment,bundle
            )

        }
        albumAdapter.setOnItemClickListener { album ->
            val bundle = Bundle().apply {
                putString("albumName",album.albumName)
            }
            findNavController().navigate(
                R.id.action_searchFragment_to_artistsAndAlbumsAndPlaylistsFragment,bundle
            )
        }
        binding.songMoreTv.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToSearchMoreButtonFragment(
                viewModel.songListState.value.toData()!!.toTypedArray(),
                arrayOf(),
                arrayOf()
            )
            findNavController().navigate(action)
        }
        binding.artistMoreTv.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToSearchMoreButtonFragment(
                arrayOf(),
                viewModel.artistListState.value.toData()!!.toTypedArray(),
                arrayOf()
            )
            findNavController().navigate(action)
        }
        binding.albumMoreTv.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToSearchMoreButtonFragment(
                arrayOf(),
                arrayOf(),
                viewModel.albumListState.value.toData()!!.toTypedArray()

            )
            findNavController().navigate(action)
        }
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.searchEt.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(letter: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (letter!!.isNotEmpty()){
                    viewModel.getSearchedSongs(letter.toString())
                    viewModel.getSearchedArtists(letter.toString())
                    viewModel.getSearchedAlbums(letter.toString())
                    binding.searchItemsGroup.visibility = View.VISIBLE
                }else{
                    viewModel.clearData()
                    binding.searchItemsGroup.visibility = View.GONE
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun setSongAdapter() {
        songAdapter = SongAdapter()
        binding.songsRv.adapter = songAdapter
        binding.songsRv.layoutManager = LinearLayoutManager(context)
        binding.songsRv.setHasFixedSize(true)
    }
    private fun setArtistsAdapter() {
        artistAdapter = ArtistAdapter()
        binding.artistsRv.adapter = artistAdapter
        binding.artistsRv.layoutManager = LinearLayoutManager(context)
    }
    private fun setAlbumAdapter() {
        albumAdapter = AlbumAdapter()
        binding.albumsRv.adapter = albumAdapter
        binding.albumsRv.layoutManager = LinearLayoutManager(context)
    }
}