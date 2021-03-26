package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.core.Book
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

        // Get the Intent that started this activity and extract the string
        val stringISBN = intent.getStringExtra(ISBN)
        // TODO for testing purpose, the ISBN will temporarily be displayed in the authors field
        findViewById<TextView>(R.id.filled_authors)         .apply { text = stringISBN }

        // Check if ISBN in our database: (could check ISBN validity before)

        // TODO Commenting out the whole chunk as it depends on the API which is not ready yet.
        /*
        val book: CompletableFuture<Book> = CompletableFuture().supplyAsync {

            bookDatabase.getBook(stringISBN)
        }.handle { (book, err) ->
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
        }

         */


    }


    fun confirmSale(view: View) {
        // TODO determine to which activity we land, but probably not MainActivity but rather a confirmation page
        // store Sale in our database
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
