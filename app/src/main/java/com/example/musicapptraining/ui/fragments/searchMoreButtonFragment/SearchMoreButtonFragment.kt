package com.example.musicapptraining.ui.fragments.searchMoreButtonFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.data.model.Artist
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.databinding.FragmentSearchMoreButtonBinding
import com.example.musicapptraining.player.PlaybackViewModel
import com.example.musicapptraining.ui.fragments.albumFragment.AlbumAdapter
import com.example.musicapptraining.ui.fragments.artistFragment.ArtistAdapter
import com.example.musicapptraining.ui.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.ui.fragments.songsFragment.SongAdapter
import com.example.musicapptraining.ui.bottomSheetFragments.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.ui.bottomSheetFragments.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.utilities.PlayerEvents
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchMoreButtonFragment : BaseFragment<FragmentSearchMoreButtonBinding>(
        FragmentSearchMoreButtonBinding::inflate
    ){
    private var songAdapter: SongAdapter? = null
    private var artistAdapter: ArtistAdapter? = null
    private var albumAdapter: AlbumAdapter? = null
    private val navArgs: SearchMoreButtonFragmentArgs by navArgs()
    private val playerViewModel : PlaybackViewModel by activityViewModels()
    private var songList = emptyArray<Song>()
    private var artistList = emptyArray<Artist>()
    private var albumList = emptyArray<Album>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songList = navArgs.songList
        artistList = navArgs.artistList
        albumList = navArgs.albumList
        checkValidListAndSetTheSuitableAdapterBasedOnResult()
        setSongAdapterClickListeners()
        setArtistAdapterClickListeners()
    }
    private fun setSongAdapterClickListeners(){
        songAdapter?.apply {
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
        artistAdapter?.setOnItemClickListener {
            val action = SearchMoreButtonFragmentDirections.
            actionSearchMoreButtonFragmentToArtistsAndAlbumsAndPlaylistsFragment(
                it.artistName,
                EMPTY_STRING,
                EMPTY_STRING
            )
            findNavController().navigate(action)
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
    private fun checkValidListAndSetTheSuitableAdapterBasedOnResult(){
        when{
            songList.isNotEmpty() -> {
                songAdapter = SongAdapter()
                setCorrectAdapter(songAdapter!!,
                    songList.toList(),
                    sortedBy = {it.songDateAdded}
                ){sortedList ->
                    songAdapter?.submitList(sortedList)
                }
            }
            artistList.isNotEmpty() -> {
                artistAdapter = ArtistAdapter()
                setCorrectAdapter(artistAdapter!!,
                    artistList.toList(),
                    sortedBy = {it.artistName}
                ){sortedList ->
                    artistAdapter?.submitList(sortedList)
                }
            }
            albumList.isNotEmpty() -> {
                albumAdapter = AlbumAdapter()
                setCorrectAdapter(albumAdapter!!,
                    albumList.toList(),
                    sortedBy = {it.albumName}
                ){sortedList ->
                    albumAdapter?.asyncListDiffer?.submitList(sortedList)
                }
            }
        }
    }
    private fun <T,R:Comparable<R>, Adapter:RecyclerView.Adapter<*>>setCorrectAdapter(
        adapter: Adapter,
        list : List<T>,
        sortedBy: (T) -> R,
        setList: (List<T>) -> Unit
    ){
        binding.Rv.layoutManager = LinearLayoutManager(context)
        binding.Rv.adapter = adapter
        val sortedList = list.sortedByDescending(sortedBy)
        setList(sortedList)
    }
    companion object{
       private const val EMPTY_STRING = ""
    }
}



