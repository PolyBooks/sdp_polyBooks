package com.github.polybooks.database

import com.github.polybooks.core.Book
import com.github.polybooks.core.ISBN
import com.github.polybooks.utils.listOfFuture2FutureOfList
import com.github.polybooks.utils.order
import java.util.concurrent.CompletableFuture


/**
 * A cached BookProvider uses one BookProvider as cache for another one. Queries to the
 * CachedBookProvider are first going to look into the cache, and then in the backing book provider
 * for results.
 * */
class CachedBookProvider(private val backing: BookProvider, private val cache: BookProvider):
    BookProvider {

    override fun getBooks(
        isbns: Collection<ISBN>,
        ordering: BookOrdering
    ): CompletableFuture<List<Book>> {
        val booksFromCacheFuture = cache.getBooks(isbns)
        return booksFromCacheFuture.thenCompose { booksFromCache ->
            val isbnsFound = booksFromCache.map { it.isbn }
            val remainingISBNs = isbns.minus(isbnsFound)
            val booksFromBackingFuture = backing.getBooks(remainingISBNs)
            val allBooksFuture = booksFromBackingFuture.thenApply { booksFromBacking ->
                booksFromBacking + booksFromCache
            }
            val cachingBooksFuture = booksFromBackingFuture.thenCompose { booksFromBacking ->
                val futures = booksFromBacking.map { book -> cache.addBook(book) }
                return@thenCompose listOfFuture2FutureOfList(futures)
            }
            cachingBooksFuture.thenCompose { allBooksFuture }.thenApply { order(it, ordering) }
        }
    }

    override fun addBook(book: Book): CompletableFuture<Unit> {
        val cacheFuture = cache.addBook(book)
        val backingFuture = backing.addBook(book)
        return CompletableFuture.allOf(cacheFuture, backingFuture).thenApply { }
    }

}