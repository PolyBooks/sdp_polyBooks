package com.github.polybooks.database

import com.github.polybooks.core.Book
import com.github.polybooks.core.BookFields
import com.github.polybooks.core.ISBN
import com.github.polybooks.utils.listOfFuture2FutureOfList
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture

private const val COLLECTION_NAME = "book"
private const val DATE_FORMAT = "yyyy MM dd"

/**
 * A book database that uses Firebase Firestore to augment the capabilities of a
 * database that only allows searching by isbn.
 * */
class FBBookDatabase(private val bookProvider : BookDatabase) : BookDatabase {

    private val bookRef = FirebaseProvider.getFirestore().collection(COLLECTION_NAME)
    private val dateFormatter = SimpleDateFormat(DATE_FORMAT)

    override fun queryBooks(): BookQuery = FBBookQuery()

    private inner class FBBookQuery: AbstractBookQuery() {

        override fun getAll(): CompletableFuture<List<Book>> {
            when {
                interests != null -> {
                    val future = CompletableFuture<List<Book>>()
                    val hashed = interests!!.map { it.hashCode() }.toList()
                    bookRef
                        .whereArrayContainsAny("interests", hashed)
                        .get().addOnSuccessListener { bookEntries ->
                            val books = bookEntries.map { bookEntry ->
                                snapshotEntryToBook(bookEntry)
                            }
                            future.complete(books)
                        }
                    return future
                }
                title != null -> {
                    val future = CompletableFuture<List<Book>>()
                    bookRef
                        .whereGreaterThanOrEqualTo("book.title", title!!)
                        .whereLessThanOrEqualTo("book.title", title!! + '\uf88f')
                        .get().addOnSuccessListener { bookEntries ->
                            val books = bookEntries.map { bookEntry ->
                                snapshotEntryToBook(bookEntry)
                            }
                            future.complete(books)
                        }
                    return future
                }
                isbns != null -> {
                    val isbns = this.isbns!!
                    val booksFromFBFuture = getBooksByISBNFromFirebase(isbns.toList())
                    return booksFromFBFuture.thenCompose { booksFromFB ->
                        val isbnsFound = booksFromFB.map { it.isbn }
                        val remainingISBNs = isbns.minus(isbnsFound)
                        val booksFromOLFuture = bookProvider.queryBooks().searchByISBN(remainingISBNs).getAll()
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
                    val future = CompletableFuture<List<Book>>()
                    bookRef.get().addOnSuccessListener { bookEntries ->
                            val books = bookEntries.map { bookEntry ->
                                snapshotEntryToBook(bookEntry)
                            }
                            future.complete(books)
                        }
                    return future

                }
            }
        }

        private fun bookToDocument(book : Book) : Any {
            val publishDate : String? = book.publishDate?.let {
                dateFormatter.format(it.toDate())
            }
            return hashMapOf(
                BookFields.AUTHORS.fieldName to book.authors,
                BookFields.EDITION.fieldName to book.edition,
                BookFields.FORMAT.fieldName to book.format,
                BookFields.ISBN.fieldName to book.isbn,
                BookFields.LANGUAGE.fieldName to book.language,
                BookFields.PUBLISHDATE.fieldName to publishDate,
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
            val publishDate = (map[BookFields.PUBLISHDATE.fieldName] as String?)?.let {
                Timestamp(dateFormatter.parse(it)!!)
            }
            return Book(
                map[BookFields.ISBN.fieldName] as ISBN,
                map[BookFields.AUTHORS.fieldName] as List<String>?,
                map[BookFields.TITLE.fieldName] as String,
                map[BookFields.EDITION.fieldName] as String?,
                map[BookFields.LANGUAGE.fieldName] as String?,
                map[BookFields.PUBLISHER.fieldName] as String?,
                publishDate,
                map[BookFields.FORMAT.fieldName] as String?
            )
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

        private fun getBooksByISBNFromFirebase(isbns : List<ISBN>) : CompletableFuture<List<Book>> {
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