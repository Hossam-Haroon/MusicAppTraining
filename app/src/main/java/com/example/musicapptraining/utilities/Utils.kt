package com.example.musicapptraining.utilities
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.ui.bottomSheetFragments.sortOptionBottomSheet.SortOptionBottomSheet
import java.util.Locale

val sortComparator : Map<SortOptions , Comparator<Song>> = mapOf(
    SortOptions.SONG_NAME to compareByDescending{ it.songName },
    SortOptions.ARTIST_NAME to compareByDescending{ it.songArtist },
    SortOptions.DATE_ADDED to compareByDescending{ it.songDateAdded }
)
fun <T>sortOptionsInBottomSheetBasedOnUserChoice(
     currentList : List<T>,
     sortOptions: SortOptions,
     adapter : ListAdapter<T,*>,
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
fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000) % 60
    val minutes = (durationMs / (1000 * 60)) % 60
    val hours = durationMs / (1000 * 60 * 60)
    return if (hours > 0) {
        String.format(Locale.US,"%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US,"%02d:%02d", minutes, seconds)
    }
}
fun tryAndCatchBlock(
    tryBlock : ()-> Unit,
    catchBlock: (Exception)->Unit
){
    try {
        tryBlock()
    }catch (e:Exception){
        catchBlock(e)
    }
}
class BaseDiffCallback<T>(
    private val itemsTheSame : (oldItem:T,newItem:T) -> Boolean,
    private val contentsTheSame: (oldItem:T,newItem:T) -> Boolean
): ItemCallback<T>(){
    override fun areItemsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
        return itemsTheSame(oldItem, newItem)
    }
    override fun areContentsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
        return contentsTheSame(oldItem, newItem)
    }
}