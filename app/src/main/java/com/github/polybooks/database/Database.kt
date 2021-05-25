package com.github.polybooks.database

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
    val bookDatabase: BookDatabase = CompleteBookDatabase

    /**
     * The instance of a Sale Database associated with this Database
     * */
    val saleDatabase: SaleDatabase = FBSaleDatabase(bookDatabase)

    /**
     * The instance of a Interest Database associated with this Database
     * */
    val interestDatabase: InterestDatabase = DummyInterestDatabase

}

object CompleteBookDatabase: BookDatabase {

    val provider =
        CachedBookProvider(CachedBookProvider(FBBookDatabase, OLBookDatabase), LocalBookCache)

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



