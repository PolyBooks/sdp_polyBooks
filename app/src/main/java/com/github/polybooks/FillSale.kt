package com.github.polybooks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/*
This activity receives the ISBN, either manually inputted from AddSale or deduced from the scanned barcode,
shows the retrieved data, but do not allow modification of it, only confirmation,
and offers some additional manual fields such as price, condition, etc.
 */
class FillSale : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_sale)
    }
}