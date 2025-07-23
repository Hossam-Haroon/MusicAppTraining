package com.example.musicapptraining.ui.sortOptionBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.SortOptionBottomSheetBinding
import com.example.musicapptraining.ui.BaseBottomSheetDialogFragment
import com.example.musicapptraining.utilities.OnOptionSelected
import com.example.musicapptraining.utilities.SortOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SortOptionBottomSheet():
    BaseBottomSheetDialogFragment<SortOptionBottomSheetBinding>(
        SortOptionBottomSheetBinding::inflate
    ){
    private lateinit var onOptionSelected : OnOptionSelected
    companion object{
        var sortOption : SortOptions = SortOptions.DATE_ADDED
        fun newInstance(onOptionSelected: OnOptionSelected):SortOptionBottomSheet {
            val sortOptionBottomSheet = SortOptionBottomSheet()
            sortOptionBottomSheet.onOptionSelected = onOptionSelected
            return sortOptionBottomSheet
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWhichSortOptionIsSelected()
        binding.rgSortOptions.setOnCheckedChangeListener{_,checkedId->
            setWhenStatementForWhichIdIsChecked(checkedId)
        }
    }
    private fun setWhenStatementForWhichIdIsChecked(checkedId:Int){
        when(checkedId){
            R.id.rbDateAdded -> {
                onOptionSelected.onOptionSelected(SortOptions.DATE_ADDED)
                dismiss()
            }
            R.id.rbSongName -> {
                onOptionSelected.onOptionSelected(SortOptions.SONG_NAME)
                dismiss()
            }
            R.id.rbArtistName -> {
                onOptionSelected.onOptionSelected(SortOptions.ARTIST_NAME)
                dismiss()
            }
        }
    }
    private fun setWhichSortOptionIsSelected(){
        when{
            sortOption == SortOptions.DATE_ADDED -> {
                binding.rbDateAdded.isChecked = true
            }
            sortOption == SortOptions.SONG_NAME -> {
                binding.rbSongName.isChecked = true
            }
            sortOption == SortOptions.ARTIST_NAME -> {
                binding.rbArtistName.isChecked = true
            }
        }
    }
}