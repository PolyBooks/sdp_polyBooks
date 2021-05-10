package com.github.polybooks.core.database.interfaces

import android.content.Context
import com.github.polybooks.R
import com.github.polybooks.core.*
import com.github.polybooks.core.database.interfaces.SaleOrdering.*
import com.github.polybooks.utils.FieldWithName
import java.io.Serializable
import java.util.concurrent.CompletableFuture

/**
 * Provides the API for accessing sales in a Database.
 * */
abstract class SaleDatabase {

    /**
     * Execute the given query and return the result as a future of a list of sales
     * */
    abstract fun execute(query : SaleQuery): CompletableFuture<List<Sale>>

    /**
     * Add the sale defined by the given parameters to the database
     * @param book the isbn of the book being sold
     * @param seller the user selling the book (can't be the local user)
     * @param price the price of the sale
     * @param condition the condition of the book
     * @param state the state of the sale
     * @param image the image describing the book being sold
     * @return a future containing the sale created and added to the database
     */
    abstract fun addSale(book : ISBN,
                seller : User,
                price : Float,
                condition : BookCondition,
                state : SaleState,
                image : Image?) : CompletableFuture<Sale>

    /**
     * Add a sale defined by the attributes of the given sale.
     * @param sale the sale defining the new sale.
     * @return a future containing the sale created and added to the database.
     * */
    fun addSale(sale: Sale) : CompletableFuture<Sale> =
        addSale(sale.book.isbn, sale.seller, sale.price, sale.condition, sale.state, sale.image)

    /**
     * Delete the given sale to the database
     * @param sale The sale to delete
     * @return A completable future of whether a sale was deleted (i.e. if it existed)
     */
    abstract fun deleteSale(sale: Sale) : CompletableFuture<Boolean>

}

/**
 * A Sale query describes the filters applied on sales in a query to the database.
 * Each field, except ordering is a filter. By default, a SaleQuery doesn't filter any books
 * and orders sales with the default ordering.
 * The filters isbn, title, and interests are mutually exclusive,
 * it is implementation defined what Sale will be returned if several of those filters are provided.
 */
data class SaleQuery (
    val isbn: String? = null,
    val title: String? = null,
    val interests: Set<Interest>? = null,
    val states: Set<SaleState>? = null,
    val conditions: Set<BookCondition>? = null,
    val minPrice: Float? = null,
    val maxPrice: Float? = null,
    val ordering: SaleOrdering = DEFAULT
) : Serializable


/**
 * Defines an ordering for books. DEFAULT is implementation defined.
 * */
enum class SaleOrdering: FieldWithName {
    DEFAULT,
    TITLE_INC,
    TITLE_DEC,
    PRICE_INC,
    PRICE_DEC,
    PUBLISH_DATE_INC,
    PUBLISH_DATE_DEC;

    override fun fieldName(c: Context?): String {
        return c?.resources?.getStringArray(R.array.sale_orderings_array)?.get(ordinal)
               ?: name
    }
}

