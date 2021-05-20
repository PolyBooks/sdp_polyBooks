package com.github.polybooks.database

import android.content.Context
import com.github.polybooks.R
import com.github.polybooks.core.Book
import com.github.polybooks.core.ISBN
import com.github.polybooks.core.Interest
import com.github.polybooks.utils.FieldWithName
import java.io.Serializable
import java.util.concurrent.CompletableFuture

/**
 * Provides the API for accessing books in a database.
 * */
interface BookDatabase {

    /**
     * Creates a new query for Books. It originally matches all books.
     * */
    fun queryBooks() : BookQuery

    /**
     * Adds/Modifies a book in the database
     * */
    fun addBook(book: Book): CompletableFuture<Unit>

    /**
     * Get all the books in the database
     * */
    fun listAllBooks() : CompletableFuture<List<Book>> = queryBooks().getAll()

    /**
     * Get data about a Book from the database given it's ISBN
     * */
    fun getBook(isbn : ISBN) : CompletableFuture<Book?>
            = queryBooks().searchByISBN(setOf(isbn)).getAll().thenApply { it.firstOrNull() }

    /**
     * A method for getting books by batches of at most N books. The batches are indexed by ordered pages.
     * @param numberOfBooks The maximum number of books per page
     * @param page The index of the page
     * @param ordering The ordering for the pages and books within the pages (see {@link BookOrdering})
     * */
    fun getNBooks(numberOfBooks : Int, page : Int, ordering : BookOrdering) : CompletableFuture<List<Book>>
            = queryBooks().withOrdering(ordering).getN(numberOfBooks, page)
}

/**
 * A BookQuery is a builder for a query to the database that will yield Books.
 * Most methods return themselves for function chaining.
 * */
interface BookQuery : Query<Book> {

    /**
     * Set this query to only include books that satisfy the given interests.
     * */
    fun onlyIncludeInterests(interests: Collection<Interest>) : BookQuery

    /**
     * Set this query to only search for books with title that are like the given one.
     * (ignoring other filters)
     * */
    fun searchByTitle(title : String) : BookQuery

    /**
     * Set this query to get the books associated with the given ISBNs, if they exist.
     * (ignoring other filters)
     * */
    fun searchByISBN(isbns : Set<ISBN>) : BookQuery

    /**
     * Set this query to order books with the given ordering.
     * (see {@link BookOrdering})
     * */
    fun withOrdering(ordering : BookOrdering) : BookQuery

    /**
     * Get Settings of the BookQuery at current state
     **/
    fun getSettings() : BookSettings

    /**
     * Reset this query using the given settings
     */
    fun fromSettings(settings : BookSettings) : BookQuery

}

/**
 * The Settings contains the values for all the possible query parameters (ig. ordering, title).
 * In contrary to a Query object, it is independent to the state of the database and thus it
 * implements Serializable and can be passed as parameter between activities.
 *
 * To define a Query, a SaleSettings can be used along with fromSettings in substitution to
 * calling other methods (ig. searchByTitle)
 */
data class BookSettings(
    val ordering: BookOrdering,
    val isbns : List<ISBN>?,
    val title : String?,
    val interests : Set<Interest>?
) : Serializable

/**
 * Defines an ordering for books. DEFAULT is implementation defined.
 * */
enum class BookOrdering: FieldWithName {
    DEFAULT,
    TITLE_INC,
    TITLE_DEC;

    override fun fieldName(c: Context?): String {
        return c?.resources?.getStringArray(R.array.book_orderings_array)?.get(ordinal)
               ?: name
    }
}