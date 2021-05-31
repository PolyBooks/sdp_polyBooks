package com.github.polybooks.database

import android.content.Context
import com.github.polybooks.core.Book
import com.github.polybooks.core.ISBN
import com.github.polybooks.core.Interest
import java.util.concurrent.CompletableFuture

/**
 * Database aggregates the functionality of Book/Sale/Interest Databases in one place
 * */
object Database {

    /**
     * The instance of a Book Database associated with this Database
     * */
    fun bookDatabase(context: Context) : BookDatabase = CompleteBookDatabase(context)

    /**
     * The instance of a Sale Database associated with this Database
     * */
    fun saleDatabase(context: Context) : SaleDatabase = FBSaleDatabase(bookDatabase(context))

    /**
     * The instance of a Interest Database associated with this Database
     * */
    val interestDatabase : InterestDatabase = FBInterestDatabase()

}

private class CompleteBookDatabase(context: Context): BookDatabase {

    val provider =
        CachedBookProvider(
            CachedBookProvider(FBBookDatabase, OLBookDatabase),
            LocalBookCache(context)
        )

    override fun searchByTitle(
        title: String,
        ordering: BookOrdering
    ): CompletableFuture<List<Book>> = FBBookDatabase.searchByTitle(title)

    override fun searchByInterests(
        interests: Collection<Interest>,
        ordering: BookOrdering
    ): CompletableFuture<List<Book>> = FBBookDatabase.searchByInterests(interests)

    override fun listAllBooks(ordering: BookOrdering): CompletableFuture<List<Book>> =
        FBBookDatabase.listAllBooks()

    override fun getBooks(
        isbns: Collection<ISBN>,
        ordering: BookOrdering
    ): CompletableFuture<List<Book>> = provider.getBooks(isbns)

    override fun addBook(book: Book): CompletableFuture<Unit> = provider.addBook(book)

}


