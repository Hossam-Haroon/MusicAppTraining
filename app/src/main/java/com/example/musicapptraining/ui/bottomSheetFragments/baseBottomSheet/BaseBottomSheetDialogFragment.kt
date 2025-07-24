package com.example.musicapptraining.ui.bottomSheetFragments.baseBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment<VB:ViewBinding>(
    private val inflaterBinding :
    (inflater : LayoutInflater,
     container : ViewGroup?,
     savedInstanceState : Boolean) -> VB
): BottomSheetDialogFragment() {
    private var _binding : VB? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflaterBinding(inflater,container,false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}