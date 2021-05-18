package com.github.polybooks.database

import android.content.Context
import com.github.polybooks.R
import com.github.polybooks.core.Book
import com.github.polybooks.core.ISBN
import com.github.polybooks.core.Interest
import com.github.polybooks.database.BookOrdering.*
import com.github.polybooks.utils.FieldWithName
import java.io.Serializable
import java.util.concurrent.CompletableFuture

/**
 * Provides the API for accessing books in a database.
 * */
abstract class BookDatabase {

    /**
     * Execute the given query and return the result as a future of a list of sales
     * */
    abstract fun execute(bookQuery : BookQuery): CompletableFuture<List<Book>>

    /**
     * Get data about a Book from the database given it's ISBN
     * */
    fun getBook(isbn : ISBN) : CompletableFuture<Book?>
            = execute(BookQuery(isbns = listOf(isbn))).thenApply { it.firstOrNull() }


}


/**
 * A Book query describes the filters applied on books in a query to the database.
 * Each field, except ordering is a filter. By default, a BookQuery doesn't filter any books
 * and orders books with the default ordering.
 * The filters isbn, title, and interests are mutually exclusive,
 * it is implementation defined what Book will be returned if several of those filters are provided.
 */
data class BookQuery(
    val isbns : Collection<ISBN>? = null,
    val title : String? = null,
    val interests : Collection<Interest>? = null,
    val ordering: BookOrdering = DEFAULT
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