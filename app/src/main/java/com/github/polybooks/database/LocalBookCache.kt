package com.github.polybooks.database

import com.github.polybooks.core.Book
import com.github.polybooks.core.ISBN
import java.util.concurrent.CompletableFuture

object LocalBookCache : BookProvider {

    override fun getBooks(isbns: Collection<ISBN>, ordering: BookOrdering): CompletableFuture<List<Book>> {
        return CompletableFuture.completedFuture(listOf())
    }

    override fun addBook(book: Book): CompletableFuture<Unit> {
        return CompletableFuture.completedFuture(Unit)
    }

}