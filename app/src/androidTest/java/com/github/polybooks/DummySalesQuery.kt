package com.github.polybooks

import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.SaleOrdering
import com.github.polybooks.core.database.SaleQuery
import java.util.*
import java.util.concurrent.CompletableFuture

private val default_sale: List<Sale> = listOf(
        Sale("Book1",1, 23.00f, BookCondition.GOOD, Date("2016-05-05"), SaleState.ACTIVE),
        Sale("Book2",1, 24.55f, BookCondition.GOOD, Date("2016-05-05"), SaleState.ACTIVE),
        Sale("Book3",4, 25.00f, BookCondition.NEW, Date("2016-05-05"), SaleState.ACTIVE),
        Sale("Book4",6, 26.00f, BookCondition.GOOD, Date("2016-05-05"), SaleState.ACTIVE),
        Sale("Book5",6, 21.00f, BookCondition.WORN, Date("2016-05-05"), SaleState.CONCLUDED),
        Sale("Book6",9, 29.00f, BookCondition.GOOD, Date("2016-05-05"), SaleState.ACTIVE),
        Sale("Book7",8, 23.00f, BookCondition.GOOD, Date("2016-05-05"), SaleState.ACTIVE),
        Sale("Book8",5, 23.66f, BookCondition.NEW, Date("2016-05-05"), SaleState.ACTIVE),
        Sale("Book9",9, 25.00f, BookCondition.GOOD, Date("2016-05-05"), SaleState.RETRACTED),
)

class DummySalesQuery(private val sale: List<Sale> = default_sale) : SaleQuery {


    override fun onlyIncludeInterests(interests: Collection<Interest>): SaleQuery {
        TODO("Not yet implemented")
    }

    override fun searchByTitle(title: String): SaleQuery {
        TODO("Not yet implemented")
    }

    override fun searchByState(state: Collection<SaleState>): SaleQuery {
        return DummySalesQuery(sale.filter { sale -> sale.condition in state})
    }

    override fun searchByCondition(condition: Collection<BookCondition>): SaleQuery {
        TODO("Not yet implemented")
    }

    override fun searchByPrice(min: Float, max: Float): SaleQuery {
        TODO("Not yet implemented")
    }

    override fun withOrdering(ordering: SaleOrdering): SaleQuery {
        TODO("Not yet implemented")
    }

    override fun searchByISBN13(isbn13: String): SaleQuery {
        TODO("Not yet implemented")
    }

    override fun getAll(): CompletableFuture<List<Sale>> {
        return CompletableFuture.supplyAsync {
            Thread.sleep(1L)
            println("HERE =========================")
            sale
            }
        }
    //CompletableFuture<List<Sale>> {()-> }



    override fun getN(n: Int, page: Int): CompletableFuture<List<Sale>> {
        TODO("Not yet implemented")
    }

    override fun getCount(): CompletableFuture<Int> {
        TODO("Not yet implemented")
    }
}