package com.github.polybooks.activities

import android.os.Bundle
import android.util.Log
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.R
import com.github.polybooks.core.ISBN
import com.github.polybooks.core.Sale
import com.github.polybooks.database.*
import com.github.polybooks.utils.StringsManip
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.CompletableFuture

/**
 * This activity displays the detailed product information of a particular
 * registered sale given in .putExtra to the activity
 */
class SaleInformationActivity: AppCompatActivity() {

    lateinit var bookDB: BookDatabase

    val bookRatingRef = FirebaseProvider.getFirestore().collection("bookRating")
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        const val EXTRA_SALE_INFORMATION: String = "EXTRA_SALE_INFORMATION"
    }


    inner class BookRating(ratingMap: Any) {
        var rating = (ratingMap as HashMap<String, Any>)["rating"] as Map<String, List<String>>
        var totalVotes = (ratingMap as HashMap<String, Any>)["totalVotes"] as Long

        fun userAlreadyVoted(): String? {
            val uid = firebaseAuth.currentUser?.uid ?: return null

            for ((key, value) in rating.entries) {
                if (value.contains(uid)) return key
            }

            return null
        }

        fun toDocument(): HashMap<String, Any> {
            return hashMapOf(
                "rating" to rating,
                "totalVotes" to totalVotes,
            )
        }

        fun uploadToFirebase(isbn: ISBN): CompletableFuture<Unit> {
            val future = CompletableFuture<Unit>()
            bookRatingRef.document(isbn).set(this.toDocument())
                .addOnSuccessListener {
                    future.complete(Unit)
                }.addOnFailureListener {
                    future.completeExceptionally(it)
                }

            return future
        }
    }

    private fun ratingDocumentToMap(ratingMap: Any): MutableMap<String, MutableList<String>> {
        return ratingMap as MutableMap<String, MutableList<String>>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_information)

        bookDB = Database.bookDatabase(this)

        val sale = (intent.getSerializableExtra(EXTRA_SALE_INFORMATION) as Sale)

        findViewById<TextView>(R.id.sale_information_title).text = sale.book.title
        findViewById<TextView>(R.id.sale_information_edition).text = sale.book.edition
        findViewById<TextView>(R.id.sale_information_authors).text = StringsManip.listAuthorsToString(sale.book.authors)
        // val countryFlag: TextView = findViewById(R.id.countryFlag)
        findViewById<TextView>(R.id.sale_information_book_format).text = sale.book.format
        // val bookImage = findViewById(R.id.proof_picture)

        val ratingBar: RatingBar = findViewById(R.id.sale_information_rating)
        ratingBar.rating = 0f

        bookRatingRef.document(sale.book.isbn)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    val bookRating = BookRating(document.data!!)

                    val hasAlreadyVoted = bookRating.userAlreadyVoted()
                    if (hasAlreadyVoted != null) {
                        ratingBar.rating = hasAlreadyVoted.toFloat()
                        ratingBar.setIsIndicator(true)
                    }
                }
            }

        ratingBar.setOnRatingBarChangeListener { bar, rating, _ ->

            bookRatingRef.document(sale.book.isbn)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.data != null) {
                        // rating for the book exists
                        val bookRating = BookRating(document.data!!)

                        if (bookRating.userAlreadyVoted() != null) {
                            // User has already voted
                            Toast.makeText(this, "You have already voted", Toast.LENGTH_SHORT).show()
                        } else {
                            // User never voted on this book
                            val ratingKey: String = if (rating.toString().endsWith(".0")) rating.toString().take(1) else rating.toString()

                            val newEntry: List<String> = bookRating.rating[ratingKey]!! + firebaseAuth.currentUser?.uid!!

                            bookRating.rating = bookRating.rating.plus(Pair(ratingKey, newEntry))
                            bookRating.totalVotes = 1 + bookRating.totalVotes

                            bookRating.uploadToFirebase(sale.book.isbn).thenApply {
                                Toast.makeText(this, "You chose : $rating stars", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // no rating yet
                        val ratingKey: String = if (rating.toString().endsWith(".0")) rating.toString().take(1) else rating.toString()

                        val emptyRating: HashMap<String, List<String>> = hashMapOf(
                            "0" to emptyList(), "0.5" to emptyList(),
                            "1" to emptyList(), "1.5" to emptyList(),
                            "2" to emptyList(), "2.5" to emptyList(),
                            "3" to emptyList(), "3.5" to emptyList(),
                            "4" to emptyList(), "4.5" to emptyList(),
                            "5" to emptyList()
                        )

                        val bookRating = BookRating(hashMapOf("rating" to emptyRating.plus(Pair(ratingKey, listOf(firebaseAuth.currentUser?.uid))), "totalVotes" to 1L))
                        bookRating.uploadToFirebase(sale.book.isbn).thenApply {
                            Toast.makeText(this, "You chose : $rating stars", Toast.LENGTH_SHORT).show()
                        }
                    }

                    bar.setIsIndicator(true)
                }
                .addOnFailureListener { exception ->
                    Log.d(
                        "SaleInforamtionActivity",
                        "SaleInformationActivity tried to request information from a Book() " +
                                "which '${sale.book.isbn}' isbn doesn't exist in database anymore",
                        exception
                    )
                }
        }

        findViewById<TextView>(R.id.sale_information_condition).text = sale.condition.name
        findViewById<TextView>(R.id.sale_information_price).text = sale.price.toString()
    }
}