package com.github.polybooks.database

import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.ISBN
import com.github.polybooks.core.Interest
import com.github.polybooks.core.SaleState

/**
 * A SaleQueryBuilder allows building a query to the database that will yield Sales.
 * Most methods return themselves for function chaining.
 * */
class SaleQueryBuilder {

    private var isbn: ISBN? = null
    private var title: String? = null

    private var interests: Collection<Interest>? = null
    private var states: Collection<SaleState>? = null
    private var conditions: Collection<BookCondition>? = null

    private var minPrice: Float? = null
    private var maxPrice: Float? = null

    private var ordering: SaleOrdering = SaleOrdering.DEFAULT

    /**
     * Set this query to only include sales that satisfy the given interests.
     * */
    fun onlyIncludeInterests(interests: Collection<Interest>): SaleQueryBuilder {
        if (interests.isNotEmpty()) this.interests = interests
        else this.interests = null
        return this
    }

    /**
     * Set this query to only search for sales with book's title that are like the given one.
     *  If called successively only the last call is taken into account
     * */
    fun searchByTitle(title: String): SaleQueryBuilder {
        this.title = title
        return this
    }

    /**
     *  Set this query to only search for sales in the given states.
     *  If called successively only the last call is taken into account
     *  (see {@link SaleState})
     * */
    fun searchByState(state: Collection<SaleState>): SaleQueryBuilder {
        if (state.isNotEmpty()) this.states = state
        else this.states = null
        return this
    }

    /**
     * Set this query to only search for sales of books in the given condition.
     * If called successively only the last call is taken into account
     * (see {@link BookCondition})
     * */
    fun searchByCondition(condition: Collection<BookCondition>): SaleQueryBuilder {
        if (condition.isNotEmpty()) this.conditions = condition
        else this.conditions = null
        return this
    }

    /**
     * Set this query to only search for sales above a certain price.
     * */
    fun searchByMinPrice(min: Float): SaleQueryBuilder {
        this.minPrice = min
        return this
    }

    /**
     * Set this query to only search for sales below a certain price.
     * */
    fun searchByMaxPrice(max: Float): SaleQueryBuilder {
        this.maxPrice = max
        return this
    }

    /**
     * Set this query to only search for sales within the given price range.
     * */
    fun searchByPrice(min: Float, max: Float): SaleQueryBuilder {
        return this.searchByMinPrice(min).searchByMaxPrice(max)
    }

    /**
     * Set this query to order books with the given ordering.
     * (see {@link BookOrdering})
     * */
    fun withOrdering(ordering: SaleOrdering): SaleQueryBuilder {
        this.ordering = ordering
        return this
    }

    /**
     * Set this query to get sales of books associated with the given isbn13.
     * (ignoring other filters)
     * */
    fun searchByISBN(isbn: String): SaleQueryBuilder {
        this.isbn = isbn
        return this
    }

    /**
     * Returns the Query build by this builder
     * */
    fun get() = SaleQuery()

}