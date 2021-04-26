package com.github.polybooks.core.database.implementation

import android.util.Log
import com.github.polybooks.core.*
import com.github.polybooks.core.database.DatabaseException
import com.github.polybooks.core.database.LocalUserException
import com.github.polybooks.core.database.interfaces.*
import com.github.polybooks.core.database.interfaces.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleOrdering.*
import com.github.polybooks.utils.listOfFuture2FutureOfList
import com.github.polybooks.utils.url2json

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import java.lang.IllegalStateException

import java.util.concurrent.CompletableFuture
import kotlin.collections.HashMap

class SaleDatabase : SaleDatabase {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val saleRef: CollectionReference = db.collection(getCollectionName())
    private val bookDB: BookDatabase = FBBookDatabase(db, OLBookDatabase{ url2json(it) })

    inner class SalesQuery : SaleQuery {

        private var isbn: ISBN? = null
        private var title: String? = null

        private var interests: Set<Interest>? = null
        private var states: Set<SaleState>? = null
        private var conditions: Set<BookCondition>? = null

        private var minPrice: Float? = null
        private var maxPrice: Float? = null

        private var ordering : SaleOrdering = DEFAULT

        override fun onlyIncludeInterests(interests: Set<Interest>): SaleQuery {
            if (interests.isNotEmpty()) this.interests = interests
            else this.interests = null
            return this
        }

        override fun searchByTitle(title: String): SaleQuery {
            this.title = title
            return this
        }

        override fun searchByState(state: Set<SaleState>): SaleQuery {
            if (state.isNotEmpty()) this.states = state
            else this.states = null
            return this
        }

        override fun searchByCondition(condition: Set<BookCondition>): SaleQuery {
            if (condition.isNotEmpty()) this.conditions = condition
            else this.conditions = null
            return this
        }

        override fun searchByMinPrice(min: Float): SaleQuery {
            this.minPrice = min
            return this
        }

        override fun searchByMaxPrice(max: Float): SaleQuery {
            this.maxPrice = max
            return this
        }

        override fun searchByPrice(min: Float, max: Float): SaleQuery {
            return this.searchByMinPrice(min).searchByMaxPrice(max)
        }

        override fun withOrdering(ordering: SaleOrdering): SaleQuery {
            this.ordering = ordering
            return this
        }

        override fun searchByISBN(isbn: ISBN): SaleQuery {
            this.isbn = isbn
            return this
        }

        private fun filterQuery(q: Query) : Query {
            var query: Query = q

            states?.let { query = query.whereIn(SaleFields.STATE.fieldName, states!!.toList()) }
            conditions?.let { query = query.whereIn(SaleFields.CONDITION.fieldName, conditions!!.toList()) }
            minPrice?.let { query = query.whereGreaterThanOrEqualTo(SaleFields.PRICE.fieldName, minPrice!!) }
            maxPrice?.let { query = query.whereLessThanOrEqualTo(SaleFields.PRICE.fieldName, maxPrice!!) }

            return query
        }

        private fun doQuery(query : Query) : CompletableFuture<QuerySnapshot> {
            val future = CompletableFuture<QuerySnapshot>()
            query.get()
                .addOnSuccessListener { documents ->
                    future.complete(documents)
                }
                .addOnFailureListener {
                    future.completeExceptionally(
                        DatabaseException("Query could not be completed")
                    )
                }
            return future
        }

        private fun getBookQuery() : BookQuery {
            var bookQuery = bookDB.queryBooks()
            if (interests != null) bookQuery.onlyIncludeInterests(interests!!)
            if (title != null) bookQuery.searchByTitle(title!!)
            if (isbn != null) bookQuery.searchByISBN(setOf(isbn!!))
            return bookQuery
        }

        override fun getAll(): CompletableFuture<List<Sale>> {

            if (interests == null && title == null && isbn == null) { //In this case we should not look in the book database
                return doQuery(filterQuery(saleRef)).thenCompose { snapshotsToSales(it) }
            } else {
                val booksFuture = getBookQuery().getAll()
                return booksFuture.thenCompose { books ->  //those are the books for which we want to find the sales
                    val isbns = books.map {it.isbn}
                    val isbnToBook = books.associateBy { it.isbn } //is used a cache to transform snapshots to Sales
                    val saleQuery = saleRef.whereIn(SaleFields.BOOK_ISBN.fieldName, isbns)
                    doQuery(filterQuery(saleQuery)).thenCompose { snapshotsToSales(it,isbnToBook) }
                }
            }
        }

        override fun getN(n: Int, page: Int): CompletableFuture<List<Sale>> {
            val future: CompletableFuture<List<Sale>> = CompletableFuture()

            if (n < 0 || page < 0) {
                future.completeExceptionally(
                    IllegalArgumentException(
                        if (n < 0) "Cannot return a negative ($n) number of results"
                        else "Cannot return a negative ($page) page number"
                    )
                )
                return future
            }

            // Firebase cannot handle querying 0 element
            if (n == 0) {
                future.complete(emptyList())
                return future
            }

            TODO("Not Implemented")

            return future
        }

        override fun getCount(): CompletableFuture<Int> {
            TODO("Not Implemented")
        }

        override fun getSettings(): SaleSettings {
            return SaleSettings(
                ordering,
                isbn,
                title,
                interests,
                states,
                conditions,
                minPrice,
                maxPrice
            )
        }

        override fun fromSettings(settings: SaleSettings): SaleQuery {
            isbn = settings.isbn
            title = settings.title
            interests = settings.interests
            states = settings.states
            conditions = settings.conditions
            minPrice = settings.minPrice
            maxPrice = settings.maxPrice
            ordering = settings.ordering

            return this
        }
    }

