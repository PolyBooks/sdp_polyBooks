package com.github.polybooks.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.github.polybooks.R
import com.github.polybooks.core.*
import com.github.polybooks.database.*
import com.github.polybooks.utils.StringsManip
import com.github.polybooks.utils.url2json
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CompletableFuture

/**
 * This activity displays the detailed product information of a particular
 * registered sale given in .putExtra to the activity
 */
class SaleInformationActivity: AppCompatActivity() {
    companion object {
        const val EXTRA_SALE_INFORMATION: String = "EXTRA_SALE_INFORMATION"
    }

    private val fireStore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val olBookDB = OLBookDatabase { string -> url2json(string) }
    private val bookDB = FBBookDatabase(fireStore, olBookDB)
    private val saleDB = FBSaleDatabase(fireStore, bookDB)

    private fun onChangeRating(ratingBar: RatingBar, rating: Float, sale: Sale) {
        // get the same book back in case something changed in the mean time
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
                val updatedTotalStars: Double = rating + (book.totalStars ?: 0.0)
                val updatedNumberVotes: Int = 1 + (book.numberVotes ?: 0)

                ratingBar.setIsIndicator(true) // don't allow the user to modify the rating bar anymore

                bookDB.addBook( // modify the book entry
                    Book(
                        book.isbn, book.authors, book.title, book.edition,
                        book.language, book.publisher, book.publishDate,
                        book.format, updatedTotalStars, updatedNumberVotes
                    )
                )
            }
        }

        Toast.makeText(this, "You chose : $rating stars", Toast.LENGTH_SHORT).show()
    }

    private fun onClickContact() {
        Toast.makeText(this, "Contact the seller! (TODO)", Toast.LENGTH_SHORT).show()
    }

    private fun onClickRetract(sale: Sale) {
        // Only update the state of the book if the sale is active
        when (sale.state) {
            SaleState.RETRACTED -> {
                Toast.makeText(this, "The sale is already delisted", Toast.LENGTH_SHORT).show()
                // CompletableFuture.completedFuture(saleFresh) // should return a CompletionStage
            }
            SaleState.CONCLUDED -> {
                Toast.makeText(this, "The sale is already concluded", Toast.LENGTH_SHORT).show()
                // CompletableFuture.completedFuture(saleFresh) // should return a CompletionStage
            }
            else -> {
                val newSale = Sale(
                    sale.book, sale.seller, sale.price, sale.condition,
                    sale.date, SaleState.RETRACTED, sale.image
                )

                saleDB.getReferenceID(sale).addOnSuccessListener { ref ->
                    if (ref != null) {
                        Toast.makeText(this, "The sale was successfully delisted", Toast.LENGTH_SHORT).show()
                        saleDB.modifySale(ref.id, newSale)
                    } else {
                        throw DatabaseException(
                            "SaleInformationActivity tried to request information from a Sale() " +
                                    "which '${sale.book.isbn}' isbn doesn't exist in database anymore"
                        )
                    }
                }
            }
        }
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

        if (sale.book.totalStars != null && sale.book.numberVotes != null && sale.book.numberVotes != 0) {
            ratingBar.rating = (sale.book.totalStars / sale.book.numberVotes).toFloat()
        }

        ratingBar.setOnRatingBarChangeListener { bar, rating, _ -> onChangeRating(bar, rating, sale) }

        val extraButton: Button = findViewById(R.id.sale_information_button_extra)

        if (true || (sale.seller as LoggedUser).uid.toString() == firebaseAuth.currentUser?.uid) { // TODO modify (if current usr)
            extraButton.text = getString(R.string.retract_sale)
            extraButton.setOnClickListener { onClickRetract(sale) }
        } else {
            extraButton.text = getString(R.string.contact_seller)
            extraButton.setOnClickListener { onClickContact() }
        }

        findViewById<TextView>(R.id.sale_information_condition).text = sale.condition.name
        findViewById<TextView>(R.id.sale_information_price).text = sale.price.toString()
    }
}