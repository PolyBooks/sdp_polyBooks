package com.github.polybooks

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.activity_scan_barcode.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/*
This activity opens the camera (ask permission for it if not already given) and try to detect a barcode,
when it does it scans it, retrieve the ISBN and automatically moves to the FillSale activity passing the ISBN as intent.
 */
class ScanBarcodeActivity : AppCompatActivity() {

    /* TODO next steps would be to refactor and write tests
     * Then implement the automatic passing of ISBN to the next activity, retest and debug
     * Then clean up code and comment it
     */

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // TODO passISBN button works onClick (might remove and do it automatically in the future)

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
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

    fun passISBN(stringISBN: String) {
        val intent = Intent(this, FillSaleActivity::class.java).apply {
            putExtra(ISBN, stringISBN)
        }
        startActivity(intent)
    }


    private inner class BarcodeAnalyzer : ImageAnalysis.Analyzer {

        // Inspired from the library guide : https://developers.google.com/ml-kit/vision/barcode-scanning/android#kotlin
        @SuppressLint("UnsafeExperimentalUsageError")
        private fun scanBarcodes(imageProxy: ImageProxy) {

            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)


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
                                /*val bounds = barcode.boundingBox
                                val corners = barcode.cornerPoints
                                val rawValue = barcode.rawValue*/
                                // In the case of ISBN, both rawValue and displayValue are identical and simply contain the ISBN with no extra text.

                                when (barcode.valueType) {
                                    Barcode.TYPE_ISBN -> {
                                        val displayValue = barcode.displayValue
                                        if (displayValue != null) {
                                            Log.d("ScanBarcodeActivity", "barcode detected: ${displayValue}.")
                                            passISBN(displayValue)
                                        }
                                    }
                                }
                            }
                            // [END get_barcodes]
                            // [END_EXCLUDE]
                            imageProxy.close()
                        }
                        .addOnFailureListener {
                            // Task failed with an exception
                            it.printStackTrace()
                            imageProxy.close()
                        }
                // [END run_detector]
            }
        }


        override fun analyze(imageProxy: ImageProxy) {
            // Pass image to an ML Kit Vision API
            scanBarcodes(imageProxy)
        }

    }


}
