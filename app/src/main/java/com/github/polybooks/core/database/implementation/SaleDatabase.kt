package com.github.polybooks.core.database.implementation

import android.util.Log
import com.github.polybooks.core.*
import com.github.polybooks.core.database.DatabaseException
import com.github.polybooks.core.database.LocalUserException
import com.github.polybooks.core.database.interfaces.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleQuery

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot

import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.model.Document

import java.util.*
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

        override fun searchByISBN13(isbn13: String): SaleQuery {
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
    }

    override fun querySales(): SalesQuery {
        return SalesQuery()
    }

    private fun snapshotToBook(map: HashMap<String,Any>): Book {
        Log.d("7===================================","${map}" )
        return Book(
                map[BookFields.ISBN13.fieldName] as String,
                map[BookFields.AUTHORS.fieldName] as List<String>?,
                map[BookFields.TITLE.fieldName] as String,
                map[BookFields.EDITION.fieldName] as String?,
                map[BookFields.LANGUAGE.fieldName] as String?,
                map[BookFields.PUBLISHER.fieldName] as String?,
                map[BookFields.PUBLISHDATE.fieldName] as java.sql.Timestamp?,
                map[BookFields.FORMAT.fieldName] as String?
        )

    }

    private fun snapshotToUser(map: HashMap<String,Any>): User {
        Log.d("8===================================","${map}" )
        val b1 = (map[UserFields.UID.fieldName] as Long).toInt()
        Log.d("b1++++++++++++++++++++","${b1!!.javaClass.name}" )
        val b2 = map[UserFields.PSEUDO.fieldName] as String
        Log.d("b2++++++++++++++++++++","${b2}" )
        return LoggedUser(
                b1 as Int,
                b2
        )

    }

    private fun snapshotToSale(snapshot: DocumentSnapshot): Sale {
        Log.d("6===================================","${snapshot}" )
        //println("6=========================${snapshot}")
        val a1 = snapshotToBook(snapshot.get(SaleFields.BOOK.fieldName)!! as HashMap<String, Any>)
        Log.d("a1++++++++++++++++++++","${a1}" )
        val a2 = snapshotToUser(snapshot.get(SaleFields.SELLER.fieldName)!! as HashMap<String, Any>)
        Log.d("a2++++++++++++++++++++","${a2}" )
        val a3 = snapshot.getLong(SaleFields.PRICE.fieldName)!!.toFloat()
        Log.d("a3++++++++++++++++++++","${a3}" )
        return Sale(
                a1,//snapshotToBook(snapshot.get(SaleFields.BOOK.fieldName)!! as HashMap<String, Any>),
                a2,//snapshotToUser(snapshot.get(SaleFields.SELLER.fieldName)!! as HashMap<String, Any>),
                a3,//snapshot.getLong(SaleFields.PRICE.fieldName)!!.toFloat(),
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
            Log.d("1===================================","${task.result.documents}" )
            //println("1=========================${task.result.documents}")
            val result = task.result.documents.filter { document ->
                Log.d("0===================================","$document" )
                val s = snapshotToSale(document)
                Log.d("2===================================","LEA IS HERE" )
                Log.d("2===================================","$s" )
                //println("2=========================$s")
                s.condition == sale.condition /*&& s.seller == sale.seller */&& s.date == sale.date
            }

            result.forEach { document ->
                Log.d("3===================================","$document" )
                //println("3=========================$document")
                saleRef.document(document.id).delete()
                    .addOnFailureListener { throw DatabaseException("Could not delete $document") }
                    .addOnSuccessListener { Log.d("SaleDataBase", "Deleted: ${document}") }
            }
        }
    }
}

