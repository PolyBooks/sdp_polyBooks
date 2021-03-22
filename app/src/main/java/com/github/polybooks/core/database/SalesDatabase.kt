package com.github.polybooks.core.database

import com.github.polybooks.core.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.*
import java.util.concurrent.CompletableFuture

class SalesDatabase : SaleDatabase {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val saleRef: CollectionReference = db.collection(getCollectionName())

    inner class SalesQuery : SaleQuery {
        private var isbn13: Optional<String> = Optional.empty()
        private var title: Optional<String> = Optional.empty()

        private var interests: Optional<Set<Interest>> = Optional.empty()
        private var states: Optional<Set<SaleState>> = Optional.empty()
        private var conditions: Optional<Set<BookCondition>> = Optional.empty()

        private var minPrice: Float = 0f
        private var maxPrice: Optional<Float> = Optional.empty()


        override fun onlyIncludeInterests(interests: Collection<Interest>): SaleQuery {
            if (!interests.isEmpty()) this.interests = Optional.of(interests.toSet())
            return this
        }

        override fun searchByTitle(title: String): SaleQuery {
            this.title = Optional.of(title)
            return this
        }

        override fun searchByState(state: Collection<SaleState>): SaleQuery {
            if (!state.isEmpty()) this.states = Optional.of(state.toSet())
            return this
        }

        override fun searchByCondition(conditions: Collection<BookCondition>): SaleQuery {
            if (!conditions.isEmpty()) this.conditions = Optional.of(conditions.toSet())
            return this
        }

        override fun searchByMinPrice(min: Float): SaleQuery {
            this.minPrice = min
            return this
        }

        override fun searchByMaxPrice(max: Float): SaleQuery {
            this.maxPrice = Optional.of(max)
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
            this.isbn13 = Optional.of(isbn13)
            return this
        }

        override fun getAll(): CompletableFuture<List<Sale>> {
            fun snapshotToSale(snapshot: QueryDocumentSnapshot): Sale {
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


            if (isbn13.isPresent) {

            } else if (title.isPresent) {

            }

            val future: CompletableFuture<List<Sale>> = CompletableFuture()
            saleRef
                .whereEqualTo("book", "Physics for dummies")
                .get()
                .addOnSuccessListener { documents ->
                    future.complete(documents.map { document ->
                        snapshotToSale(document)
                    })
                }
                .addOnFailureListener {
                    future.completeExceptionally(
                        DatabaseException("Couldn't reach database for query 'title' = 'TODO fill in SD.kt'")
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

