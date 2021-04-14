package com.github.polybooks.core.database.implementation

import com.github.polybooks.core.Book
import com.github.polybooks.core.Interest
import com.github.polybooks.core.database.interfaces.BookDatabase
import com.github.polybooks.core.database.interfaces.BookOrdering
import com.github.polybooks.core.database.interfaces.BookQuery
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CompletableFuture

private const val COLLECTION_NAME = "book"

/**
 * A book database that uses Firebase Firestore to augment the capabilities of a
 * database that only allows searching by isbn.
 * */
class FBBookDatabase(private val firebase : FirebaseFirestore, private val isbnDB : BookDatabase) : BookDatabase {

    override fun queryBooks(): BookQuery {
        TODO("Not yet implemented")
    }

    inner class FBBookQuery : BookQuery {
        override fun onlyIncludeInterests(interests: Collection<Interest>): BookQuery {
            TODO("Not yet implemented")
        }

        override fun searchByTitle(title: String): BookQuery {
            TODO("Not yet implemented")
        }

        override fun searchByISBN13(isbn13: String): BookQuery {
            TODO("Not yet implemented")
        }

        override fun withOrdering(ordering: BookOrdering): BookQuery {
            TODO("Not yet implemented")
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