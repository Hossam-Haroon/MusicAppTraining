package com.example.musicapptraining.ui.scanLocalAudiosfromDevice

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.musicapptraining.R
import com.example.musicapptraining.databinding.FragmentScanLocalAudiosFromDeviceBinding
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ScanLocalAudiosFromDeviceFragment : Fragment() {


    private lateinit var binding : FragmentScanLocalAudiosFromDeviceBinding
    private val viewModel: ScanLocalAudiosFromDeviceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.checkAndRefresh()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.audioListState.collect{uiState->
                when(uiState){
                    is UiState.Error -> Log.d("error",uiState.message)
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.finishLoadingGroup.visibility = View.VISIBLE
                        binding.scanningState2Tv.text = uiState.data.size.toString()
                    }
                }

            }
        }

        binding.scanAgainButton.setOnClickListener {
            viewModel.checkAndRefresh()
            binding.loadingProgressBar.visibility = View.VISIBLE
            binding.finishLoadingGroup.visibility = View.GONE
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanLocalAudiosFromDeviceBinding.inflate(inflater,container,false)
        return binding.root
    }
}