package com.github.polybooks.core.database

import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import java.io.Serializable
import java.util.concurrent.CompletableFuture

/**
 * Provides the API for accessing sales in a Database.
 * */
interface SaleDatabase {

    /**
     * Create a new query for Sales. It originally matches all sales.
     * */
    fun querySales() : SaleQuery

    /**
     * Get all the sales in the database
     * */
    fun listAllSales() : CompletableFuture<List<Sale>> = querySales().getAll()

    /**
     * A method for getting sales by batches of at most N sales. The batches are indexed by ordered pages.
     * @param numberOfSales The maximum number of sales per page
     * @param page The index of the page
     * @param ordering The ordering for the pages and sales within the pages (see {@link SaleOrdering})
     * */
    fun getNSales(numberOfSales : Int, page : Int, ordering : SaleOrdering) : CompletableFuture<List<Sale>>
            = querySales().withOrdering(ordering).getN(numberOfSales, page)


}

/**
 * A SaleQuery is a builder for a query to the database that will yield Sales.
 * Most methods return themselves for function chaining.
 * */
interface SaleQuery : Query<Sale>,Serializable {

    /**
     * Set this query to only include sales that satisfy the given interests.
     * */
    fun onlyIncludeInterests(interests: Set<Interest>) : SaleQuery

    /**
     * Set this query to only search for sales with book's title that are like the given one.
     * (ignoring other filters)
     * */
    fun searchByTitle(title : String) : SaleQuery

    /**
     *  Set this query to only search for sales in the given states.
     *  (see {@link SaleState})
     * */
    fun searchByState(state : Set<SaleState>) : SaleQuery

    /**
     * Set this query to only search for sales of books in the given condition.
     * (see {@link BookCondition})
     * */
    fun searchByCondition(condition : Set<BookCondition>) : SaleQuery

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
enum class SaleOrdering {
    DEFAULT, TITLE_INC, TITLE_DEC, PRICE_INC, PRICE_DEC, PUBLISH_DATE_INC, PUBLISH_DATE_DEC,
}