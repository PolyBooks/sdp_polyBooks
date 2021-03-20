package com.github.polybooks.core.database

import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.SaleOrdering
import com.github.polybooks.core.database.SaleQuery
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CompletableFuture

val formatString = "yyyy-mm-dd"
val format : DateFormat = SimpleDateFormat(formatString)
@RequiresApi(Build.VERSION_CODES.O)
val default_sale: List<Sale> = listOf(
        Sale("Book1", 1, 23.00f, BookCondition.GOOD, format.parse("2016-05-05")!!, SaleState.ACTIVE),
        Sale("Book2", 1, 24.55f, BookCondition.GOOD, format.parse("2016-05-05")!!, SaleState.ACTIVE),
        Sale("Book3", 4, 25.00f, BookCondition.NEW, format.parse("2016-05-05")!!, SaleState.ACTIVE),
        Sale("Book4", 6, 26.00f, BookCondition.GOOD, format.parse("2016-05-05")!!, SaleState.ACTIVE),
        Sale("Book5", 6, 21.00f, BookCondition.WORN, format.parse("2016-05-05")!!, SaleState.CONCLUDED),
        Sale("Book6", 9, 29.00f, BookCondition.GOOD, format.parse("2016-05-05")!!, SaleState.ACTIVE),
        Sale("Book7", 8, 23.00f, BookCondition.GOOD, format.parse("2016-05-05")!!, SaleState.ACTIVE),
        Sale("Book8", 5, 23.66f, BookCondition.NEW, format.parse("2016-05-05")!!, SaleState.ACTIVE),
        Sale("Book9", 9, 25.00f, BookCondition.GOOD, format.parse("2016-05-05")!!, SaleState.RETRACTED),
)

/**
 * Default sales query used for tests
 * @property sale the list of sales you query
 */
class DummySalesQuery(private val sale: List<Sale> = default_sale) : SaleQuery {


    override fun onlyIncludeInterests(interests: Collection<Interest>): SaleQuery {
        TODO("Not yet implemented")
    }

    override fun searchByTitle(title: String): SaleQuery {
        TODO("Not yet implemented")
    }

    override fun searchByState(state: Collection<SaleState>): SaleQuery {
        return DummySalesQuery(sale.filter { sale -> sale.state in state })
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