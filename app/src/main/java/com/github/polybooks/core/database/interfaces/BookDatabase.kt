package com.github.polybooks.core.database.interfaces

import android.annotation.SuppressLint
import com.github.polybooks.core.Book
import com.github.polybooks.core.Interest
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
     * Get all the books in the database
     * */
    fun listAllBooks() : CompletableFuture<List<Book>> = queryBooks().getAll()

    /**
     * Get data about a Book from the database given it's ISBN13
     * */
    @SuppressLint("NewApi")
    fun getBook(isbn13 : String) : CompletableFuture<Book>
            = queryBooks().searchByISBN13(isbn13).getAll().thenApply { it.first() }

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
     * Set this query to get the book associated with the given isbn13, if it exists.
     * (ignoring other filters)
     * */
    fun searchByISBN13(isbn13: String) : BookQuery

    /**
     * Set this query to order books with the given ordering.
     * (see {@link BookOrdering})
     * */
    fun withOrdering(ordering : BookOrdering) : BookQuery

}


/**
 * Defines an ordering for books. DEFAULT is implementation defined.
 * */
enum class BookOrdering {
    DEFAULT, TITLE_INC, TITLE_DEC,
}