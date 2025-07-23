package com.example.musicapptraining.utilities
import androidx.recyclerview.widget.AsyncListDiffer
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.ui.sortOptionBottomSheet.SortOptionBottomSheet

val sortComparator : Map<SortOptions , Comparator<Song>> = mapOf(
    SortOptions.SONG_NAME to compareByDescending{ it.songName },
    SortOptions.ARTIST_NAME to compareByDescending{ it.songArtist },
    SortOptions.DATE_ADDED to compareByDescending{ it.songDateAdded }
)
fun <T>sortOptionsInBottomSheetBasedOnUserChoice(
     currentList : List<T>,
     sortOptions: SortOptions,
     adapter : AsyncListDiffer<T>,
     comparator: Comparator<T>
){
    val sortedList = currentList.sortedWith(comparator)
    SortOptionBottomSheet.sortOption = sortOptions
    adapter.submitList(sortedList)
}
fun <T>handleUiState(
    uiState:UiState<T>,
    successState : (T) -> Unit,
    errorState : (String) -> Unit
){
    when (uiState) {
        is UiState.Error -> {
            errorState(uiState.message)
        }
        UiState.Loading -> {}
        is UiState.Success -> {
            successState(uiState.data)
        }
    }
}