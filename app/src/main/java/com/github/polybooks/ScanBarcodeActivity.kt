package com.github.polybooks

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

import android.util.Log
import android.widget.Toast
import java.util.concurrent.Executors
import androidx.camera.core.*
//import androidx.camera.core.CameraX.getContext
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import kotlinx.android.synthetic.main.activity_scan_barcode.*
import java.util.concurrent.ExecutorService

/*
This activity opens the camera (ask permission for it if not already given) and try to detect a barcode,
when it does it scans it, retrieve the ISBN and automatically moves to the FillSale activity passing the ISBN as intent.
 */
class ScanBarcode : AppCompatActivity() {

    /* TODO it compiles, but app crashes. maybe due to the emulator not having a camera?
     * next steps would be write tests and debug.
     * Then implement the automatic passing of ISBN to the next activity, retest and debug
     * Then clean up code, remove useless parts, and comment it
     */

    private lateinit var cameraExecutor: ExecutorService
    // TODO what about textureView? probably get rid of
    private lateinit var textureView: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode)

        // textureView = findViewById(R.id.texture_view)

        // Request camera permissions
        if (allPermissionsGranted()) {
            //startCamera()
            textureView.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // passISBN button works onClick (might remove and do it automatically in the future)

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                //startCamera()
                textureView.post { startCamera() }
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener( {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                    //preview.setSurfaceProvider(textureView.createSurfaceProvider())
                    // TODO textureview instead of viewFinder??
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer())
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
                    this, cameraSelector, preview, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    fun passISBN(view: View) {
        val stringISBN = "9876543210123"// TODO
        val intent = Intent(this, FillSale::class.java).apply {
            putExtra(ISBN, stringISBN)
        }
        startActivity(intent)
    }


    private class BarcodeAnalyzer : ImageAnalysis.Analyzer {

        // Inspired from the library guide : https://developers.google.com/ml-kit/vision/barcode-scanning/android#kotlin
        private fun scanBarcodes(image: InputImage) {
            // [START set_detector_options]
            // ISBNs are represented on EAN-13 barcodes only.
            val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_EAN_13)
                    .build()
            // [END set_detector_options]

            // [START get_detector]
            // Specifying the formats to recognize:
            val scanner = BarcodeScanning.getClient(options)
            // [END get_detector]

            // [START run_detector]
            val result = scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        // Task completed successfully
                        // [START_EXCLUDE]
                        // [START get_barcodes]
                        for (barcode in barcodes) {
                            val bounds = barcode.boundingBox
                            val corners = barcode.cornerPoints

                            val rawValue = barcode.rawValue
                            //Toast.makeText(getContext(), rawValue, Toast.LENGTH_SHORT).show()
                            Log.d("MainActivity", "barcode detected: ${rawValue}.")

                            when (barcode.valueType) {
                                Barcode.TYPE_ISBN -> {
                                    val displayValue = barcode.displayValue
                                    Log.d("MainActivity", "barcode detected: ${displayValue}.")
                                }
                            }
                        }
                        // [END get_barcodes]
                        // [END_EXCLUDE]
                    }
                    .addOnFailureListener {
                        // Task failed with an exception
                        it.printStackTrace()
                    }
            // [END run_detector]
        }

        @SuppressLint("UnsafeExperimentalUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                // Pass image to an ML Kit Vision API
                scanBarcodes(image)
                // TODO Does mediaImage needs to be closed? To test
                mediaImage?.close()
                imageProxy.close()
                /* Examples from other app
                listener(luma)
                imageProxy.close()
                //
                objectDetector
                        .process(inputImage)
                        .addOnFailureListener {
                            imageProxy.close()
                        }.addOnSuccessListener { objects ->
                                    for( it in objects) {
                                        if(binding.layout.childCount > 1)  binding.layout.removeViewAt(1)
                                        val element = Draw(this, it.boundingBox, it.labels.firstOrNull()?.text ?: "Undefined")
                                        binding.layout.addView(element,1)
                                    }
                            imageProxy.close()
                        }
                 */
            }
        }
    }


}
