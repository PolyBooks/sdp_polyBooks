package com.github.polybooks.database

import com.github.polybooks.core.*
import com.github.polybooks.database.SaleOrdering.*
import com.github.polybooks.utils.listOfFuture2FutureOfList
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.HashMap

private const val COLLECTION_NAME = "sale2"

/**
 *  !! DO NOT INSTANTIATE THIS CLASS. If you are writing a UI you should always use Database.saleDatabase instead.
 * A sale database that uses Firebase Firestore to store and retrieve sales
 * */
class FBSaleDatabase(private val bookDatabase: BookDatabase): SaleDatabase {

    private val saleRef: CollectionReference =
        FirebaseProvider.getFirestore().collection(COLLECTION_NAME)

    //This functions computes the set of queries to execute in order to get the desired Sales.
    //We need to return a set of queries because Firebase doesn't allow for more than one IN clause.
    //Ergo, we need to do multiple queries.
    private fun filterQuery(q: Query, saleQuery: SaleQuery): List<Query> {
        var queries = listOf(q)

        saleQuery.minPrice?.let {
            queries = queries.map {
                it.whereGreaterThanOrEqualTo(
                    SaleFields.PRICE.fieldName,
                    saleQuery.minPrice
                )
            }
        }
        saleQuery.maxPrice?.let {
            queries = queries.map {
                it.whereLessThanOrEqualTo(
                    SaleFields.PRICE.fieldName,
                    saleQuery.maxPrice
                )
            }
        }
        saleQuery.states?.let {
            queries = queries.flatMap { query ->
                saleQuery.states.map { state -> query.whereEqualTo(SaleFields.STATE.fieldName, state) }
            }
        }
        saleQuery.conditions?.let {
            queries = queries.flatMap { query ->
                saleQuery.conditions.map { condition ->
                    query.whereEqualTo(
                        SaleFields.CONDITION.fieldName,
                        condition
                    )
                }
            }
        }

        return queries
    }

    private fun doQuery(query: Query): CompletableFuture<Iterable<DocumentSnapshot>> {
        val future = CompletableFuture<Iterable<DocumentSnapshot>>()
        query.get()
            .addOnSuccessListener { documents ->
                future.complete(documents)
            }
            .addOnFailureListener {
                future.completeExceptionally(
                    DatabaseException("Query could not be completed: $it")
                )
            }
        return future
    }

    private fun doQueries(queries: List<Query>): CompletableFuture<Iterable<DocumentSnapshot>> {
        return listOfFuture2FutureOfList(queries.map { doQuery(it) }
            .toList()).thenApply { it.flatten() }
    }


    private fun getBookQuery(saleQuery: SaleQuery): BookQuery {
        var bookQuery: BookQuery = AllBooksQuery()
        if (saleQuery.interests != null) bookQuery = bookQuery.searchByInterests(saleQuery.interests)
        if (saleQuery.title != null) bookQuery = bookQuery.searchByTitle(saleQuery.title)
        if (saleQuery.isbn != null) bookQuery = bookQuery.getBooks(setOf(saleQuery.isbn))
        return bookQuery
    }

    override fun execute(saleQuery: SaleQuery): CompletableFuture<List<Sale>> {
        return if (saleQuery.interests == null && saleQuery.title == null && saleQuery.isbn == null) { //In this case we should not look in the book database
            doQueries(filterQuery(saleRef, saleQuery)).thenCompose { snapshotsToSales(it) }
        } else {
            val booksFuture = bookDatabase.execute(getBookQuery(saleQuery))
            booksFuture.thenCompose { books ->  //those are the books for which we want to find the sales
                val isbns = books.map { it.isbn }
                if (isbns.isEmpty()) return@thenCompose CompletableFuture.completedFuture(listOf())
                val isbnToBook =
                    books.associateBy { it.isbn } //is used a cache to transform snapshots to Sales
                val fbQuery = saleRef.whereIn(SaleFields.BOOK_ISBN.fieldName, isbns)
                doQueries(filterQuery(fbQuery, saleQuery)).thenCompose {
                    snapshotsToSales(
                        it,
                        isbnToBook
                    )
                }.thenApply { order(it, saleQuery.ordering) }
            }
        }
    }


    private fun order(sales: List<Sale>, ordering: SaleOrdering): List<Sale> {
        return when (ordering) {
            DEFAULT -> sales
            TITLE_INC -> sales.sortedBy { it.book.title }
            TITLE_DEC -> sales.sortedByDescending { it.book.title }
            PRICE_INC -> sales.sortedBy { it.price }
            PRICE_DEC -> sales.sortedByDescending { it.price }
            PUBLISH_DATE_INC -> sales.sortedBy { it.date }
            PUBLISH_DATE_DEC -> sales.sortedByDescending { it.date }
        }
    }

