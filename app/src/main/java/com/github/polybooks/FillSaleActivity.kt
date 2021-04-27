package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.polybooks.core.*
import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.utils.StringsManip.isbnHasCorrectFormat
import com.github.polybooks.utils.StringsManip.listAuthorsToString
import com.github.polybooks.utils.url2json
import com.google.firebase.Timestamp
import java.lang.Exception
import java.text.DateFormat
import java.util.concurrent.CompletableFuture


/**
 * This activity receives the ISBN, either manually inputted from AddSale or deduced from the scanned barcode,
 * shows the retrieved data, but does not allow modification of it, only confirmation,
 * and offers some additional manual fields such as price, condition, etc.
 */
class FillSaleActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    // TODO I would imagine that in the future, the dbs are global constants, but while writing this class, I'll instantiate one locally
    private val salesDB = SaleDatabase()
    private val bookDB = OLBookDatabase { string -> url2json(string) }

    private lateinit var bookFuture: CompletableFuture<Book?>
    private var bookConditionSelected: BookCondition? = null
    private val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.LONG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_sale_fancy)

        // Get the Intent that started this activity and extract the string
        val stringISBN = intent.getStringExtra(ISBN)

        // Retrieve book data and display it if possible, else redirect with error toast
        if(!stringISBN.isNullOrEmpty() && isbnHasCorrectFormat(stringISBN)) {
            try {
                bookFuture = bookDB.getBook(stringISBN)
                val book = bookFuture.get()
                if (book != null) {
                    fillBookData(book)
                } else {
                    redirectToAddSaleWithToast("Book matching the ISBN could not be found")
                }
            } catch (e: Exception) {
                redirectToAddSaleWithToast("An error occurred, please try again")
            }
        } else {
            redirectToAddSaleWithToast("Please provide an ISBN")
        }


        // Drop-down menu for condition
        val spinner: Spinner = findViewById(R.id.filled_condition)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.condition_options_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this

        // Fill-in text for book price
        findViewById<EditText>(R.id.filled_price).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                handleButton()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        // Disable confirm button until filled
        disableButton(findViewById(R.id.confirm_sale_button))
    }

    /**
     * Fill the UI with the data about a book
     */
    private fun fillBookData(book: Book) {
        findViewById<TextView>(R.id.filled_authors).apply {
            text = listAuthorsToString(book.authors)
        }
        findViewById<TextView>(R.id.filled_title).apply { text = book.title }
        findViewById<TextView>(R.id.filled_edition).apply { text = book.edition ?: "" }
        // TODO language is ideally not converted to a string but to a flag
        // findViewById<TextView>(R.id.filled_language).apply { text = book.language ?: "" }
        findViewById<TextView>(R.id.filled_publisher).apply { text = book.publisher ?: "" }
        findViewById<TextView>(R.id.filled_publish_date).apply {
            text = dateFormat.format(book.publishDate!!.toDate()) ?: ""
        }
        // TODO whole lines could be removed from UI when argument is null instead of placeholding with default value
        findViewById<TextView>(R.id.filled_format).apply { text = book.format ?: "" }
    }

    /**
     * Called when an issue happen to convert the issue to a nicer UX flow than crashing
     * Showing a message about the error and redirecting to a previous activity
     */
    private fun redirectToAddSaleWithToast(message: String) {
        Log.w("BookFuture", message)
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(this, AddSaleActivity::class.java)
        startActivity(intent)
    }


    // TODO all the picture stuff.


    /**
     * Create a sale in the database with the relevant data from the activity
     * (i.e. book, condition, user, price, date)
     */
    fun confirmSale(view: View) {
        // store Sale in our database
        val sale = Sale(
            bookFuture.get()!!, // TODO maybe ensure above that the button is disabled if book is null
            LocalUser, // TODO user
            findViewById<EditText>(R.id.filled_price).text.toString().toFloat(),
            // Should never be null as the button is not enabled otherwise
            bookConditionSelected!!,
            Timestamp.now(),
            SaleState.ACTIVE,
            null
        )

        salesDB.addSale(sale)

        // TODO determine to which activity we land, but probably not MainActivity but rather a confirmation page
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    /**
     * disableButton allows a button to not be clickable and change its appearance to grey
     * To be called whenever fields are missing
     */
    fun disableButton(button: Button) {
        button.isEnabled = false
        button.isClickable = false
        button.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        button.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.grey))
    }

    /**
     * enableButton allows a button to be clickable and change its appearance to active
     * To be called once all the fields have been set
     */
    fun enableButton(button: Button) {
        button.isEnabled = true
        button.isClickable = true
        button.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        button.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.blue_green_400
            )
        )
    }



    /**
     *
     * Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.
     *
     * Implementers can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.getItemAtPosition(position).toString()) {
            "Select" -> bookConditionSelected = null
            "New" -> bookConditionSelected = BookCondition.NEW
            "Good" -> bookConditionSelected = BookCondition.GOOD
            "Worn" -> bookConditionSelected = BookCondition.WORN
            else -> {
                Toast.makeText(
                    applicationContext,
                    "Error in selecting the Book Condition",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        handleButton()
    }

    private fun handleButton() {
        if (bookConditionSelected != null && findViewById<EditText>(R.id.filled_price).text.toString().isNotEmpty()) {
            enableButton(findViewById(R.id.confirm_sale_button))
        } else {
            disableButton(findViewById(R.id.confirm_sale_button))
        }
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    override fun onNothingSelected(parent: AdapterView<*>?) {
        disableButton(findViewById(R.id.confirm_sale_button))
    }
}
