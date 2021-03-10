package com.github.polybooks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


/*
This activity open the camera (ask permission for it if not already given) and try to detect a barcode,
when it does it scans it, retrieve the ISBN and automatically moves to the FillSale activity passing the ISBN as intent.
 */
class ScanBarcode : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode)
    }
}