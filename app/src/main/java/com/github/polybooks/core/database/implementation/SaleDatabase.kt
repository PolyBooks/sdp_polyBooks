package com.github.polybooks.core.database.implementation

import com.github.polybooks.core.*
import com.github.polybooks.core.database.DatabaseException
import com.github.polybooks.core.database.interfaces.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleQuery
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.*
import java.util.concurrent.CompletableFuture

class SaleDatabase : SaleDatabase {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val saleRef: CollectionReference = db.collection(getCollectionName())

    inner class SalesQuery : SaleQuery {
        /*
        private var isbn13: Optional<String> = Optional.empty()
        private var title: Optional<String> = Optional.empty()

        private var interests: Optional<Set<Interest>> = Optional.empty()
        private var states: Optional<Set<SaleState>> = Optional.empty()
        private var conditions: Optional<Set<BookCondition>> = Optional.empty()

        private var minPrice: Float = 0f
        private var maxPrice: Optional<Float> = Optional.empty()
        */
        private var isbn13: String? = null
        private var title: String? = null

        private var interests: Set<Interest>? = null
        private var states: Set<SaleState>?  = null
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
            this.searchByMinPrice(min)
            return this.searchByMaxPrice(max)
        }

        override fun withOrdering(ordering: SaleOrdering): SaleQuery {
            TODO("Not yet implemented")
        }

        override fun searchByISBN13(isbn13: String): SaleQuery {
            this.isbn13 = isbn13
            return this
        }

        private fun getQuery() : Query {
            var query :Query = saleRef

            isbn13?.let { query = query.whereEqualTo("isbn", isbn13) }
            title?.let { query = query.whereEqualTo("book", title) }
            interests?.let { query = query.whereIn("interests", interests!!.toList()) }
            states?.let { query = query.whereIn("interests", states!!.toList()) }
            conditions?.let { query = query.whereIn("interests", conditions!!.toList()) }
            minPrice?.let { query.whereGreaterThanOrEqualTo("price", minPrice!!) }
            maxPrice?.let { query.whereLessThanOrEqualTo("price", maxPrice!!) }

            return query
        }

        private fun snapshotToSale(snapshot: QueryDocumentSnapshot): Sale {
            return Sale(
                    snapshot.getString("book")!!,
                    snapshot.getLong("seller")!!.toInt(),
                    snapshot.getLong("price")!!.toFloat(),
                    BookCondition.valueOf(snapshot.getString("condition")!!),
                    // FIXME Maybe rather store timestamps?
                    Date(snapshot.getString("publicationDate")!!),
                    SaleState.valueOf(snapshot.getString("state")!!)
            )
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

        // worked 3h40 for now

        override fun getN(n: Int, page: Int): CompletableFuture<List<Sale>> {
            TODO("Not yet implemented")
        }

        override fun getCount(): CompletableFuture<Int> {
            TODO("Not yet implemented")
        }
    }

    override fun querySales(): SalesQuery {
        return SalesQuery()
    }
}

