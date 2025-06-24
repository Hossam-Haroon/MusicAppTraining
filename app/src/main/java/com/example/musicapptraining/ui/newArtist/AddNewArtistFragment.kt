package com.example.musicapptraining.ui.newArtist

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.databinding.FragmentAddNewArtistBinding

class AddNewArtistFragment : Fragment() {


    private lateinit var binding : FragmentAddNewArtistBinding
    private val viewModel: AddNewArtistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirm.setOnClickListener {
            if (binding.editText.text.isNotEmpty()){
                val artistName = binding.editText.text.toString()
                viewModel.insertArtist(artistName)
                Toast.makeText(
                    requireContext(),
                    "your artist has been created",
                    Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }else{
                Toast.makeText(
                    requireContext(),
                    "Please enter a name for your artist",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewArtistBinding.inflate(inflater,container,false)
        return binding.root
    }
}