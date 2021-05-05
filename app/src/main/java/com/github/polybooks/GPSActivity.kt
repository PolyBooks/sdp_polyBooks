package com.github.polybooks

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.utils.setupNavbar

class GPSActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gps)

        setupNavbar(findViewById(R.id.bottom_navigation), this)
    }
}