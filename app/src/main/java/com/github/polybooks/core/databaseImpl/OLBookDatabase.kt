package com.github.polybooks.core.databaseImpl

import android.annotation.SuppressLint
import com.github.polybooks.core.Book
import com.github.polybooks.core.Interest
import com.github.polybooks.core.database.*
import com.github.polybooks.core.database.BookOrdering.*
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * An implementation of a book database based on the Open Library online database
 * */
@SuppressLint("NewApi")
class OLBookDatabase : BookDatabase {

    override fun queryBooks(): BookQuery = OLBookQuery()

    private inner class OLBookQuery : BookQuery {

        private var ordering = DEFAULT

        private var empty : Boolean = false
        private var title : Optional<String> = Optional.empty()
        private var isbn : Optional<String> = Optional.empty()

        override fun onlyIncludeInterests(interests: Collection<Interest>): BookQuery {
            System.err.println("Warning: onlyIncludeInterest not fully implemented for OLBookQuery")
            this.empty = true
            return this
        }

        override fun searchByTitle(title: String): BookQuery {
            this.empty = false
            this.title = Optional.of(title)
            this.isbn = Optional.empty()
            return this
        }

        override fun searchByISBN13(isbn13: String): BookQuery {
            this.empty = false
            this.title = Optional.empty()
            this.isbn = Optional.of(isbn13)
            return this
        }

        override fun withOrdering(ordering: BookOrdering): BookQuery {
            this.ordering = ordering
            return this
        }

        override fun getAll(): CompletableFuture<List<Book>> {
            TODO("Not yet implemented")
        }

        override fun getN(n: Int, page: Int): CompletableFuture<List<Book>> {
            TODO("Not yet implemented")
        }

        override fun getCount(): CompletableFuture<Int> {
            TODO("Not yet implemented")
        }

    }

}