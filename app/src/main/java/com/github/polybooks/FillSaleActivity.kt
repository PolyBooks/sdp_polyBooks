package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.github.polybooks.core.Book
import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.interfaces.BookDatabase
import com.google.gson.JsonParser
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.sql.Timestamp
import java.text.DateFormat
import java.util.*
import java.util.concurrent.CompletableFuture


/**
 * This activity receives the ISBN, either manually inputted from AddSale or deduced from the scanned barcode,
 * shows the retrieved data, but does not allow modification of it, only confirmation,
 * and offers some additional manual fields such as price, condition, etc.
 */
class FillSaleActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    // TODO I would imagine that in the future, the dbs are global constants, but while writing this class, I'll instantiate one locally
    private val salesDB = SaleDatabase()
    private val urlRegex = """.*openlibrary.org(.*)""".toRegex()
    private val url2filename = mapOf(
        Pair("/authors/OL7511250A.json", "OL7511250A.json"),
        Pair("/authors/OL7482089A.json", "OL7482089A.json"),
        Pair("/isbn/9782376863069.json", "9782376863069.json"),
        Pair("/isbn/2376863066.json", "9782376863069.json")
    )
    private val baseDir = "src/test/java/com/github/polybooks/core/databaseImpl"
    private val url2json = { url: String ->
        CompletableFuture.supplyAsync {
            val regexMatch =
                urlRegex.matchEntire(url) ?: throw FileNotFoundException("File Not Found : $url")
            val address = regexMatch.groups[1]?.value ?: throw Error("The regex is wrong")
            val filename =
                url2filename[address] ?: throw FileNotFoundException("File Not Found : $url")
            val file = File("$baseDir/$filename")
            val stream = file.inputStream()
            JsonParser.parseReader(InputStreamReader(stream))
        }
    }
    private val bookDB = OLBookDatabase(url2json)


    private lateinit var dateFromBookToSale: Timestamp
    private var bookConditionSelected: BookCondition? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_sale_fancy)



        val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.LONG)

        // Get the Intent that started this activity and extract the string
        val stringISBN = intent.getStringExtra(ISBN)
        // TODO for testing purpose, the ISBN will temporarily be displayed in the publisher field
        findViewById<TextView>(R.id.filled_publisher)         .apply { text = stringISBN }

        // Check if ISBN in our database: (could check ISBN validity before)
        if(stringISBN != null) {
            val book: CompletableFuture<Book> = bookDB.getBook(stringISBN)

            book.thenApply { book ->
                {
                    findViewById<TextView>(R.id.filled_authors)         .apply { text = book.authors?.get(
                        0
                    ) ?: "" } //TODO update that to either transform the list to string, and just store the string of authors
                    findViewById<TextView>(R.id.filled_title)           .apply { text = book.title }
                    findViewById<TextView>(R.id.filled_edition)         .apply { text = book.edition }
                    findViewById<TextView>(R.id.filled_language)        .apply { text = book.language }
                    findViewById<TextView>(R.id.filled_publisher)       .apply { text = book.publisher }
                    findViewById<TextView>(R.id.filled_publish_date)    .apply { text = dateFormat.format(Date(book.publishDate!!.time)) }
                    findViewById<TextView>(R.id.filled_format)          .apply { text = book.format }

                    dateFromBookToSale = book.publishDate!!
                }
            }
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


        // Disable confirm button until filled
        disableButton(findViewById<Button>(R.id.confirm_sale))

        findViewById<EditText>(R.id.filled_price).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (bookConditionSelected != null && findViewById<EditText>(R.id.filled_price).text.toString().isNotEmpty()) {
                    enableButton(findViewById<Button>(R.id.confirm_sale))
                } else {
                    disableButton(findViewById<Button>(R.id.confirm_sale))
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        /* Other spinner implementation
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Toast.makeText(this@MainActivity,
                    getString(R.string.selected_item) + " " +
                            "" + languages[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }*/

        /*
        // Old version of doing the future, can handle errors, but probably not compatible with Firebase, and I think the interface is handling the
        // case when our DB does not have the book already.

        val book: CompletableFuture<Book> = bookDB.getBook(stringISBN)
        book.handle { (book, err) ->
        {
            if (err == null) {
    // Yes: Retrieve from our database data about the book
                findViewById<TextView>(R.id.filled_authors)         .apply { text = book.authors }
                findViewById<TextView>(R.id.filled_title)           .apply { text = book.title }
                findViewById<TextView>(R.id.filled_edition)         .apply { text = book.edition }
                findViewById<TextView>(R.id.filled_language)        .apply { text = book.language }
                findViewById<TextView>(R.id.filled_publisher)       .apply { text = book.publisher }
                findViewById<TextView>(R.id.filled_publish_date)    .apply { text = book.publishDate }
                findViewById<TextView>(R.id.filled_format)          .apply { text = book.format }
            } else {
    // No: Use Google Books to convert stringISBN to JSON with relevant data, and also add to our database
                // If Google Books fails to find the ISBN, pop-out an error message about invalid ISBN and go back to AddSale page
            }
        }
         */
    }


    // TODO all the picture stuff.


    fun confirmSale(view: View) {
        // store Sale in our database
        val sale = Sale(
            findViewById<TextView>(R.id.filled_title).text.toString(),
            123, // TODO userID???
            findViewById<EditText>(R.id.filled_price).text.toString().toFloat(),
            // Should never be null as the button is not enabled otherwise
            bookConditionSelected!!,
            dateFromBookToSale,
            SaleState.ACTIVE
        )
        salesDB.addSale(sale)

        // TODO determine to which activity we land, but probably not MainActivity but rather a confirmation page
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun disableButton(button: Button) {
        button.isEnabled = false
        button.isClickable = false
        button.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        button.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.grey))
    }

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
            "New" -> bookConditionSelected = BookCondition.NEW
            "Good" -> bookConditionSelected = BookCondition.GOOD
            "Worn" -> bookConditionSelected = BookCondition.WORN
            else -> {
                Toast.makeText(
                    applicationContext,
                    "Error in selecting the Book Condition",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (findViewById<EditText>(R.id.filled_price).text.toString().isNotEmpty()) {
            enableButton(findViewById<Button>(R.id.confirm_sale))
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
        disableButton(findViewById<Button>(R.id.confirm_sale))
    }
}
