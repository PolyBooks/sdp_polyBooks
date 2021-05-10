package com.github.polybooks.core.database.implementation

import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.ISBN
import com.github.polybooks.core.Interest
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleQuery

class SaleQueryBuilder {

    private var isbn: ISBN? = null
    private var title: String? = null

    private var interests: Set<Interest>? = null
    private var states: Set<SaleState>? = null
    private var conditions: Set<BookCondition>? = null

    private var minPrice: Float? = null
    private var maxPrice: Float? = null

    private var ordering: SaleOrdering = SaleOrdering.DEFAULT

    fun onlyIncludeInterests(interests: Set<Interest>): SaleQueryBuilder {
        if (interests.isNotEmpty()) this.interests = interests
        else this.interests = null
        return this
    }

    fun searchByTitle(title: String): SaleQueryBuilder {
        this.title = title
        return this
    }

    fun searchByState(state: Set<SaleState>): SaleQueryBuilder {
        if (state.isNotEmpty()) this.states = state
        else this.states = null
        return this
    }

    fun searchByCondition(condition: Set<BookCondition>): SaleQueryBuilder {
        if (condition.isNotEmpty()) this.conditions = condition
        else this.conditions = null
        return this
    }

    fun searchByMinPrice(min: Float): SaleQueryBuilder {
        this.minPrice = min
        return this
    }

    fun searchByMaxPrice(max: Float): SaleQueryBuilder {
        this.maxPrice = max
        return this
    }

    fun searchByPrice(min: Float, max: Float): SaleQueryBuilder {
        return this.searchByMinPrice(min).searchByMaxPrice(max)
    }

    fun withOrdering(ordering: SaleOrdering): SaleQueryBuilder {
        this.ordering = ordering
        return this
    }

    fun searchByISBN(isbn: ISBN): SaleQueryBuilder {
        this.isbn = isbn
        return this
    }

    fun getQuery() : SaleQuery = SaleQuery(isbn, title, interests, states, conditions, minPrice, maxPrice, ordering)
}