package com.github.polybooks.database

import android.content.Context
import com.github.polybooks.R
import com.github.polybooks.core.*
import com.github.polybooks.database.SaleOrdering.*
import com.github.polybooks.utils.FieldWithName
import java.io.Serializable
import java.util.concurrent.CompletableFuture

/**
 * Provides the API for accessing sales in a Database.
 * */
interface SaleDatabase {

    /**
     * Execute a given query
     * */
    fun execute(query: SaleQuery): CompletableFuture<List<Sale>>

    /**
     * Get all the sales in the database
     * */
    fun listAllSales(): CompletableFuture<List<Sale>> = execute(SaleQuery())

    /**
     * Add the sale defined by the given parameters to the database
     * @param bookISBN the isbn of the book being sold
     * @param seller the user selling the book (can't be the local user)
     * @param price the price of the sale
     * @param condition the condition of the book
     * @param state the state of the sale
     * @param image the image describing the book being sold
     * @return a future containing the sale created and added to the database
     */
    fun addSale(bookISBN : ISBN,
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
    fun deleteSale(sale: Sale) : CompletableFuture<Boolean>
}

/**
 * A SaleQuery represents the different filters to apply to sales in a query to a SaleDatabase.
 * isbn, title, and interests are mutually exclusive; It is implementation defined what will be
 * returned if several of these filters are defined.
 * ordering defines how the sales will be ordered
 * */
data class SaleQuery(
    val isbn: String? = null,
    val title: String? = null,
    val interests: List<Interest>? = null,
    val states: List<SaleState>? = null,
    val conditions: List<BookCondition>? = null,
    val minPrice: Float? = null,
    val maxPrice: Float? = null,
    val ordering: SaleOrdering = DEFAULT
): Serializable {

    /**
     * Set this query to only include sales that satisfy the given interests.
     * */
    fun searchByInterests(interests: List<Interest>): SaleQuery {
        return if (interests.isNotEmpty()) this.copy(interests = interests, title = null, isbn = null)
        else this.copy(interests = null)
    }

    /**
     * Set this query to only search for sales with book's title that are like the given one.
     *  If called successively only the last call is taken into account
     * */
    fun searchByTitle(title: String): SaleQuery {
        return this.copy(title = title, interests = null, isbn = null)
    }

    /**
     *  Set this query to only search for sales in the given states.
     *  If called successively only the last call is taken into account
     *  (see {@link SaleState})
     * */
    fun searchByState(states: List<SaleState>): SaleQuery {
        return if (states.isNotEmpty()) this.copy(states = states)
        else this.copy(states = null)
    }

    /**
     * Set this query to only search for sales of books in the given condition.
     * If called successively only the last call is taken into account
     * (see {@link BookCondition})
     * */
    fun searchByCondition(conditions: List<BookCondition>): SaleQuery {
        return if (conditions.isNotEmpty()) this.copy(conditions = conditions)
        else this.copy(conditions = null)
    }

    /**
     * Set this query to only search for sales above a certain price.
     * */
    fun searchByMinPrice(min: Float): SaleQuery {
        return this.copy(minPrice = min)
    }

    /**
     * Set this query to only search for sales below a certain price.
     * */
    fun searchByMaxPrice(max: Float): SaleQuery {
        return this.copy(maxPrice = max)
    }

    /**
     * Set this query to only search for sales within the given price range.
     * */
    fun searchByPrice(min: Float, max: Float): SaleQuery {
        return this.searchByMinPrice(min).searchByMaxPrice(max)
    }

    /**
     * Set this query to order books with the given ordering.
     * (see {@link BookOrdering})
     * */
    fun withOrdering(ordering: SaleOrdering): SaleQuery {
        return this.copy(ordering = ordering)
    }

    /**
     * Set this query to get sales of books associated with the given isbn.
     * (ignoring other filters)
     * */
    fun searchByISBN(isbn: String): SaleQuery {
        return this.copy(isbn = isbn, title = null, interests = null)
    }

}

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
