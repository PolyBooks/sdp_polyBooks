package com.github.polybooks.database

import androidx.core.os.persistableBundleOf
import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.implementation.DummySalesQuery
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleQuery
import com.github.polybooks.core.database.interfaces.SaleSettings
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.sql.Timestamp

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DummySalesQueryTest {

    val query : SaleQuery = DummySalesQuery()

    val default_sale: List<Sale> = listOf(
            Sale("Book1", 1, 23.00f, BookCondition.GOOD, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
            Sale("Book2", 1, 24.55f, BookCondition.GOOD, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
            Sale("Book3", 4, 25.00f, BookCondition.NEW, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
            Sale("Book4", 6, 26.00f, BookCondition.GOOD, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
            Sale("Book5", 6, 21.00f, BookCondition.WORN, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.CONCLUDED),
            Sale("Book6", 9, 29.00f, BookCondition.GOOD, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
            Sale("Book7", 8, 23.00f, BookCondition.GOOD, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
            Sale("Book8", 5, 23.66f, BookCondition.NEW, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
            Sale("Book9", 9, 25.00f, BookCondition.GOOD, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.RETRACTED),
    )


    @Test
    fun basicFunctionsWork() {
        val q1 = query.getCount()
        val q2 = query.getN(0,0)
        val q3 = query.getAll()
        assertEquals(default_sale.size, q1.get())
        assertEquals(default_sale, q2.get())
        assertEquals(default_sale, q3.get())
    }

    @Test
    fun unimplementedFunctionsWork() {
        val q1 = query.onlyIncludeInterests(emptySet()).getAll()
        val q2 = query.searchByTitle("").getAll()
        val q3 = query.searchByISBN13("Book1").getAll()

        val q3Result = listOf(
                Sale("Book1", 1, 23.00f, BookCondition.GOOD, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE))

        assertEquals(default_sale, q1.get())
        assertEquals(default_sale, q2.get())
        assertEquals(q3Result, q3.get())
    }

    @Test
    fun implementedFunctionsWork() {
        val q1 = query.searchByState( setOf(SaleState.CONCLUDED, SaleState.RETRACTED)).getAll()
        val q2 = query.searchByCondition(setOf(BookCondition.WORN, BookCondition.NEW)).getAll()
        val q3 = query.searchByPrice(23f, 24.9f).getAll()


        val q1Result = listOf(
                Sale("Book5", 6, 21.00f, BookCondition.WORN, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.CONCLUDED),
                Sale("Book9", 9, 25.00f, BookCondition.GOOD, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.RETRACTED),
                )
        val q2Result = listOf(
                Sale("Book3", 4, 25.00f, BookCondition.NEW, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
                Sale("Book5", 6, 21.00f, BookCondition.WORN, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.CONCLUDED),
                Sale("Book8", 5, 23.66f, BookCondition.NEW, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
                )
        val q3Result = listOf(
                Sale("Book1", 1, 23.00f, BookCondition.GOOD, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
                Sale("Book2", 1, 24.55f, BookCondition.GOOD, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
                Sale("Book7", 8, 23.00f, BookCondition.GOOD, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE),
                Sale("Book8", 5, 23.66f, BookCondition.NEW, Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!.time), SaleState.ACTIVE)
        )
        assertEquals(q1Result, q1.get())
        assertEquals(q2Result, q2.get())
        assertEquals(q3Result, q3.get())
    }

    @Test
    fun settingsTest() {
        val settingsRes = SaleSettings(SaleOrdering.DEFAULT,
                null, null, null, null, null,null, null)
        val settings = query.searchByMaxPrice(10.0f).getSettings()
        assertEquals(settingsRes, settings)
    }

    @Test
    fun getSettingsAfterFromSettingsShouldCorrespond() {
        val settingsRes = SaleSettings(SaleOrdering.DEFAULT,
                null, null, null, null, null,null, null)

        val settings = query.fromSettings(settingsRes).getSettings()
        assertEquals(settingsRes, settings)
    }
}