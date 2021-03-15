package com.github.polybooks

import android.Manifest
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


/*
This activity open the camera (ask permission for it if not already given) and try to detect a barcode,
when it does it scans it, retrieve the ISBN and automatically moves to the FillSale activity passing the ISBN as intent.
 */
class ScanBarcode : AppCompatActivity() {

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

                        val valueType = barcode.valueType
                        // See API reference for complete list of supported types
                        when (valueType) {
                            Barcode.TYPE_ISBN -> {
                                val displayValue = barcode.displayValue
                            }
                        }
                    }
                    // [END get_barcodes]
                    // [END_EXCLUDE]
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }
        // [END run_detector]
    }

    private val myCameraPermission = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode)
    }


    fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), myCameraPermission)
        } else {

        }
    }

    fun passISBN(view: View) {
        val stringISBN = "9876543210123"// TODO
        val intent = Intent(this, FillSale::class.java).apply {
            putExtra(ISBN, stringISBN)
        }
        startActivity(intent)
    }
}