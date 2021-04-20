package com.github.polybooks.core.database.implementation

import com.github.polybooks.core.Interest
import com.github.polybooks.core.database.interfaces.BookOrdering
import com.github.polybooks.core.database.interfaces.BookOrdering.*
import com.github.polybooks.core.database.interfaces.BookQuery
import com.github.polybooks.utils.regulariseISBN

/**
 * Aggregates common code for all BookQueries.
 * */
internal abstract class AbstractBookQuery : BookQuery {
    protected var interests : Collection<Interest>? = null
    protected var title : String? = null
    protected var isbns : Set<String>? = null
    protected var ordering : BookOrdering = DEFAULT;

    override fun onlyIncludeInterests(interests: Collection<Interest>): BookQuery {
        title = null
        isbns = null
        this.interests = interests
        return this
    }

    override fun searchByTitle(title: String): BookQuery {
        interests = null
        isbns = null
        this.title = title
        return this
    }

    override fun searchByISBN(isbns: Set<String>): BookQuery {
        val regularised = isbns.map { regulariseISBN(it) ?: throw IllegalArgumentException("ISBN \"$it\" is not valid") }
        title = null
        interests = null
        this.isbns = regularised.toSet()
        return this
    }

    override fun withOrdering(ordering: BookOrdering): BookQuery {
        this.ordering = ordering
        return this
    }

}