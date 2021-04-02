package com.github.polybooks.core.database.implementation

import android.os.Build
import android.os.Parcelable
import android.os.SystemClock
import androidx.annotation.RequiresApi
import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState

import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleQuery
import java.sql.Timestamp


import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CompletableFuture

val formatString = "yyyy-mm-dd"
val format : DateFormat = SimpleDateFormat(formatString)
@RequiresApi(Build.VERSION_CODES.O)
val default_sale: List<Sale> = listOf(
        Sale("Book1", 1, 23.00f, BookCondition.GOOD, Timestamp(format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
        Sale("Book2", 1, 24.55f, BookCondition.GOOD, Timestamp(format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
        Sale("Book3", 4, 25.00f, BookCondition.NEW, Timestamp(format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
        Sale("Book4", 6, 26.00f, BookCondition.GOOD, Timestamp(format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
        Sale("Book5", 6, 21.00f, BookCondition.WORN, Timestamp(format.parse("2016-05-05")!!.time), SaleState.CONCLUDED),
        Sale("Book6", 9, 29.00f, BookCondition.GOOD, Timestamp(format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
        Sale("Book7", 8, 23.00f, BookCondition.GOOD, Timestamp(format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
        Sale("Book8", 5, 23.66f, BookCondition.NEW, Timestamp(format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
        Sale("Book9", 9, 25.00f, BookCondition.GOOD, Timestamp(format.parse("2016-05-05")!!.time), SaleState.RETRACTED),
)

/**
 * Default sales query used for tests
 * @property sale the list of sales you query
 */
class DummySalesQuery(private val sale: List<Sale> = default_sale) : SaleQuery{


    override fun onlyIncludeInterests(interests: Set<Interest>): SaleQuery {
//        TODO("Not yet implemented")
        println("Search by interest not correct yet")
        return DummySalesQuery(sale)
    }

    override fun searchByTitle(title: String): SaleQuery {
//        TODO("Not yet implemented")
//        return DummySalesQuery()
        println("Search by title not correct yet")
        return DummySalesQuery(sale)
    }

    override fun searchByState(state: Set<SaleState>): SaleQuery {
        return DummySalesQuery(sale.filter { sale -> sale.state in state })
//        return DummySalesQuery()
    }


    override fun searchByCondition(condition: Set<BookCondition>): SaleQuery {
        return DummySalesQuery(sale.filter { sale -> sale.condition in condition })
    }


    override fun searchByMinPrice(min: Float): SaleQuery {
        return DummySalesQuery(sale.filter { sale -> sale.price >= min })
    }

    override fun searchByMaxPrice(max: Float): SaleQuery {
        return DummySalesQuery(sale.filter { sale -> max <= sale.price })
    }


    override fun searchByPrice(min: Float, max: Float): SaleQuery {
        return searchByMaxPrice(max).searchByMinPrice(min)
    }

    override fun withOrdering(ordering: SaleOrdering): SaleQuery {
//        TODO("Not yet implemented")
        return DummySalesQuery(sale)
    }

    override fun searchByISBN13(isbn13: String): SaleQuery {
//        TODO("Not yet implemented")
        return DummySalesQuery(sale.filter { sale -> sale.title == isbn13 })
    }

    override fun getAll(): CompletableFuture<List<Sale>> {
        return CompletableFuture.supplyAsync {
            SystemClock.sleep(2000)
            sale
        }
    }

    override fun getN(n: Int, page: Int): CompletableFuture<List<Sale>> {
        TODO("Not yet implemented")
    }

    override fun getCount(): CompletableFuture<Int> {
        TODO("Not yet implemented")
    }
}