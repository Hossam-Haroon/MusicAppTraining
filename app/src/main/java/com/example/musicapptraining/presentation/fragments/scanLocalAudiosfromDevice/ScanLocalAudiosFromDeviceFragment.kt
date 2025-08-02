package com.example.musicapptraining.presentation.fragments.scanLocalAudiosfromDevice

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.musicapptraining.databinding.FragmentScanLocalAudiosFromDeviceBinding
import com.example.musicapptraining.presentation.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.utilities.handleUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ScanLocalAudiosFromDeviceFragment :
    BaseFragment<FragmentScanLocalAudiosFromDeviceBinding>(
        FragmentScanLocalAudiosFromDeviceBinding::inflate
    ){
    private val viewModel: ScanLocalAudiosFromDeviceViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewmodelObservers()
        setCLickListeners()
    }
    private fun setViewmodelObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.audioListState.collect{uiState->
                    binding.apply {
                        handleUiState(
                            uiState = uiState,
                            successState = {songs ->
                                loadingProgressBar.visibility = View.GONE
                                finishLoadingGroup.visibility = View.VISIBLE
                                scanningState2Tv.text = songs.size.toString()
                            },
                            errorState = {
                                Toast.makeText(
                                    context,
                                    "failed to load songs, please try again",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
    private fun setCLickListeners(){
        binding.scanAgainButton.setOnClickListener {
            viewModel.checkAndRefresh()
            binding.apply {
                loadingProgressBar.visibility = View.VISIBLE
                finishLoadingGroup.visibility = View.GONE
            }
        }
    }
    companion object{
        private const val CHECK_AUDIOS_RESULT = "check audios result"
    }
}