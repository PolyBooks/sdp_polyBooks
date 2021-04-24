package com.github.polybooks.core.database.implementation

import com.github.polybooks.core.Book
import com.github.polybooks.core.BookFields
import com.github.polybooks.core.Interest
import com.github.polybooks.core.database.interfaces.BookDatabase
import com.github.polybooks.core.database.interfaces.BookOrdering
import com.github.polybooks.core.database.interfaces.BookQuery
import com.github.polybooks.utils.listOfFuture2FutureOfList
import com.github.polybooks.utils.regulariseISBN
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CompletableFuture

private const val COLLECTION_NAME = "book"

/**
 * A book database that uses Firebase Firestore to augment the capabilities of a
 * database that only allows searching by isbn.
 * */
class FBBookDatabase(private val firebase : FirebaseFirestore, private val isbnDB : BookDatabase) : BookDatabase {

    /*TODO:
    [x] proxy search by isbn to OLBookDatabase
    [x] save books from OL to Firebase
    [x] use firebase as cache
    [ ] handle ISBN10 and alternative ISBN better (not always ask OL for aid)
    [x] optimise the search by ISBN
    [ ] allow search by title
    [ ] allow search by interest
    [ ] implement getN and count
    */

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
                    val isbns = this.isbns!!
                    //TODO change this so that it first searches in the Firebase instance
                    val booksFromFBFuture = getBooksByISBNFromFirebase(isbns.toList())
                    return booksFromFBFuture.thenCompose { booksFromFB ->
                        val isbnsFound = booksFromFB.map { it.isbn }
                        val remainingISBNs = isbns.minus(isbnsFound)
                        val booksFromOLFuture = isbnDB.queryBooks().searchByISBN(remainingISBNs).getAll()
                        val allBooksFuture = booksFromOLFuture.thenApply { booksFromOL ->
                            booksFromOL + booksFromFB
                        }
                        val booksToFBFuture = booksFromOLFuture.thenCompose { booksFromOL ->
                            val futures = booksFromOL.map { book -> addBookToFirebase(book) }
                            listOfFuture2FutureOfList(futures)
                        }
                        booksToFBFuture.thenCompose { allBooksFuture }
                    }
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

        private fun assembleBookEntry(bookDocument : Any) : Any {
            return hashMapOf(
                "book" to bookDocument,
                "interests" to listOf<Any>()
            )
        }

        private fun snapshotBookToBook(map: HashMap<String,Any>): Book {
            return Book(
                map[BookFields.ISBN.fieldName] as String,
                map[BookFields.AUTHORS.fieldName] as List<String>?,
                map[BookFields.TITLE.fieldName] as String,
                map[BookFields.EDITION.fieldName] as String?,
                map[BookFields.LANGUAGE.fieldName] as String?,
                map[BookFields.PUBLISHER.fieldName] as String?,
                (map[BookFields.PUBLISHDATE.fieldName] as Timestamp?)?.let {timestampConvert(it)},
                map[BookFields.FORMAT.fieldName] as String?
            )
        }

        private fun timestampConvert(firebase : Timestamp) : java.sql.Timestamp {
            return java.sql.Timestamp(firebase.toDate().time)
        }

        private fun snapshotEntryToBook(snapshot : DocumentSnapshot) : Book {
            val bookDocument = snapshot.get("book") as HashMap<String, Any>
            return snapshotBookToBook(bookDocument)
        }

        private fun addBookToFirebase(book : Book) : CompletableFuture<Unit> {
            val future = CompletableFuture<Unit>()
            val bookEntry = assembleBookEntry(bookToDocument(book))
            bookRef.document(book.isbn).set(bookEntry)
                .addOnSuccessListener {
                    future.complete(Unit)
                }.addOnFailureListener {
                    future.completeExceptionally(it)
                }
            return future
        }

        private fun getBooksByISBNFromFirebase(isbns : List<String>) : CompletableFuture<List<Book>> {
            val future = CompletableFuture<List<Book>>()
            bookRef.whereIn(FieldPath.documentId(), isbns)
                .get().addOnSuccessListener { bookEntries ->
                    val books = bookEntries.map { bookEntry ->
                        snapshotEntryToBook(bookEntry)
                    }
                    future.complete(books)
                }
            return future
        }

    }

}