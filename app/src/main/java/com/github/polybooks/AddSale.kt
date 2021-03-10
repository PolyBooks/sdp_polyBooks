package com.github.polybooks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/* This activity is the starting point to add a new sale. It gives the option between scanning the book barcode or manually inputting the ISBN.
* Both options reach the same end result, the FillSale activity, but through different steps.
*/
class AddSale : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sale)
    }
}