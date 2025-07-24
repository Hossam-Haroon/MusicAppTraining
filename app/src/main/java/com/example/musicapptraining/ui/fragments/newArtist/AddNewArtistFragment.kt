package com.example.musicapptraining.ui.fragments.newArtist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.databinding.FragmentAddNewArtistBinding
import com.example.musicapptraining.ui.fragments.artistFragment.ArtistViewModel
import com.example.musicapptraining.ui.fragments.baseFragment.BaseFragment

class AddNewArtistFragment : BaseFragment<FragmentAddNewArtistBinding>(
    FragmentAddNewArtistBinding::inflate
) {
    private val artistViewModel: ArtistViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            checkEditTextIsEmptyOrNotAndSetResult()
        }
    }
    private fun addNewArtistToTheList(){
        val artistName = binding.editText.text.toString()
        artistViewModel.insertArtist(artistName)
        Toast.makeText(
            requireContext(),
            "your artist has been created",
            Toast.LENGTH_LONG).show()
    }
    private fun checkEditTextIsEmptyOrNotAndSetResult(){
        if (binding.editText.text.isNotEmpty()){
            addNewArtistToTheList()
            findNavController().navigateUp()
        }else{
            Toast.makeText(
                requireContext(),
                "Please enter a name for your artist",
                Toast.LENGTH_LONG).show()
        }
    }
}