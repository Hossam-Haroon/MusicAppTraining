package com.example.musicapptraining.ui.newArtist

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.databinding.FragmentAddNewArtistBinding
import com.example.musicapptraining.ui.BaseFragment
import com.example.musicapptraining.ui.artistFragment.ArtistViewModel

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