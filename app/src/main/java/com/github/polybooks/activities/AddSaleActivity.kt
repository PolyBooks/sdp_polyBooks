package com.github.polybooks.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.utils.StringsManip.isbnHasCorrectFormat
import com.github.polybooks.utils.UIManip.disableButton
import com.github.polybooks.utils.UIManip.enableButton
import com.github.polybooks.utils.setupNavbar


const val EXTRA_ISBN = "com.github.polybooks.activities.ISBN"
const val EXTRA_PICTURE_FILE = "com.github.polybooks.activities.picture_file"
const val EXTRA_SALE_PRICE = "com.github.polybooks.activities.sale_price"


/**
 * This activity is the starting point to add a new sale.
 * It gives the option between scanning the book barcode or manually inputting the ISBN.
 * Both options reach the same end result, the FillSale activity, but through different steps.
 */
class AddSaleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sale)
        setupNavbar(findViewById(R.id.bottom_navigation), this)


        // Listener on fill-in ISBN to trigger pass ISBN button
        findViewById<EditText>(R.id.fill_in_ISBN).addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && isbnHasCorrectFormat(s.toString())) {
                    enableButton(findViewById(R.id.pass_isbn_button), applicationContext)
                } else {
                    disableButton(findViewById(R.id.pass_isbn_button), applicationContext)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        disableButton(findViewById(R.id.pass_isbn_button), applicationContext)
    }

    fun scanBook(view: View) {
        val intent = Intent(this, ScanBarcodeActivity::class.java)
        startActivity(intent)
    }

    fun passISBN(view: View) {
        val editISBN = findViewById<EditText>(R.id.fill_in_ISBN)
        val stringISBN = editISBN.text.toString()
        val intent = Intent(this, FillSaleActivity::class.java).apply {
            val extras = Bundle()
            extras.putString(EXTRA_ISBN, stringISBN)
            extras.putString(EXTRA_PICTURE_FILE, null)
            extras.putString(EXTRA_SALE_PRICE, null)
            putExtras(extras)
        }
        startActivity(intent)
    }
}