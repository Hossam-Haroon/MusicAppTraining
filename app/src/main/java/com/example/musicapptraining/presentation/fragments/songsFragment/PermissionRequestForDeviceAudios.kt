package com.example.musicapptraining.presentation.fragments.songsFragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.musicapptraining.presentation.fragments.songsFragment.SongsFragment.Companion.PERMISSION_TAG
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class PermissionRequestForDeviceAudios(
    private val context: Context,
    private val songsFragment: SongsFragment
    ) {
    private lateinit var songsViewModel: SongsViewModel
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var permissionContinuation: Continuation<Boolean>? = null

    suspend fun checkIfPermissionGrantedOrNotToFetchAllAudios(){
        if (requestReadExternalStoragePermission()){
            songsViewModel.fetchAllMusic()
        }else {
            Log.i(
                PERMISSION_TAG,
                "onViewCreated: not granted permission"
            )
        }
    }
    fun checkRequestPermissionLauncher(){
        requestPermissionLauncher = songsFragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                permissionContinuation?.resume(true)
            } else {
                permissionContinuation?.resume(false)
            }
            permissionContinuation = null
        }
    }
    private suspend fun requestReadExternalStoragePermission(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val permission = checkDeviceVersionForCorrectPermission()
            checkAndRequestPermission(permission,continuation)
        }
    }
    private fun checkDeviceVersionForCorrectPermission(): String{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
    private fun checkAndRequestPermission(
        permission: String,
        continuation: CancellableContinuation<Boolean>
    ){
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            continuation.resume(true)
        } else {
            permissionContinuation = continuation
            requestPermissionLauncher.launch(permission)
        }
    }
    fun setSongsViewModel(viewModel:SongsViewModel){
        this.songsViewModel = viewModel
    }
}