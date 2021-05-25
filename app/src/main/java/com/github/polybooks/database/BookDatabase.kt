package com.github.polybooks.database

import android.content.Context
import com.github.polybooks.R
import com.github.polybooks.core.Book
import com.github.polybooks.core.ISBN
import com.github.polybooks.core.Interest
import com.github.polybooks.database.BookOrdering.DEFAULT
import com.github.polybooks.utils.FieldWithName
import java.io.Serializable
import java.util.concurrent.CompletableFuture

/**
 * A BookProvider is an object that can provide books given their ISBN
 * */
interface BookProvider {

    /**
     * Get the books with the given ISBNs
     * */
    fun getBooks(
        isbns: Collection<ISBN>,
        ordering: BookOrdering = DEFAULT
    ): CompletableFuture<List<Book>>

    /**
     *  Get the book with the given ISBN
     **/
    fun getBook(isbn: ISBN): CompletableFuture<Book?> =
        getBooks(listOf(isbn)).thenApply { it.firstOrNull() }

    /**
     * Adds a book to the BookProvider.
     * @return a unit future for error handling
     * */
    fun addBook(book: Book): CompletableFuture<Unit>

}

/**
 * Provides the API for accessing books in a database.
 * */
interface BookDatabase: BookProvider {

    /**
     * Searches for book whose title resembles the given one
     * */
    fun searchByTitle(
        title: String,
        ordering: BookOrdering = DEFAULT
    ): CompletableFuture<List<Book>>

    /**
     * Searches for books that are interesting for the given interests
     * */
    fun searchByInterests(
        interests: Collection<Interest>,
        ordering: BookOrdering = DEFAULT
    ): CompletableFuture<List<Book>>

    /**
     * Lists all the books stored in the database
     * */
    fun listAllBooks(ordering: BookOrdering = DEFAULT): CompletableFuture<List<Book>>

    /**
     * Execute a query
     * */
    fun execute(query: BookQuery): CompletableFuture<List<Book>> {
        return when (query) {
            is ISBNBookQuery -> getBooks(query.isbns, query.ordering)
            is TitleBookQuery -> searchByTitle(query.title, query.ordering)
            is InterestsBookQuery -> searchByInterests(query.interests, query.ordering)
            is AllBooksQuery -> listAllBooks(query.ordering)
        }
    }

}

/**
 * A serializable object to represent a query to a BookDatabase
 */
sealed class BookQuery(private val ordering: BookOrdering): Serializable {

    /**
     * Searches for book whose title resembles the given one
     * */
    fun searchByTitle(title: String): TitleBookQuery = TitleBookQuery(title, this.ordering)

    /**
     * Searches for books that are interesting for the given interests
     * */
    fun searchByInterests(interests: Collection<Interest>): InterestsBookQuery =
        InterestsBookQuery(interests, this.ordering)

    /**
     * Get the books with the given ISBNs
     * */
    fun getBooks(isbns: Collection<ISBN>): ISBNBookQuery =
        ISBNBookQuery(isbns, this.ordering)

    /**
     * Define the orders the result of the query
     * */
    fun orderBy(ordering: BookOrdering): BookQuery = when (this) {
        is AllBooksQuery -> AllBooksQuery(ordering)
        is ISBNBookQuery -> ISBNBookQuery(this.isbns, ordering)
        is InterestsBookQuery -> InterestsBookQuery(this.interests, ordering)
        is TitleBookQuery -> TitleBookQuery(this.title, ordering)
    }

}

data class ISBNBookQuery(val isbns: Collection<ISBN>, val ordering: BookOrdering = DEFAULT):
    BookQuery(ordering)

data class TitleBookQuery(val title: String, val ordering: BookOrdering = DEFAULT):
    BookQuery(ordering)

data class InterestsBookQuery(
    val interests: Collection<Interest>,
    val ordering: BookOrdering = DEFAULT
): BookQuery(ordering)

data class AllBooksQuery(val ordering: BookOrdering = DEFAULT): BookQuery(ordering)

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