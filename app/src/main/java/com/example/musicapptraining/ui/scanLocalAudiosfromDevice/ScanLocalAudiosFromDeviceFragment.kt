package com.example.musicapptraining.ui.scanLocalAudiosfromDevice

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.musicapptraining.R
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.databinding.FragmentScanLocalAudiosFromDeviceBinding
import com.example.musicapptraining.ui.BaseFragment
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ScanLocalAudiosFromDeviceFragment :
    BaseFragment<FragmentScanLocalAudiosFromDeviceBinding>(
        FragmentScanLocalAudiosFromDeviceBinding::inflate
    ){
    private val viewModel: ScanLocalAudiosFromDeviceViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.checkAndRefresh()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.audioListState.collect{uiState->
                    handleUiState(uiState)
                }
            }
        }
        binding.scanAgainButton.setOnClickListener {
            viewModel.checkAndRefresh()
            binding.apply {
                loadingProgressBar.visibility = View.VISIBLE
                finishLoadingGroup.visibility = View.GONE
            }
        }
    }
    private fun handleUiState(uiState: UiState<List<Song>>){
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