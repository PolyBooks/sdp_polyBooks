package com.github.polybooks.core.database.implementation

import android.util.Log
import com.github.polybooks.core.*
import com.github.polybooks.core.database.DatabaseException
import com.github.polybooks.core.database.LocalUserException
import com.github.polybooks.core.database.interfaces.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleSettings
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleQuery

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*

import java.util.concurrent.CompletableFuture
import kotlin.collections.HashMap

class SaleDatabase : SaleDatabase {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val saleRef: CollectionReference = db.collection(getCollectionName())

    inner class SalesQuery : SaleQuery {

        private var isbn13: String? = null
        private var title: String? = null

        private var interests: Set<Interest>? = null
        private var states: Set<SaleState>? = null
        private var conditions: Set<BookCondition>? = null

        private var minPrice: Float? = null
        private var maxPrice: Float? = null

        override fun onlyIncludeInterests(interests: Set<Interest>): SaleQuery {
            if (interests.isNotEmpty()) this.interests = interests
            return this
        }

        override fun searchByTitle(title: String): SaleQuery {
            this.title = title
            return this
        }

        override fun searchByState(state: Set<SaleState>): SaleQuery {
            if (state.isNotEmpty()) this.states = state
            return this
        }

        override fun searchByCondition(condition: Set<BookCondition>): SaleQuery {
            if (condition.isNotEmpty()) this.conditions = condition
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
            TODO("Not yet implemented")
        }

        override fun searchByISBN(isbn13: String): SaleQuery {
            this.isbn13 = isbn13
            return this
        }

        private fun getQuery() : Query {
            var query: Query = saleRef

            // TODO: add these when necessary
            // isbn13?.let { query = query.whereEqualTo("isbn", isbn13) }
            // TODO: fix this for title
            // FieldPath.of(SaleFields.BOOK.fieldName, BookFields.TITLE.fieldName)
            title?.let { query = query.whereEqualTo(SaleFields.BOOK.fieldName + "." + BookFields.TITLE.fieldName, title) }//SaleFields.BOOK.fieldName[BookFields.TITLE.fieldName]
            // interests?.let { query = query.whereIn("interests", interests!!.toList()) }
            states?.let { query = query.whereIn(SaleFields.STATE.fieldName, states!!.toList()) }
            // TODO: fix this with whereIN
            // https://stackoverflow.com/questions/45419272/firebase-how-to-structure-for-multiple-where-in-query
            // Or find a way to this in client
            conditions?.let { query = query.whereIn(SaleFields.CONDITION.fieldName, conditions!!.toList()) }
            minPrice?.let { query = query.whereGreaterThanOrEqualTo(SaleFields.PRICE.fieldName, minPrice!!) }
            maxPrice?.let { query = query.whereLessThanOrEqualTo(SaleFields.PRICE.fieldName, maxPrice!!) }

            return query
        }

        internal fun getReferenceID(sale: Sale): Task<QuerySnapshot> {
            val query = querySales()
                .searchByTitle(sale.book.title)
                .searchByState(setOf(sale.state))
                .searchByPrice(sale.price,sale.price) as SalesQuery
            return query.getQuery().get()

        }

        override fun getAll(): CompletableFuture<List<Sale>> {
            val future: CompletableFuture<List<Sale>> = CompletableFuture()

            getQuery()
                .get()
                .addOnSuccessListener { documents ->
                    future.complete(documents.map { document ->
                        snapshotToSale(document)
                    })
                }
                .addOnFailureListener {
                    future.completeExceptionally(
                        DatabaseException("Query could not be completed")
                    )
                }

            return future
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

            // FIXME ignoring page number for now
            getQuery()
                .limit(n.toLong())
                .get()
                .addOnSuccessListener { documents ->
                    future.complete(documents.map { document ->
                        snapshotToSale(document)
                    })
                }
                .addOnFailureListener {
                    future.completeExceptionally(
                        DatabaseException("Query could not be completed")
                    )
                }

            return future
        }

        override fun getCount(): CompletableFuture<Int> {
            val future: CompletableFuture<Int> = CompletableFuture()

            getQuery()
                .get()
                .addOnSuccessListener { documents ->
                    future.complete(documents.fold(0){ acc, _ -> acc + 1 })
                }
                .addOnFailureListener {
                    future.completeExceptionally(
                        DatabaseException("Query count could not be completed")
                    )
                }

            return future
        }

        override fun getSettings(): SaleSettings {
            return SaleSettings(
                    SaleOrdering.DEFAULT, //TODO change when ordering implemented
                    isbn13,
                    title,
                    interests,
                    states,
                    conditions,
                    minPrice,
                    maxPrice
            )
        }

        override fun fromSettings(settings: SaleSettings): SaleQuery {
            isbn13 = settings.isbn
            title = settings.title

            if(settings.interests == null) interests == null
            else onlyIncludeInterests(settings.interests)

            if(settings.states == null) states == null
            else searchByState(settings.states)

            if(settings.conditions == null) conditions
            else searchByCondition(settings.conditions)

            minPrice = settings.minPrice
            maxPrice = settings.maxPrice

            return this
        }
    }

    override fun querySales(): SalesQuery {
        return SalesQuery()
    }

    private fun snapshotToBook(map: HashMap<String, Any>): Book {
        return Book(
            map[BookFields.ISBN.fieldName] as String,
            map[BookFields.AUTHORS.fieldName] as List<String>?,
            map[BookFields.TITLE.fieldName] as String,
            map[BookFields.EDITION.fieldName] as String?,
            map[BookFields.LANGUAGE.fieldName] as String?,
            map[BookFields.PUBLISHER.fieldName] as String?,
            map[BookFields.PUBLISHDATE.fieldName] as java.sql.Timestamp?,
            map[BookFields.FORMAT.fieldName] as String?
        )

    }

    private fun snapshotToUser(map: HashMap<String, Any>): User {
        val uid = (map[UserFields.UID.fieldName] as Long).toInt()
        val pseudo = map[UserFields.PSEUDO.fieldName] as String

        return LoggedUser(uid, pseudo)
    }

    private fun snapshotToSale(snapshot: DocumentSnapshot): Sale {

        return Sale(
            snapshotToBook(snapshot.get(SaleFields.BOOK.fieldName)!! as HashMap<String, Any>),
            snapshotToUser(snapshot.get(SaleFields.SELLER.fieldName)!! as HashMap<String, Any>),
            snapshot.getLong(SaleFields.PRICE.fieldName)!!.toFloat(),
            BookCondition.valueOf(snapshot.getString(SaleFields.CONDITION.fieldName)!!),
            Timestamp(snapshot.getTimestamp(SaleFields.PUBLICATION_DATE.fieldName)!!.toDate()),
            SaleState.valueOf(snapshot.getString(SaleFields.STATE.fieldName)!!),
            null
        )
    }

    private fun saleToDocument(sale: Sale): Any {
        return hashMapOf(
            SaleFields.BOOK.fieldName to sale.book,
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

    override fun deleteSale(sale: Sale) {
        if(sale.seller == LocalUser)
            throw LocalUserException("Cannot add sale as LocalUser")
        SalesQuery().getReferenceID(sale).continueWith { task ->
            val result = task.result.documents.filter { document ->
                val s = snapshotToSale(document)
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

