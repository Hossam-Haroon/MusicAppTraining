package com.example.musicapptraining.presentation.fragments.artistsAndAlbumsFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.musicapptraining.databinding.FragmentArtistsAndAlbumsAndPlaylistsBinding
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.bottomSheetFragments.moreButtonBottomSheet.MoreButtonBottomSheet
import com.example.musicapptraining.presentation.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.presentation.fragments.songsFragment.SongAdapter
import com.example.musicapptraining.utilities.PlayedSongBottomSheetHandler
import com.example.musicapptraining.presentation.bottomSheetFragments.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.presentation.bottomSheetFragments.sortOptionBottomSheet.SortOptionBottomSheet
import com.example.musicapptraining.presentation.playerControllerViewModel.PlayerControllerViewModel
import com.example.musicapptraining.utilities.MoreButtonBottomSheetHandler
import com.example.musicapptraining.utilities.SortOptionBottomSheetHandler
import com.example.musicapptraining.utilities.OnOptionSelected
import com.example.musicapptraining.utilities.SortOptions
import com.example.musicapptraining.utilities.setAdapterData
import com.example.musicapptraining.utilities.sortComparator
import com.example.musicapptraining.utilities.sortOptionsInBottomSheetBasedOnUserChoice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ArtistsAndAlbumsAndPlaylistsFragment :
    BaseFragment<FragmentArtistsAndAlbumsAndPlaylistsBinding>(
    FragmentArtistsAndAlbumsAndPlaylistsBinding::inflate
    ),
    OnOptionSelected, PlayedSongBottomSheetHandler,
    MoreButtonBottomSheetHandler, SortOptionBottomSheetHandler
{
    private val playerViewModel: PlayerControllerViewModel by activityViewModels()
    private val artistAndAlbumViewModel: ArtistAndAlbumViewModel by viewModels()
    private val args: ArtistsAndAlbumsAndPlaylistsFragmentArgs by navArgs()
    private val songAdapter by lazy { SongAdapter() }
    private lateinit var artistsAndAlbumsAndPlaylistsClickBinder:
            ArtistsAndAlbumsAndPlaylistsClickBinder
    private lateinit var audioDataProcessor: AudioDataProcessor
    private lateinit var audioStateHandler: AudioStateHandler
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeComponents()
        setupUI()
        getAudiosBasedOnArtistOrPlayListOrAlbum()
    }
    private fun initializeComponents(){
        artistsAndAlbumsAndPlaylistsClickBinder = ArtistsAndAlbumsAndPlaylistsClickBinder(
            playerViewModel,songAdapter,binding,this,
            this,this
        )
        audioDataProcessor = AudioDataProcessor(binding, songAdapter)
        audioStateHandler = AudioStateHandler()
    }
    private fun setupUI(){
        binding.songsRv.setAdapterData(songAdapter)
        artistsAndAlbumsAndPlaylistsClickBinder.setupUIClicks()
    }
    private fun getAudiosBasedOnArtistOrPlayListOrAlbum(){
        viewLifecycleOwner.lifecycleScope.launch {
            when {
                args.albumName.isNotEmpty() -> {
                    artistAndAlbumViewModel.getAlbumAudioList(args.albumName)
                    audioStateHandler.collectAndHandleAudioState(
                        artistAndAlbumViewModel.albumAudioList,
                        { album -> audioDataProcessor.processAlbumData(album) },
                        ALBUM_ERROR_TAG
                    )
                }
                args.artistName.isNotEmpty() -> {
                    artistAndAlbumViewModel.getArtistAudioList(args.artistName)
                    audioStateHandler.collectAndHandleAudioState(
                        artistAndAlbumViewModel.artistAudioList,
                        { artist -> audioDataProcessor.processArtistData(artist) },
                        ARTIST_ERROR_TAG
                    )
                }
                else -> {
                    artistAndAlbumViewModel.getPlaylistAudioList(args.playListName)
                    audioStateHandler.collectAndHandleAudioState(
                        artistAndAlbumViewModel.playListAudioList,
                        { playlist -> audioDataProcessor.processPlaylistData(playlist) },
                        PLAYLIST_ERROR_TAG
                    )
                }
            }
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
    override fun openPlayedSongBottomSheet(song: Song) {
        val bottomSheet = PlayedSongBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }

    override fun openMoreButtonBottomSheet(song: Song) {
        val bottomSheet = MoreButtonBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }

    override fun openSortOptionBottomSheet() {
        val bottomSheet = SortOptionBottomSheet.newInstance(this)
        bottomSheet.show(parentFragmentManager,tag)
    }
    companion object{
        private const val PLAYLIST_ERROR_TAG = "playList Error detected"
        private const val ARTIST_ERROR_TAG = "artist Error detected"
        private const val ALBUM_ERROR_TAG = "artist Error detected"
    }
}
