package com.github.polybooks

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/*
This activity open the camera (ask permission for it if not already given) and try to detect a barcode,
when it does it scans it, retrieve the ISBN and automatically moves to the FillSale activity passing the ISBN as intent.
 */
class ScanBarcode : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode)
    }


    fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA))
        } else {

        }
    }

    fun passISBN(view: View) {
        val stringISBN = // TODO
        val intent = Intent(this, FillSale::class.java).apply {
            putExtra(ISBN, stringISBN)
        }
        startActivity(intent)
    }
}