package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.core.Book
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.interfaces.BookDatabase
import java.text.DateFormat
import java.util.concurrent.CompletableFuture

/**
 * This activity receives the ISBN, either manually inputted from AddSale or deduced from the scanned barcode,
 * shows the retrieved data, but does not allow modification of it, only confirmation,
 * and offers some additional manual fields such as price, condition, etc.
 */
class FillSaleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_sale_fancy)

        // TODO I would imagine that in the future, the dbs are global constants, but while writing this class, I'll instantiate one locally
        val salesDB = SaleDatabase()
        val bookDB = BookDatabase()

        val DFormat: DateFormat = DateFormat.getDateInstance(DateFormat.LONG)

        // Get the Intent that started this activity and extract the string
        val stringISBN = intent.getStringExtra(ISBN)
        // TODO for testing purpose, the ISBN will temporarily be displayed in the publisher field
        findViewById<TextView>(R.id.filled_publisher)         .apply { text = stringISBN }

        // Check if ISBN in our database: (could check ISBN validity before)

        val book: CompletableFuture<Book> = bookDB.getBook(stringISBN)

        book.thenApply { book ->
            {
                findViewById<TextView>(R.id.filled_authors)         .apply { text = book.authors?.get(0) ?: "" } //TODO update that to either transform the list to string, and just store the string of authors
                findViewById<TextView>(R.id.filled_title)           .apply { text = book.title }
                findViewById<TextView>(R.id.filled_edition)         .apply { text = book.edition }
                findViewById<TextView>(R.id.filled_language)        .apply { text = book.language }
                findViewById<TextView>(R.id.filled_publisher)       .apply { text = book.publisher }
                findViewById<TextView>(R.id.filled_publish_date)    .apply { text = DFormat.format(book.publishDate?.toDate()) }
                findViewById<TextView>(R.id.filled_format)          .apply { text = book.format }
            }
        }


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
        // TODO determine to which activity we land, but probably not MainActivity but rather a confirmation page
        // store Sale in our database
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
