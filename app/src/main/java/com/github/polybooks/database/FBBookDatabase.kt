package com.github.polybooks.database

import com.github.polybooks.core.*
import com.github.polybooks.utils.order
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import java.text.SimpleDateFormat
import java.util.concurrent.CompletableFuture

private const val COLLECTION_NAME = "book"
private const val DATE_FORMAT = "yyyy MM dd"

/**
 *  !! DO NOT INSTANTIATE THIS CLASS. If you are writing a UI you should always use Database.bookDatabase instead.
 * A book database that uses Firebase Firestore to augment the capabilities of a
 * database that only allows searching by isbn.
 * */
object FBBookDatabase: BookDatabase {

    private val bookRef = FirebaseProvider.getFirestore().collection(COLLECTION_NAME)
    private val dateFormatter = SimpleDateFormat(DATE_FORMAT)

    override fun addBook(book: Book): CompletableFuture<Unit> {
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

    override fun searchByTitle(
        title: String,
        ordering: BookOrdering
    ): CompletableFuture<List<Book>> {
        val future = CompletableFuture<List<Book>>()
        bookRef
            .whereGreaterThanOrEqualTo("book.title", title)
            .whereLessThanOrEqualTo("book.title", title + '\uf88f')
            .get().addOnSuccessListener { bookEntries ->
                val books = bookEntries.map { bookEntry ->
                    snapshotEntryToBook(bookEntry)
                }
                val ordered = order(books, ordering)
                future.complete(ordered)
            }
        return future
    }

    override fun searchByInterests(
        interests: Collection<Interest>,
        ordering: BookOrdering
    ): CompletableFuture<List<Book>> {
        val future = CompletableFuture<List<Book>>()
        val hashed = interests.map { it.hashCode() }.toList()
        bookRef
            .whereArrayContainsAny("interests", hashed)
            .get().addOnSuccessListener { bookEntries ->
                val books = bookEntries.map { bookEntry ->
                    snapshotEntryToBook(bookEntry)
                }
                val ordered = order(books, ordering)
                future.complete(ordered)
            }
        return future
    }

    override fun listAllBooks(ordering: BookOrdering): CompletableFuture<List<Book>> {
        val future = CompletableFuture<List<Book>>()
        bookRef.get().addOnSuccessListener { bookEntries ->
            val books = bookEntries.map { bookEntry ->
                snapshotEntryToBook(bookEntry)
            }
            val ordered = order(books, ordering)
            future.complete(ordered)
        }
        return future
    }

    override fun getRating(isbn: ISBN): CompletableFuture<BookRating> {
        val future = CompletableFuture<BookRating>()
        BookRating.bookRatingRef.document(isbn)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    future.complete(BookRating(document.data!!))
                }
            }
            .addOnFailureListener {
                future.completeExceptionally(it)
            }

        return future
    }

    override fun setRating(isbn: ISBN, bookRating: BookRating): CompletableFuture<Unit> {
        return bookRating.uploadToFirebase(isbn)
    }

    override fun getBooks(
        isbns: Collection<ISBN>,
        ordering: BookOrdering
    ): CompletableFuture<List<Book>> {
        if (isbns.isEmpty()) return CompletableFuture.completedFuture(listOf())
        val future = CompletableFuture<List<Book>>()
        bookRef.whereIn(FieldPath.documentId(), isbns.toList())
            .get().addOnSuccessListener { bookEntries ->
                val books = bookEntries.map { bookEntry ->
                    snapshotEntryToBook(bookEntry)
                }
                future.complete(books)
            }
        return future
    }

    private fun bookToDocument(book: Book): Any {
        val publishDate: String? = book.publishDate?.let {
            dateFormatter.format(it)
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

    private fun assembleBookEntry(bookDocument: Any): Any {
        return hashMapOf(
            "book" to bookDocument,
            "interests" to listOf<Any>()
        )
    }

    private fun snapshotBookToBook(map: HashMap<String, Any>): Book {
        val publishDate = (map[BookFields.PUBLISHDATE.fieldName] as String?)?.let {
            dateFormatter.parse(it)
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

    private fun snapshotEntryToBook(snapshot: DocumentSnapshot): Book {
        val bookDocument = snapshot.get("book") as HashMap<String, Any>
        return snapshotBookToBook(bookDocument)
    }

}