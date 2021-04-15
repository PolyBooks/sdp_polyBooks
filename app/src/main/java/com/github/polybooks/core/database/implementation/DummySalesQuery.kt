package com.github.polybooks.core.database.implementation

import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.interfaces.BookSettings
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleQuery
import com.github.polybooks.core.database.interfaces.SaleSettings
import com.google.firebase.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.CompletableFuture

val formatString = "yyyy-mm-dd"
val format : DateFormat = SimpleDateFormat(formatString)
@RequiresApi(Build.VERSION_CODES.O)
val default_sale: List<Sale> = listOf(
        Sale("Book1", 1, 23.00f, BookCondition.GOOD, Timestamp(format.parse("2016-05-05")!!), SaleState.ACTIVE),
        Sale("Book2", 1, 24.55f, BookCondition.GOOD, Timestamp(format.parse("2016-05-05")!!), SaleState.ACTIVE),
        Sale("Book3", 4, 25.00f, BookCondition.NEW, Timestamp(format.parse("2016-05-05")!!), SaleState.ACTIVE),
        Sale("Book4", 6, 26.00f, BookCondition.GOOD, Timestamp(format.parse("2016-05-05")!!), SaleState.ACTIVE),
        Sale("Book5", 6, 21.00f, BookCondition.WORN, Timestamp(format.parse("2016-05-05")!!), SaleState.CONCLUDED),
        Sale("Book6", 9, 29.00f, BookCondition.GOOD, Timestamp(format.parse("2016-05-05")!!), SaleState.ACTIVE),
        Sale("Book7", 8, 23.00f, BookCondition.GOOD, Timestamp(format.parse("2016-05-05")!!), SaleState.ACTIVE),
        Sale("Book8", 5, 23.66f, BookCondition.NEW, Timestamp(format.parse("2016-05-05")!!), SaleState.ACTIVE),
        Sale("Book9", 9, 25.00f, BookCondition.GOOD, Timestamp(format.parse("2016-05-05")!!), SaleState.RETRACTED),
)

/**
 * Default sales query used for tests
 * @property sale the list of sales you query
 */
class DummySalesQuery(private val sale: List<Sale> = default_sale) : SaleQuery {


    override fun onlyIncludeInterests(interests: Collection<Interest>): SaleQuery {
        return DummySalesQuery()
    }

    override fun searchByTitle(title: String): SaleQuery {
        return DummySalesQuery()
    }

    override fun searchByState(state: Collection<SaleState>): SaleQuery {
        return DummySalesQuery()
    }

    override fun searchByCondition(conditions: Collection<BookCondition>): SaleQuery {
        TODO("Not yet implemented")
    }

    override fun searchByMinPrice(min: Float): SaleQuery {
        TODO("Not yet implemented")
    }

    override fun searchByMaxPrice(max: Float): SaleQuery {
        TODO("Not yet implemented")
    }

    override fun searchByPrice(min: Float, max: Float): SaleQuery {
        return DummySalesQuery()
    }

    override fun withOrdering(ordering: SaleOrdering): SaleQuery {
        return DummySalesQuery()
    }

    override fun searchByISBN13(isbn13: String): SaleQuery {
        return DummySalesQuery()
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

    override fun getSettings(): SaleSettings {
        return SaleSettings(SaleOrdering.DEFAULT,
                null, null, null, null, null,null, null)
    }

    override fun fromSettings(settings: BookSettings): SaleQuery {
        return DummySalesQuery()
    }
}