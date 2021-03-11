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

    /**
     * Get all the books in the database
     * */
    fun listAllBooks() : CompletableFuture<List<Book>> = queryBooks().getAll()

    /**
     * Get all the sales in the database
     * */
    fun listAllSales() : CompletableFuture<List<Sale>> = querySales().getAll()

    /**
     * Get data about a Book from the database given it's ISBN13
     * */
    fun getBook(isbn13 : String) : CompletableFuture<Book>
        = TODO("It can be implemented from the previous functions")

    /**
     * A method for getting books by batches of at most N books. The batches are indexed by ordered pages.
     * @param numberOfBooks The maximum number of books per page
     * @param page The index of the page
     * @param ordering The ordering for the pages and books within the pages (see {@link BookOrdering})
     * */
    fun getNBooks(numberOfBooks : Int, page : Int, ordering : BookOrdering) : CompletableFuture<List<Book>>
        = queryBooks().withOrdering(ordering).getN(numberOfBooks, page)

    /**
     * A method for getting sales by batches of at most N sales. The batches are indexed by ordered pages.
     * @param numberOfSales The maximum number of sales per page
     * @param page The index of the page
     * @param ordering The ordering for the pages and sales within the pages (see {@link SaleOrdering})
     * */
    fun getNSales(numberOfSales : Int, page : Int, ordering : SaleOrdering) : CompletableFuture<List<Sale>>
            = querySales().withOrdering(ordering).getN(numberOfSales, page)

    //TODO need to add methods to modify the database, create entries.

}

/**
 * Queries are Object that allow to make queries to a database.
 * */
interface Query<T> {

    /**
     * Execute the query and return all the results in a Future.
     * */
    fun getAll() : CompletableFuture<List<T>>

    /**
     * Get the results in batches of at most n books.
     * */
    fun getN(n : Int, page : Int) : CompletableFuture<List<T>>

    /**
     * Get how many entries match this query
     * */
    fun getCount() : CompletableFuture<Int>

}

/**
 * A BookQuery is a builder for a query to the database that will yield Books.
 * Most methods return themselves for function chaining
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
 * A SaleQuery is a builder for a query to the database that will yield Sales.
 * Most methods return themselves for function chaining.
 * */
interface SaleQuery : Query<Sale> {

    /**
     * Set this query to only include sales that satisfy the given interests.
     * */
    fun onlyIncludeInterests(interests: Collection<Interest>) : SaleQuery

    /**
     * Set this query to only search for sales with book's title that are like the given one.
     * (ignoring other filters)
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
     * Set this query to only search for sales within the given price range.
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
    fun searchByISBN13(isbn13: String) : SaleQuery

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