    private fun snapshotToUser(map: HashMap<String, Any>): User {
        val uid = map[UserFields.UID.fieldName] as String
        val pseudo = map[UserFields.PSEUDO.fieldName] as String

        return LoggedUser(uid, pseudo)
    }

    private fun snapshotToSale(snapshot: DocumentSnapshot, bookCache: Map<ISBN, Book>): Sale {
        val isbn = snapshot[SaleFields.BOOK_ISBN.fieldName] as ISBN
        return Sale(
            bookCache[isbn]!!, // The cache should contain the book, otherwise the call to this function is illegal
            snapshotToUser(snapshot.get(SaleFields.SELLER.fieldName)!! as HashMap<String, Any>),
            snapshot.getLong(SaleFields.PRICE.fieldName)!!.toFloat(),
            BookCondition.valueOf(snapshot.getString(SaleFields.CONDITION.fieldName)!!),
            snapshot.getTimestamp(SaleFields.PUBLICATION_DATE.fieldName)!!.toDate(),
            SaleState.valueOf(snapshot.getString(SaleFields.STATE.fieldName)!!),
            null
        )
    }

    private fun snapshotsToSales(
        snapshots: Iterable<DocumentSnapshot>,
        bookCache: Map<ISBN, Book> = mapOf()
    ): CompletableFuture<List<Sale>> {
        val missingBooks =
            snapshots.map { doc -> doc[SaleFields.BOOK_ISBN.fieldName] as ISBN }.toSet()
                .minus(bookCache.keys)
        if (missingBooks.isEmpty()) {
            val sales = snapshots.map { doc ->
                snapshotToSale(doc, bookCache)
            }
            return CompletableFuture.completedFuture(sales)
        } else {
            val booksFuture = bookDatabase
                .getBooks(missingBooks.toSet())
            return booksFuture.thenApply { books ->
                val biggerBookCache = bookCache + books.associateBy { it.isbn }
                snapshots.map { doc ->
                    snapshotToSale(doc, biggerBookCache)
                }
            }
        }
    }

    private fun saleToDocument(sale: Sale): Any {
        return hashMapOf(
            SaleFields.BOOK_ISBN.fieldName to sale.book.isbn,
            SaleFields.SELLER.fieldName to sale.seller,
            SaleFields.PRICE.fieldName to sale.price,
            SaleFields.CONDITION.fieldName to sale.condition,
            SaleFields.PUBLICATION_DATE.fieldName to Timestamp(sale.date),
            SaleFields.STATE.fieldName to sale.state,
            SaleFields.IMAGE.fieldName to null //TODO change this, image goes elsewhere
        )
    }

    override fun addSale(
        bookISBN: ISBN,
        seller: User,
        price: Float,
        condition: BookCondition,
        state: SaleState,
        image: Image?
    ): CompletableFuture<Sale> {

        if (seller == LocalUser) {
            val future = CompletableFuture<Sale>()
            future.completeExceptionally(LocalUserException("Cannot add sale as LocalUser"))
            return future
        }
        val bookFuture = bookDatabase.getBook(bookISBN)
        return bookFuture.thenCompose { book ->
            val future = CompletableFuture<Sale>()
            if (book == null) {
                future.completeExceptionally(DatabaseException("Could not find book associated with sale : isbn = $bookISBN"))
            } else {
                val sale = Sale(book, seller, price, condition, Date(), state, image)
                saleRef.add(saleToDocument(sale))
                    .addOnSuccessListener {
                        future.complete(sale)
                    }
                    .addOnFailureListener {
                        future.completeExceptionally(DatabaseException("Failed to insert $sale into Database because of : $it"))
                    }
            }
            future
        }
    }

    //find the ID of a sale based on the ISBN of the book being sold, the time of the publication and the UID of the seller
    private fun getReferenceID(sale: Sale): Task<DocumentSnapshot?> {
        val query = saleRef
            .whereEqualTo(SaleFields.BOOK_ISBN.fieldName, sale.book.isbn)
            .whereEqualTo(SaleFields.PUBLICATION_DATE.fieldName, sale.date)
            .whereEqualTo(
                SaleFields.SELLER.fieldName + "." + UserFields.UID.fieldName,
                (sale.seller as LoggedUser).uid
            )
        return query.get().continueWith { task -> task.result.documents.firstOrNull() }

    }

    override fun deleteSale(sale: Sale): CompletableFuture<Boolean> {
        if (sale.seller == LocalUser) {
            throw IllegalArgumentException("A sale by the LocalUser is invalid")
        }
        val future = CompletableFuture<Boolean>()
        getReferenceID(sale).addOnSuccessListener { toDelete ->
            if (toDelete == null) future.complete(false)
            else {
                saleRef.document(toDelete.id).delete()
                    .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not delete $toDelete")) }
                    .addOnSuccessListener { future.complete(true) }
            }
        }
        return future
    }
}

