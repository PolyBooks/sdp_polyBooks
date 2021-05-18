package com.github.polybooks.database

import com.github.polybooks.core.ISBN
import com.github.polybooks.core.Interest
import com.github.polybooks.utils.regulariseISBN

/**
 * A BookQueryBuilder allows building query to the database that will yield Books.
 * Most methods return themselves for function chaining.
 * */
class BookQueryBuilder {
    private var interests : Collection<Interest>? = null
    private var title : String? = null
    private var isbns : Collection<ISBN>? = null
    private var ordering : BookOrdering = BookOrdering.DEFAULT

    /**
     * Set this query to only include books that satisfy the given interests.
     * */
    fun onlyIncludeInterests(interests: Collection<Interest>) : BookQueryBuilder {
        title = null
        isbns = null
        this.interests = interests
        return this
    }

    /**
     * Set this query to only search for books with title that are like the given one.
     * (ignoring other filters)
     * */
    fun searchByTitle(title : String) : BookQueryBuilder {
        interests = null
        isbns = null
        this.title = title
        return this
    }

    /**
     * Set this query to get the books associated with the given ISBNs, if they exist.
     * (ignoring other filters)
     * */
    fun searchByISBN(isbns : Collection<ISBN>) : BookQueryBuilder {
        val regularised = isbns.map { regulariseISBN(it) ?: throw IllegalArgumentException("ISBN \"$it\" is not valid") }
        title = null
        interests = null
        this.isbns = regularised
        return this
    }

    /**
     * Set this query to order books with the given ordering.
     * (see {@link BookOrdering})
     * */
    fun withOrdering(ordering : BookOrdering) : BookQueryBuilder {
        this.ordering = ordering
        return this
    }

    /**
     * Returns the Query build by this builder
     * */
    fun get() : BookQuery = BookQuery(isbns, title, interests, ordering)

}