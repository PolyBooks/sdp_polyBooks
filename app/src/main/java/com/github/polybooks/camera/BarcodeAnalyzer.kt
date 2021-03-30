package com.github.polybooks.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat.startActivity
import com.github.polybooks.FillSaleActivity
import com.github.polybooks.ISBN
import com.github.polybooks.ScanBarcodeActivity
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService

class BarcodeAnalyzer(
    private val AppCompatActivity: ScanBarcodeActivity,
    private val cameraExecutor: ExecutorService,
    private val context: Context
) : ImageAnalysis.Analyzer {


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
                    // [START_EXCLUDE]
                    // [START get_barcodes]
                    for (barcode in barcodes) {
                        // In the case of ISBN, both rawValue and displayValue are identical and simply contain the ISBN with no extra text.
                        when (barcode.valueType) {
                            Barcode.TYPE_ISBN -> {
                                val displayValue = barcode.displayValue
                                if (displayValue != null) {
                                    // Needs to shutdown and close here to avoid starting the next activity several times!
                                    cameraExecutor.shutdown()
                                    scanner.close()
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

    private fun passISBN(stringISBN: String) {
        val intent = Intent(context, FillSaleActivity::class.java).apply {
            putExtra(ISBN, stringISBN)
        }
        context.startActivity(intent)
    }

}