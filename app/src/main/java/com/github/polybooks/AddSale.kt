package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


const val ISBN = "com.github.polybooks.ISBN"


/* This activity is the starting point to add a new sale. It gives the option between scanning the book barcode or manually inputting the ISBN.
* Both options reach the same end result, the FillSale activity, but through different steps.
*/
class AddSale : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sale)
    }

    fun scanBook(view: View) {
        val intent = Intent(this, ScanBarcode::class.java)
        startActivity(intent)
    }

    fun passISBN(view: View) {
        val editISBN = findViewById<EditText>(R.id.fillin_ISBN)
        val stringISBN = editISBN.text.toString()
        val intent = Intent(this, FillSale::class.java).apply {
            putExtra(ISBN, stringISBN)
        }
        startActivity(intent)
    }
}