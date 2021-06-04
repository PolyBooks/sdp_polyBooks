package com.github.polybooks.activities

import android.os.Bundle
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.R
import com.github.polybooks.core.Book
import com.github.polybooks.core.Sale
import com.github.polybooks.database.*
import com.github.polybooks.utils.StringsManip
import com.github.polybooks.utils.url2json
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * This activity displays the detailed product information of a particular
 * registered sale given in .putExtra to the activity
 */
class SaleInformationActivity: AppCompatActivity() {
    companion object {
        const val EXTRA_SALE_INFORMATION: String = "EXTRA_SALE_INFORMATION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_information)

        val sale = (intent.getSerializableExtra(EXTRA_SALE_INFORMATION) as Sale)

        findViewById<TextView>(R.id.sale_information_title).text = sale.book.title
        findViewById<TextView>(R.id.sale_information_edition).text = sale.book.edition
        findViewById<TextView>(R.id.sale_information_authors).text = StringsManip.listAuthorsToString(sale.book.authors)
        // val countryFlag: TextView = findViewById(R.id.countryFlag)
        findViewById<TextView>(R.id.sale_information_book_format).text = sale.book.format
        // val bookImage = findViewById(R.id.proof_picture)

        val ratingBar: RatingBar = findViewById(R.id.sale_information_rating)
        ratingBar.rating = 0f
/*
        if (sale.book.totalStars != null && sale.book.numberVotes != null && sale.book.numberVotes != 0) {
            ratingBar.rating = (sale.book.totalStars / sale.book.numberVotes).toFloat()
        } */
        ratingBar.setOnRatingBarChangeListener { bar, rating, _ ->
            val bookDB = Database.bookDatabase

            var query: BookQuery = bookDB.queryBooks()
            query = query.searchByISBN(setOf(sale.book.isbn))
            query.getAll().thenCompose { books ->
                if (books == null || books.isEmpty()) {
                    throw DatabaseException(
                        "SaleInformationActivity tried to request information from a Book() " +
                        "which '${sale.book.isbn}' isbn doesn't exist in database anymore"
                    )
                } else if (books.size > 1) {
                    throw DatabaseException(
                        "SaleInformationActivity tried to request information from a Book() " +
                        "which isbn '${sale.book.isbn}' is used ${books.size} times in the database"
                    )
                } else {
                    val book: Book = books[0]
                    /*
                    val updatedTotalStars: Double = rating + (book.totalStars ?: 0.0)
                    val updatedNumberVotes: Int = 1 + (book.numberVotes ?: 0)

                    bar.setIsIndicator(true)

                    bookDB.addBook(book.copy(totalStars = updatedTotalStars, numberVotes = updatedNumberVotes)) */
                    bookDB.addBook(book)
                }
            }

            Toast.makeText(this, "You chose : $rating stars", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.sale_information_condition).text = sale.condition.name
        findViewById<TextView>(R.id.sale_information_price).text = sale.price.toString()
    }
}