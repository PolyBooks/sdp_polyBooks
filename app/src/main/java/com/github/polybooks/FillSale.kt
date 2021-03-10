package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.CompletableFuture

/*
This activity receives the ISBN, either manually inputted from AddSale or deduced from the scanned barcode,
shows the retrieved data, but do not allow modification of it, only confirmation,
and offers some additional manual fields such as price, condition, etc.
 */
class FillSale : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_sale)

        // Get the Intent that started this activity and extract the string
        val stringISBN = intent.getStringExtra(ISBN)

        // Check if ISBN in our database:
        // TODO
        val book: CompletableFuture<Book> = CompletableFuture().supplyAsync {
            getBook(stringISBN)
        }.handle { (book, err) ->
            {
                if (err == null) {
        // Yes: Retrieve from our database data about the book
                    findViewById<TextView>(R.id.filled_authors)         .apply { text = decodedJSON }
                    findViewById<TextView>(R.id.filled_title)           .apply { text = decodedJSON }
                    findViewById<TextView>(R.id.filled_edition)         .apply { text = decodedJSON }
                    findViewById<TextView>(R.id.filled_language)        .apply { text = decodedJSON }
                    findViewById<TextView>(R.id.filled_publisher)       .apply { text = decodedJSON }
                    findViewById<TextView>(R.id.filled_publish_date)    .apply { text = decodedJSON }
                    findViewById<TextView>(R.id.filled_format)          .apply { text = decodedJSON }
                } else {
        // No: Use Google Books to convert stringISBN to JSON with relevant data, and also add to our database
                    // If Google Books fails to find the ISBN, pop-out an error message about invalid ISBN and go back to AddSale page

                }
            }
        }


    }


    fun confirmSale(view: View) {
        // TODO determine to which activity we land
        // store Sale in our database
        val intent = Intent(this, ???::class.java)
        startActivity(intent)
    }
}