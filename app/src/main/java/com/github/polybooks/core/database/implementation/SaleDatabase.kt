package com.github.polybooks.core.database.implementation

import com.github.polybooks.core.*
import com.github.polybooks.core.database.DatabaseException
import com.github.polybooks.core.database.LocalUserException
import com.github.polybooks.core.database.interfaces.*
import com.github.polybooks.core.database.interfaces.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleOrdering.*
import com.github.polybooks.utils.listOfFuture2FutureOfList
import com.github.polybooks.utils.url2json
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.Query
import java.util.concurrent.CompletableFuture

private const val COLLECTION_NAME = "sale2"

class SaleDatabase(firestore: FirebaseFirestore, private val bookDB: BookDatabase) : SaleDatabase() {

    private val saleRef: CollectionReference = firestore.collection(COLLECTION_NAME)

    override fun execute(saleQuery: SaleQuery): CompletableFuture<List<Sale>> {

        val isbn = saleQuery.isbn
        val title = saleQuery.title
        val interests = saleQuery.interests
        val conditions = saleQuery.conditions
        val states = saleQuery.states
        val minPrice = saleQuery.minPrice
        val maxPrice = saleQuery.maxPrice
        val ordering = saleQuery.ordering

        //This functions computes the set of queries to execute in order to get the desired Sales.
        //We need to return a set of queries because Firebase doesn't allow for more than one IN clause.
        //Ergo, we need to do multiple queries.
        fun filterQuery(q: Query) : List<Query> {
            var queries = listOf(q)

            minPrice?.let { queries = queries.map{it.whereGreaterThanOrEqualTo(SaleFields.PRICE.fieldName, minPrice!!)} }
            maxPrice?.let { queries = queries.map{it.whereLessThanOrEqualTo(SaleFields.PRICE.fieldName, maxPrice!!)} }
            states?.let {
                queries = queries.flatMap { query ->
                    states!!.map{ state -> query.whereEqualTo(SaleFields.STATE.fieldName, state) }
                }
            }
            conditions?.let {
                queries = queries.flatMap{ query ->
                    conditions!!.map{ condition -> query.whereEqualTo(SaleFields.CONDITION.fieldName, condition) }
                }
            }

            return queries
        }

        fun doQuery(query : Query) : CompletableFuture<Iterable<DocumentSnapshot>> {
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

        fun doQueries(queries : List<Query>) : CompletableFuture<Iterable<DocumentSnapshot>> {
            return listOfFuture2FutureOfList(queries.map { doQuery(it) }.toList()).thenApply { it.flatten() }
        }


        fun getBookQuery() : BookQuery {
            var bookQuery = bookDB.queryBooks()
            if (interests != null) bookQuery = bookQuery.onlyIncludeInterests(interests)
            if (title != null) bookQuery = bookQuery.searchByTitle(title)
            if (isbn != null) bookQuery = bookQuery.searchByISBN(setOf(isbn))
            return bookQuery
        }

        return if (interests == null && title == null && isbn == null) { //In this case we should not look in the book database
            doQueries(filterQuery(saleRef)).thenCompose { snapshotsToSales(it) }
        } else {
            val booksFuture = getBookQuery().getAll()
            booksFuture.thenCompose { books ->  //those are the books for which we want to find the sales
                val isbns = books.map {it.isbn}
                val isbnToBook = books.associateBy { it.isbn } //is used a cache to transform snapshots to Sales
                val saleQuery = saleRef.whereIn(SaleFields.BOOK_ISBN.fieldName, isbns)
                doQueries(filterQuery(saleQuery)).thenCompose { snapshotsToSales(it,isbnToBook) }
            }
        }
        
    }

    override fun addSale(
        bookISBN: ISBN,
        seller: User,
        price: Float,
        condition: BookCondition,
        state: SaleState,
        image: Image?
    ): CompletableFuture<Sale> {
        if(seller == LocalUser) {
            val future = CompletableFuture<Sale>()
            future.completeExceptionally(LocalUserException("Cannot add sale as LocalUser"))
            return future
        }
        val bookFuture = bookDB.getBook(bookISBN)
        return bookFuture.thenCompose { book ->
            val future = CompletableFuture<Sale>()
            if (book == null) {
                future.completeExceptionally(DatabaseException("Could not find book associated with sale : isbn = $bookISBN"))

            } else {
                val sale = Sale(book, seller, price, condition, Timestamp.now(), state, image)
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

    private fun snapshotToUser(map: HashMap<String, Any>): User {
        val uid = (map[UserFields.UID.fieldName] as Long).toInt()
        val pseudo = map[UserFields.PSEUDO.fieldName] as String

        return LoggedUser(uid, pseudo)
    }

    private fun snapshotToSale(snapshot: DocumentSnapshot, bookCache : Map<ISBN, Book>): Sale {
        val isbn = snapshot[SaleFields.BOOK_ISBN.fieldName] as ISBN
        return Sale(
            bookCache[isbn]!!, // The cache should contain the book, otherwise the call to this function is illegal
            snapshotToUser(snapshot.get(SaleFields.SELLER.fieldName)!! as HashMap<String, Any>),
            snapshot.getLong(SaleFields.PRICE.fieldName)!!.toFloat(),
            BookCondition.valueOf(snapshot.getString(SaleFields.CONDITION.fieldName)!!),
            Timestamp(snapshot.getTimestamp(SaleFields.PUBLICATION_DATE.fieldName)!!.toDate()),
            SaleState.valueOf(snapshot.getString(SaleFields.STATE.fieldName)!!),
            null
        )
    }

    private fun snapshotsToSales(snapshots : Iterable<DocumentSnapshot>, bookCache: Map<ISBN, Book> = mapOf()) : CompletableFuture<List<Sale>> {
        val missingBooks = snapshots.map { doc -> doc[SaleFields.BOOK_ISBN.fieldName] as ISBN }.toSet().minus(bookCache.keys)
        if (missingBooks.isEmpty()) {
            val sales = snapshots.map { doc ->
                snapshotToSale(doc, bookCache)
            }
            return CompletableFuture.completedFuture(sales)
        } else {
            val booksFuture = bookDB
                .queryBooks()
                .searchByISBN(missingBooks.toSet())
                .getAll()
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
            SaleFields.PUBLICATION_DATE.fieldName to sale.date,
            SaleFields.STATE.fieldName to sale.state,
            SaleFields.IMAGE.fieldName to null //TODO change this, image goes elsewhere
        )
    }

    //find the ID of a sale based on the ISBN of the book being sold, the time of the publication and the UID of the seller
    private fun getReferenceID(sale: Sale): Task<DocumentSnapshot?> {
        var query = saleRef
            .whereEqualTo(SaleFields.BOOK_ISBN.fieldName, sale.book.isbn)
            .whereEqualTo(SaleFields.PUBLICATION_DATE.fieldName, sale.date)
            .whereEqualTo(SaleFields.SELLER.fieldName +"."+UserFields.UID.fieldName, (sale.seller as LoggedUser).uid)
        return query.get().continueWith { task -> task.result.documents.firstOrNull() }

    }

}
