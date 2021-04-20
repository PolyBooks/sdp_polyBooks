package com.github.polybooks.core.database.implementation

import com.github.polybooks.core.Book
import com.github.polybooks.core.BookFields
import com.github.polybooks.core.Interest
import com.github.polybooks.core.database.interfaces.BookDatabase
import com.github.polybooks.core.database.interfaces.BookOrdering
import com.github.polybooks.core.database.interfaces.BookQuery
import com.github.polybooks.utils.listOfFuture2FutureOfList
import com.github.polybooks.utils.regulariseISBN
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CompletableFuture

private const val COLLECTION_NAME = "book"

/**
 * A book database that uses Firebase Firestore to augment the capabilities of a
 * database that only allows searching by isbn.
 * */
class FBBookDatabase(private val firebase : FirebaseFirestore, private val isbnDB : BookDatabase) : BookDatabase {

    private val bookRef = firebase.collection("book")

    override fun queryBooks(): BookQuery = FBBookQuery()

    private inner class FBBookQuery() : AbstractBookQuery() {

        override fun getAll(): CompletableFuture<List<Book>> {
            when {
                interests != null -> {
                    TODO("Not yet implemented")
                }
                title != null -> {
                    TODO("Not yet implemented")
                }
                isbns != null -> {
                    //TODO change this so that it first searches in the Firebase instance
                    val booksFromOLFuture = isbnDB.queryBooks().searchByISBN(isbns!!).getAll()
                    val booksToFBFuture =
                        booksFromOLFuture.thenCompose {
                        val listOfFutures =
                            it.map { book -> addBookToFirebase(book) }
                        listOfFuture2FutureOfList(listOfFutures)
                    }
                    //fail if writing to FB fails. return books otherwise.
                    return booksToFBFuture.thenCompose {booksFromOLFuture}
                }
                else -> {
                    throw Error("BookQuery is in an illegal state")
                }
            }
        }

        override fun getN(n: Int, page: Int): CompletableFuture<List<Book>> {
            TODO("Not yet implemented")
        }

        override fun getCount(): CompletableFuture<Int> {
            TODO("Not yet implemented")
        }

        private fun bookToDocument(book : Book) : Any {
            return hashMapOf(
                BookFields.AUTHORS.fieldName to book.authors,
                BookFields.EDITION.fieldName to book.edition,
                BookFields.FORMAT.fieldName to book.format,
                BookFields.ISBN.fieldName to book.isbn,
                BookFields.LANGUAGE.fieldName to book.language,
                BookFields.PUBLISHDATE.fieldName to book.publishDate,
                BookFields.PUBLISHER.fieldName to book.publisher,
                BookFields.TITLE.fieldName to book.title,
            )
        }

        private fun snapshotToBook(snapshot : DocumentSnapshot) : Book {
            TODO("Not yet implemented")
        }

        private fun addBookToFirebase(book : Book) : CompletableFuture<Unit> {
            val future = CompletableFuture<Unit>()
            bookRef.document(book.isbn).set(bookToDocument(book))
                .addOnSuccessListener {
                    future.complete(Unit)
                }.addOnFailureListener {
                    future.completeExceptionally(it)
                }
            return future
        }

    }

}