package com.example.musicapptraining.ui.sortOptionBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.SortOptionBottomSheetBinding
import com.example.musicapptraining.utilities.OnOptionSelected
import com.example.musicapptraining.utilities.SortOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SortOptionBottomSheet(var onOptionSelected : OnOptionSelected): BottomSheetDialogFragment() {

    private lateinit var binding : SortOptionBottomSheetBinding
    //var onOptionSelected : OnOptionSelected? = null

    companion object{
        var sortOption : SortOptions = SortOptions.DATE_ADDED

        /*fun getInstance(
            onOptionSelected: OnOptionSelected
        ): SortOptionBottomSheet{
            val fragmentBottomSheet = SortOptionBottomSheet()
            fragmentBottomSheet.onOptionSelected = onOptionSelected
            return fragmentBottomSheet
        }*/
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.rgSortOptions.setOnCheckedChangeListener{_,checkedId->
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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SortOptionBottomSheetBinding.inflate(inflater,container,false)
        return binding.root
    }



}