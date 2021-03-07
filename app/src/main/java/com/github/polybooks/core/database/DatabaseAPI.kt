package com.github.polybooks.core.database

import com.github.polybooks.core.*
import java.util.concurrent.CompletableFuture

/**
 * Provides the API for a database. Allows to retrieve information in the database
 * about Books, Sales.
 * */
interface Database {

    /**
     * Creates a new query for Books. It originally matches all books.
     * */
    fun queryBooks() : BookQuery

    /**
     * Create a new query for Sales. It originally matches all sales.
     * */
    fun querySales() : SaleQuery

    //TODO we could or could not include those functions (and more) in the interface. Should we?

    fun listAllBooks() : CompletableFuture<List<Book>> = TODO("It can be implemented from the previous functions")

    fun listAllSales() : CompletableFuture<List<Sale>> = TODO("It can be implemented from the previous functions")

    /**
     * Get data about a Book from the database given it's ISBN13
     * */ //TODO define how to handle an invalid isbn13
    fun getBook(isbn13 : Long) : CompletableFuture<Book> = TODO("It can be implemented from the previous functions")


    /**
     * A method for getting books by batches of at most N books. The batches are indexed by ordered pages.
     * @param numberOfBooks The maximum number of books per page
     * @param page The index of the page
     * @param ordering The ordering for the pages and books within the pages (see {@link BookOrdering})
     * */
    fun getNBooks(numberOfBooks : Int, page : Int, ordering : BookOrdering) : CompletableFuture<List<Book>>
        = TODO("It can be implemented from the previous functions")

    //TODO need to add methods to modify the database, create entries.

}

/**
 * A BookQuery is a builder for a query to the database that will yield Books.
 * Most methods return themselves for function chaining
 * */
interface BookQuery {

    /**
     * Execute the query and return all the results in a Future.
     * */
    fun getAll() : CompletableFuture<List<Book>>

    /**
     * Get the results in batches of at most n books.
     * */
    fun getN(n : Int, page : Int) : CompletableFuture<List<Book>>

    /**
     * Get how many Books match this query
     * */
    fun getCount() : CompletableFuture<Int>

    /**
     * Set this query to not include books that satisfy the given interests.
     * */
    fun dontIncludeInterests(interests : Collection<Interest>) : BookQuery

    /**
     * Set this query to also include books that satisfy the given interests.
     * */
    fun includeInterests(interests: Collection<Interest>) : BookQuery

    /**
     * Set this query to only search for books with title that are like the given one.
     * */
    fun searchByTitle(title : String) : BookQuery

    /**
     * Set this query to get the book associated with the given isbn13, if it exists.
     * (ignoring other filters)
     * */
    fun searchByISBN13(isbn13: Long) : BookQuery

    /**
     * Set this query to order books with the given ordering.
     * (see {@link BookOrdering})
     * */
    fun withOrdering(ordering : BookOrdering) : BookQuery

}

/**
 * A SaleQuery is a builder for a query to the database that will yield Sales.
 * Most methods return themselves for function chaining.
 * */
interface SaleQuery {

    /**
     * Execute the query and return all the results in a Future.
     * */
    fun getAll() : CompletableFuture<List<Sale>>

    /**
     * Get the results in batches of at most n sales.
     * */
    fun getN(n : Int, page : Int) : CompletableFuture<List<Sale>>

    /**
     * Get how many Sales match this query
     * */
    fun getCount() : CompletableFuture<Int>

    /**
     * Set this query to not include sales that satisfy the given interests.
     * */
    fun dontIncludeInterests(interests : Collection<Interest>) : SaleQuery

    /**
     * Set this query to also include sales that satisfy the given interests.
     * */
    fun includeInterests(interests: Collection<Interest>) : SaleQuery

    /**
     * Set this query to only search for sales with book's title that are like the given one.
     * */
    fun searchByTitle(title : String) : SaleQuery

    /**
     *  Set this query to only search for sales in the given states.
     *  (see {@link SaleState})
     * */
    fun searchByState(state : Collection<SaleState>) : SaleQuery

    /**
     * Set this query to only search for sales of books in the given condition.
     * (see {@link BookCondition})
     * */
    fun searchByCondition(condition : Collection<BookCondition>) : SaleQuery

    /**
     * Set this query to search for sales within the given price range.
     * */
    fun searchByPrice(min : Float, max : Float) : SaleQuery

    /**
     * Set this query to order books with the given ordering.
     * (see {@link BookOrdering})
     * */
    fun withOrdering(ordering : SaleOrdering) : SaleQuery

    /**
     * Set this query to get sales of books associated with the given isbn13.
     * (ignoring other filters)
     * */
    fun searchByISBN13(isbn13: Long) : SaleQuery

}

/**
 * Defines an ordering for books. DEFAULT is implementation defined.
 * */
enum class BookOrdering {
    DEFAULT, TITLE_INC, TITLE_DEC,
}

/**
 * Defines an ordering for books. DEFAULT is implementation defined.
 * */
enum class SaleOrdering {
    DEFAULT, TITLE_INC, TITLE_DEC, PRICE_INC, PRICE_DEC, PUBLISH_DATE_INC, PUBLISH_DATE_DEC,
}