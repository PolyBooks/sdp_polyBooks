package com.github.polybooks.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat

object CameraManip {
    const val TAG = "CameraXBasic"
    const val REQUEST_CODE_PERMISSIONS = 10
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    fun allPermissionsGranted(context: Context) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun startCamera(activity: AppCompatActivity, viewFinder: PreviewView, useCase: UseCase) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            // TODO Other option:
            //       = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    activity, cameraSelector, preview, useCase)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(activity))
    }

}