    override fun querySales(): SalesQuery {
        return SalesQuery()
    }

    private fun snapshotToUser(map: HashMap<String, Any>): User {
        val uid = (map[UserFields.UID.fieldName] as Long).toInt()
        val pseudo = map[UserFields.PSEUDO.fieldName] as String

        return LoggedUser(uid, pseudo)
    }

    private fun snapshotToSale(snapshot: DocumentSnapshot, bookCache : Map<ISBN, Book> = mapOf()): Sale {
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

    private fun snapshotsToSales(snapshots : QuerySnapshot, bookCache: Map<ISBN, Book> = mapOf()) : CompletableFuture<List<Sale>> {
        val missingBooks = snapshots.map { doc -> doc[SaleFields.BOOK_ISBN.fieldName] as ISBN }.minus(bookCache.keys)
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

    override fun addSale(sale: Sale) {
        if(sale.seller == LocalUser)
            throw LocalUserException("Cannot add sale as LocalUser")
        saleRef.add(saleToDocument(sale))
                .addOnSuccessListener { documentReference ->
                    Log.d("SaleDataBase", "DocumentSnapshot written with ID: ${documentReference.id}")}
                .addOnFailureListener {
                    // TODO: Change this to maybe only log the error
                    throw DatabaseException("Failed to insert $sale into Database")
                }
    }

    //find the ID of a sale based on the ISBN of the book being sold, the time of the publication and the UID of the seller
    private fun getReferenceID(sale: Sale): Task<QuerySnapshot> {
        var query = saleRef
            .whereEqualTo(SaleFields.BOOK_ISBN.fieldName, sale.book.isbn)
            .whereEqualTo(SaleFields.PUBLICATION_DATE.fieldName, sale.date)
            .whereEqualTo(SaleFields.SELLER.fieldName +"."+UserFields.UID.fieldName, (sale.seller as LoggedUser).uid)
        return query.get()

    }

    override fun deleteSale(sale: Sale) {
        if(sale.seller == LocalUser)
            throw LocalUserException("Cannot add sale as LocalUser")
        getReferenceID(sale).continueWith { task ->
            val result = task.result.documents.filter { document ->
                val s = snapshotToSale(document, mapOf())
                s.condition == sale.condition /*&& s.seller == sale.seller */&& s.date == sale.date
            }

            result.forEach { document ->
                saleRef.document(document.id).delete()
                    .addOnFailureListener { throw DatabaseException("Could not delete $document") }
                    .addOnSuccessListener { Log.d("SaleDataBase", "Deleted: ${document}") }
            }
        }
    }
}

