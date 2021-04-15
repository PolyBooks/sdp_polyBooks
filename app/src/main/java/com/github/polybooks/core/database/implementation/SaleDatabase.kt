package com.github.polybooks.core.database.implementation

import android.util.Log
import com.github.polybooks.core.*
import com.github.polybooks.core.database.DatabaseException
import com.github.polybooks.core.database.interfaces.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleFields
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleQuery
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.model.Document
import java.util.*
import java.util.concurrent.CompletableFuture

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

        override fun onlyIncludeInterests(interests: Collection<Interest>): SaleQuery {
            if (!interests.isEmpty()) this.interests = interests.toSet()
            return this
        }

        override fun searchByTitle(title: String): SaleQuery {
            this.title = title
            return this
        }

        override fun searchByState(state: Collection<SaleState>): SaleQuery {
            if (!state.isEmpty()) this.states = state.toSet()
            return this
        }

        override fun searchByCondition(conditions: Collection<BookCondition>): SaleQuery {
            if (!conditions.isEmpty()) this.conditions = conditions.toSet()
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
            title?.let { query = query.whereEqualTo(SaleFields.TITLE.fieldName, title) }
            // interests?.let { query = query.whereIn("interests", interests!!.toList()) }
            states?.let { query = query.whereIn(SaleFields.STATE.fieldName, states!!.toList()) }
            //conditions?.let { query = query.whereIn(SaleFields.CONDITION.fieldName, conditions!!.toList()) }
            minPrice?.let { query = query.whereGreaterThanOrEqualTo(SaleFields.PRICE.fieldName, minPrice!!) }
            maxPrice?.let { query = query.whereLessThanOrEqualTo(SaleFields.PRICE.fieldName, maxPrice!!) }

            return query
        }

        internal fun getReferenceID(sale: Sale): Task<QuerySnapshot> {
            val query = querySales()
                .searchByTitle(sale.title)
                .searchByCondition(setOf(sale.condition))
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

    private fun snapshotToSale(snapshot: QueryDocumentSnapshot): Sale {
        return Sale(
            snapshot.getString(SaleFields.TITLE.fieldName)!!,
            snapshot.getLong(SaleFields.SELLER.fieldName)!!.toInt(),
            snapshot.getLong(SaleFields.PRICE.fieldName)!!.toFloat(),
            BookCondition.valueOf(snapshot.getString(SaleFields.CONDITION.fieldName)!!),
            snapshot.getTimestamp(SaleFields.PUBLICATION_DATE.fieldName)!!,
            SaleState.valueOf(snapshot.getString(SaleFields.STATE.fieldName)!!)
        )
    }

    private fun saleToDocument(sale: Sale): Any {
        return hashMapOf(
                SaleFields.TITLE.fieldName to sale.title,
                SaleFields.SELLER.fieldName to sale.seller,
                SaleFields.PRICE.fieldName to sale.price,
                SaleFields.CONDITION.fieldName to sale.condition,
                SaleFields.PUBLICATION_DATE.fieldName to sale.date,
                SaleFields.STATE.fieldName to sale.state
        )
    }

    override fun addSale(sale: Sale) {
        saleRef.add(saleToDocument(sale))
                .addOnSuccessListener { documentReference ->
                        Log.d("SaleDataBase", "DocumentSnapshot written with ID: ${documentReference.id}")}
                .addOnFailureListener {
                        // TODO: Change this to maybe only log the error
                        throw DatabaseException("Failed to insert $sale into Database")
                }
    }

    override fun deleteSale(sale: Sale) {
        Log.d("SaleDataBase", "HERE")
        println("HERE")
        SalesQuery().getReferenceID(sale).continueWith { task ->
            val result = task.result.filter { document ->
                val s = snapshotToSale(document)
                s.seller == sale.seller && s.date == sale.date
            }
            Log.d("SaleDataBase", "Results are : ${result}")
            println("Results are : ${result}")
            result.forEach { document ->
                saleRef.document(document.id).delete()
                    .addOnFailureListener { throw DatabaseException("Could not delete $document") }
                    .addOnSuccessListener { Log.d("SaleDataBase", "Deleted: ${document}") }
            }
        }
        /*
    .continueWith( new Continuation() {
    @Override
    public Task<String> then(@NonNull Task<QuerySnapshot> task) throws Exception {
        // Take the result from the first task and start the second one
        QuerySnapshot result = task.result
        return doSomething(result);
    }
}



    .addOnSuccessListener { documents->
    documents.filter { document ->
        val s = snapshotToSale(document)
        s.seller == sale.seller && s.date == sale.date
    }
}.addOnFailureListener { DatabaseException("Query could not be completed")  }
SalesQuery().getReferenceID(sale).continueWith { task ->
    task.result.documents.forEach{ document -> saleRef.document(document.id)
        .delete()
    }
}
}


SalesQuery().getReferenceID(sale).onSuccessTask { result ->
    result.documents.{ document -> saleRef.document(document.id)
        .delete()
}
SalesQuery().getReferenceID(sale).result.documents.forEach {
    document -> saleRef.document(document.id)
        .delete()
        .addOnFailureListener { DatabaseException("Could not delete $document") }
        .addOnSuccessListener {  Log.d("SaleDataBase", "Deleted: ${document}")} }
}*/
    }
}

