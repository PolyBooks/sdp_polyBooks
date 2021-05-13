package com.github.polybooks.database

import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.LocalUser
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.utils.anonymousBook
import com.google.firebase.Timestamp
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DummySalesQueryTest {

    val query : SaleQuery = DummySalesQuery()

    val default_sale: List<Sale> = listOf(
            Sale( anonymousBook("Book1"), LocalUser, 23.00f, BookCondition.GOOD, com.google.firebase.Timestamp(
                format.parse("2016-05-05")!!), SaleState.ACTIVE, null),
            Sale(anonymousBook("Book2"), LocalUser, 24.55f, BookCondition.GOOD, com.google.firebase.Timestamp(
                format.parse("2016-05-05")!!), SaleState.ACTIVE, null),
            Sale(anonymousBook("Book3"), LocalUser, 25.00f, BookCondition.NEW, com.google.firebase.Timestamp(
                format.parse("2016-05-05")!!), SaleState.ACTIVE, null),
            Sale(anonymousBook("Book4"), LocalUser, 26.00f, BookCondition.GOOD, com.google.firebase.Timestamp(
                format.parse("2016-05-05")!!), SaleState.ACTIVE, null),
            Sale(anonymousBook("Book5"), LocalUser, 21.00f, BookCondition.WORN, com.google.firebase.Timestamp(
                format.parse("2016-05-05")!!), SaleState.CONCLUDED, null),
            Sale(anonymousBook("Book6"), LocalUser, 29.00f, BookCondition.GOOD, com.google.firebase.Timestamp(
                format.parse("2016-05-05")!!), SaleState.ACTIVE, null),
            Sale(anonymousBook("Book7"), LocalUser, 23.00f, BookCondition.GOOD, com.google.firebase.Timestamp(
                format.parse("2016-05-05")!!), SaleState.ACTIVE, null),
            Sale(anonymousBook("Book8"), LocalUser, 23.66f, BookCondition.NEW, com.google.firebase.Timestamp(
                format.parse("2016-05-05")!!), SaleState.ACTIVE, null),
            Sale(anonymousBook("Book9"), LocalUser, 25.00f, BookCondition.GOOD, com.google.firebase.Timestamp(
                format.parse("2016-05-05")!!), SaleState.RETRACTED, null),
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
        val q3 = query.searchByISBN("").getAll()

        assertEquals(default_sale, q1.get())
        assertEquals(default_sale, q2.get())
        assertEquals(default_sale, q3.get())
    }

    @Test
    fun implementedFunctionsWork() {
        val q1 = query.searchByState( setOf(SaleState.CONCLUDED, SaleState.RETRACTED)).getAll()
        val q2 = query.searchByCondition(setOf(BookCondition.WORN, BookCondition.NEW)).getAll()
        val q3 = query.searchByPrice(23f, 24.9f).getAll()


        val q1Result = listOf(
                Sale(anonymousBook("Book5"), LocalUser, 21.00f, BookCondition.WORN, Timestamp(
                    format.parse("2016-05-05")!!), SaleState.CONCLUDED, null),
                Sale(anonymousBook("Book9"), LocalUser, 25.00f, BookCondition.GOOD, Timestamp(
                    format.parse("2016-05-05")!!), SaleState.RETRACTED, null),
                )
        val q2Result = listOf(
                Sale(anonymousBook("Book3"), LocalUser, 25.00f, BookCondition.NEW, Timestamp(
                    format.parse("2016-05-05")!!), SaleState.ACTIVE, null),
                Sale(anonymousBook("Book5"), LocalUser, 21.00f, BookCondition.WORN, Timestamp(
                    format.parse("2016-05-05")!!), SaleState.CONCLUDED, null),
                Sale(anonymousBook("Book8"), LocalUser, 23.66f, BookCondition.NEW, Timestamp(
                    format.parse("2016-05-05")!!), SaleState.ACTIVE, null),
                )
        val q3Result = listOf(
                Sale(anonymousBook("Book1"), LocalUser, 23.00f, BookCondition.GOOD, Timestamp(
                    format.parse("2016-05-05")!!), SaleState.ACTIVE, null),
                Sale(anonymousBook("Book2"), LocalUser, 24.55f, BookCondition.GOOD, Timestamp(
                    format.parse("2016-05-05")!!), SaleState.ACTIVE, null),
                Sale(anonymousBook("Book7"), LocalUser, 23.00f, BookCondition.GOOD, Timestamp(
                    format.parse("2016-05-05")!!), SaleState.ACTIVE, null),
                Sale(anonymousBook("Book8"), LocalUser, 23.66f, BookCondition.NEW, Timestamp(
                    format.parse("2016-05-05")!!), SaleState.ACTIVE, null)
        )
        assertEquals(q1Result, q1.get())
        assertEquals(q2Result, q2.get())
        assertEquals(q3Result, q3.get())
    }

    @Test
    fun settingsTest() {
        val settingsRes = SaleSettings(
            SaleOrdering.DEFAULT,
                null, null, null, null, null,null, null)
        val settings = query.searchByMaxPrice(10.0f).getSettings()
        assertEquals(settingsRes, settings)
    }

    @Test
    fun getSettingsAfterFromSettingsShouldCorrespond() {
        val settingsRes = SaleSettings(
            SaleOrdering.DEFAULT,
                null, null, null, null, null,null, null)

        val settings = query.fromSettings(settingsRes).getSettings()
        assertEquals(settingsRes, settings)
    }
}