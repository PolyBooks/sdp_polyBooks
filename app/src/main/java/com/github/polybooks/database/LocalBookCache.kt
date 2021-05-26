package com.github.polybooks.database

import android.content.Context
import com.github.polybooks.core.Book
import com.github.polybooks.core.ISBN
import java.util.concurrent.CompletableFuture

class LocalBookCache(private val context: Context): BookProvider {

    override fun getBooks(
        isbns: Collection<ISBN>,
        ordering: BookOrdering
    ): CompletableFuture<List<Book>> {
        return CompletableFuture.completedFuture(listOf())
    }

    override fun addBook(book: Book): CompletableFuture<Unit> {
        return CompletableFuture.completedFuture(Unit)
    }

}