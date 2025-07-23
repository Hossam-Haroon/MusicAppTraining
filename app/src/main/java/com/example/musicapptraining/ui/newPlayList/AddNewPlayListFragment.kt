package com.example.musicapptraining.ui.newPlayList

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentAddNewPlayListBinding
import com.example.musicapptraining.ui.BaseFragment
import com.example.musicapptraining.ui.playlistFragment.PlaylistViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewPlayListFragment : BaseFragment<FragmentAddNewPlayListBinding>(
    FragmentAddNewPlayListBinding::inflate
) {
    private val playListViewModel : PlaylistViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            checkEditTextIsEmptyOrNotAndSetResult()
        }
    }
    private fun checkEditTextIsEmptyOrNotAndSetResult(){
        if (binding.editText.text.isNotEmpty()){
            addNewPlaylist()
            findNavController().navigateUp()
        }else{
            Toast.makeText(
                requireContext(),
                "Please enter a name for your playList",
                Toast.LENGTH_LONG).show()
        }
    }
    private fun addNewPlaylist(){
        val playListName = binding.editText.text.toString()
        playListViewModel.addNewPlayList(playListName)
        Toast.makeText(
            requireContext(),
            "your playList has been created",
            Toast.LENGTH_LONG).show()
    }
}