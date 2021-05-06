package com.github.polybooks

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.app.ActivityCompat
import com.github.polybooks.utils.CameraManip.REQUEST_CODE_PERMISSIONS
import com.github.polybooks.utils.CameraManip.REQUIRED_PERMISSIONS
import com.github.polybooks.utils.CameraManip.allPermissionsGranted
import com.github.polybooks.utils.CameraManip.startCamera
import com.github.polybooks.utils.StringsManip.isbnHasCorrectFormat
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.activity_scan_barcode.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * This activity opens the camera (ask permission for it if not already given) and tries to detect a barcode.
 * When it does it scans it, retrieve the ISBN and automatically moves to the FillSale activity passing the ISBN as intent.
 */
class ScanBarcodeActivity : AppCompatActivity() {

    // TODO next step: Maybe refactor the BarcodeAnalyzer inner class, but it causes issues with access rights...

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode)

        // Request camera permissions
        if (allPermissionsGranted(baseContext)) {
            startScanBarcodeCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startScanBarcodeCamera() {
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, BarcodeAnalyzer())
            }
        startCamera(this, viewFinder, imageAnalyzer)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted(baseContext)) {
                startScanBarcodeCamera()
            } else {
                Toast.makeText(this,
                    "You must grant camera permissions to take a picture.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    private fun passISBN(stringISBN: String) {
        val intent = Intent(this, FillSaleActivity::class.java).apply {
            val extras = Bundle()
            extras.putString(EXTRA_ISBN, stringISBN)
            extras.putString(EXTRA_PICTURE_FILE, null)
            putExtras(extras)
        }
        startActivity(intent)
    }


    private inner class BarcodeAnalyzer : ImageAnalysis.Analyzer {

        override fun analyze(imageProxy: ImageProxy) {
            // Pass image to an ML Kit Vision API
            scanBarcodes(imageProxy)
        }

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
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        // Task completed successfully
                        // [START get_barcodes]
                        for (barcode in barcodes) {
                            // In the case of ISBN, both rawValue and displayValue are identical and simply contain the ISBN with no extra text.
                            when (barcode.valueType) {
                                Barcode.TYPE_ISBN -> {
                                    val displayValue = barcode.displayValue
                                    // TODO could potentially split the NotCorrectFormat case to display a toast?
                                    if (!displayValue.isNullOrEmpty() && isbnHasCorrectFormat(displayValue)) {
                                        // Needs to shutdown and close here to avoid starting the next activity several times!
                                        cameraExecutor.shutdown()
                                        scanner.close()
                                        passISBN(displayValue)
                                    }
                                }
                            }
                        }
                        // [END get_barcodes]
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

    }


}
