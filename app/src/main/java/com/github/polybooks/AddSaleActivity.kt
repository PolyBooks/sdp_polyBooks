package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


const val ISBN = "com.github.polybooks.ISBN"


/**
 * This activity is the starting point to add a new sale.
 * It gives the option between scanning the book barcode or manually inputting the ISBN.
 * Both options reach the same end result, the FillSale activity, but through different steps.
 */
class AddSaleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sale)
        setupNavbar(findViewById(R.id.bottom_navigation))
    }

    private fun setupNavbar(navBar: BottomNavigationView){
        navBar.selectedItemId = R.id.default_selected
        navBar.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.books ->{
                    startActivity(Intent(this, FilteringBooksActivity::class.java))
                    true
                }
                R.id.sales ->{
                    startActivity(Intent(this, FilteringSalesActivity::class.java))
                    true
                }
                R.id.user_profile ->{
                    // TODO: user sales
                    false
                }
                else -> true
            }
        }
    }

    fun scanBook(view: View) {
        val intent = Intent(this, ScanBarcodeActivity::class.java)
        startActivity(intent)
    }

    fun passISBN(view: View) {
        val editISBN = findViewById<EditText>(R.id.filling_ISBN)
        val stringISBN = editISBN.text.toString()
        val intent = Intent(this, FillSaleActivity::class.java).apply {
            putExtra(ISBN, stringISBN)
        }
        startActivity(intent)
    }
}