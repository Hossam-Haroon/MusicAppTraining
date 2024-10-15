package com.example.musicapptraining.ui.newPlayList

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentAddNewPlayListBinding
import com.example.musicapptraining.ui.playlistFragment.PlaylistViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewPlayListFragment : Fragment() {



    private lateinit var binding : FragmentAddNewPlayListBinding
    private val playListViewModel : PlaylistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        binding.btnConfirm.setOnClickListener {
         if (binding.editText.text.isNotEmpty()){
            val playListName = binding.editText.text.toString()
                playListViewModel.addNewPlayList(playListName)
                Toast.makeText(
                    requireContext(),
                    "your playList has been created",
                    Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }else{
            Toast.makeText(
                requireContext(),
                "Please enter a name for your playList",
                Toast.LENGTH_LONG).show()
         }
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewPlayListBinding.inflate(inflater,container,false)
        return binding.root
    }
}