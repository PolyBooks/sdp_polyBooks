package com.github.polybooks.database

import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.LocalUser
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.implementation.DummySalesQuery
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleQuery
import com.github.polybooks.core.database.interfaces.SaleSettings
import com.github.polybooks.utils.anonymousBook
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DummySalesQueryTest {

    private val query: SaleQuery = DummySalesQuery()

    private val defaultSale: List<Sale> = listOf(
        Sale(
            anonymousBook("Book1"),
            LocalUser,
            23.00f,
            BookCondition.GOOD,
            Timestamp(
                com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!
            ),
            SaleState.ACTIVE,
            null
        ),
        Sale(
            anonymousBook("Book2"),
            LocalUser,
            24.55f,
            BookCondition.GOOD,
            Timestamp(
                com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!
            ),
            SaleState.ACTIVE,
            null
        ),
        Sale(
            anonymousBook("Book3"),
            LocalUser,
            25.00f,
            BookCondition.NEW,
            Timestamp(
                com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!
            ),
            SaleState.ACTIVE,
            null
        ),
        Sale(
            anonymousBook("Book4"),
            LocalUser,
            26.00f,
            BookCondition.GOOD,
            Timestamp(
                com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!
            ),
            SaleState.ACTIVE,
            null
        ),
        Sale(
            anonymousBook("Book5"),
            LocalUser,
            21.00f,
            BookCondition.WORN,
            Timestamp(
                com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!
            ),
            SaleState.CONCLUDED,
            null
        ),
        Sale(
            anonymousBook("Book6"),
            LocalUser,
            29.00f,
            BookCondition.GOOD,
            Timestamp(
                com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!
            ),
            SaleState.ACTIVE,
            null
        ),
        Sale(
            anonymousBook("Book7"),
            LocalUser,
            23.00f,
            BookCondition.GOOD,
            Timestamp(
                com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!
            ),
            SaleState.ACTIVE,
            null
        ),
        Sale(
            anonymousBook("Book8"),
            LocalUser,
            23.66f,
            BookCondition.NEW,
            Timestamp(
                com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!
            ),
            SaleState.ACTIVE,
            null
        ),
        Sale(
            anonymousBook("Book9"),
            LocalUser,
            25.00f,
            BookCondition.GOOD,
            Timestamp(
                com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!
            ),
            SaleState.RETRACTED,
            null
        ),
    )


    @Test
    fun basicFunctionsWork() {
        val q1 = query.getCount()
        val q2 = query.getN(0, 0)
        val q3 = query.getAll()
        assertEquals(defaultSale.size, q1.get())
        assertEquals(defaultSale, q2.get())
        assertEquals(defaultSale, q3.get())
    }

    @Test
    fun unimplementedFunctionsWork() {
        val q1 = query.onlyIncludeInterests(emptySet()).getAll()
        val q2 = query.searchByTitle("").getAll()
        val q3 = query.searchByISBN("").getAll()

        assertEquals(defaultSale, q1.get())
        assertEquals(defaultSale, q2.get())
        assertEquals(defaultSale, q3.get())
    }

    @Test
    fun implementedFunctionsWork() {
        val q1 = query.searchByState(setOf(SaleState.CONCLUDED, SaleState.RETRACTED)).getAll()
        val q2 = query.searchByCondition(setOf(BookCondition.WORN, BookCondition.NEW)).getAll()
        val q3 = query.searchByPrice(23f, 24.9f).getAll()


        val q1Result = listOf(
            Sale(
                anonymousBook("Book5"),
                LocalUser,
                21.00f,
                BookCondition.WORN,
                Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!),
                SaleState.CONCLUDED,
                null
            ),
            Sale(
                anonymousBook("Book9"),
                LocalUser,
                25.00f,
                BookCondition.GOOD,
                Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!),
                SaleState.RETRACTED,
                null
            ),
        )
        val q2Result = listOf(
            Sale(
                anonymousBook("Book3"),
                LocalUser,
                25.00f,
                BookCondition.NEW,
                Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!),
                SaleState.ACTIVE,
                null
            ),
            Sale(
                anonymousBook("Book5"),
                LocalUser,
                21.00f,
                BookCondition.WORN,
                Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!),
                SaleState.CONCLUDED,
                null
            ),
            Sale(
                anonymousBook("Book8"),
                LocalUser,
                23.66f,
                BookCondition.NEW,
                Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!),
                SaleState.ACTIVE,
                null
            ),
        )
        val q3Result = listOf(
            Sale(
                anonymousBook("Book1"),
                LocalUser,
                23.00f,
                BookCondition.GOOD,
                Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!),
                SaleState.ACTIVE,
                null
            ),
            Sale(
                anonymousBook("Book2"),
                LocalUser,
                24.55f,
                BookCondition.GOOD,
                Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!),
                SaleState.ACTIVE,
                null
            ),
            Sale(
                anonymousBook("Book7"),
                LocalUser,
                23.00f,
                BookCondition.GOOD,
                Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!),
                SaleState.ACTIVE,
                null
            ),
            Sale(
                anonymousBook("Book8"),
                LocalUser,
                23.66f,
                BookCondition.NEW,
                Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!),
                SaleState.ACTIVE,
                null
            )
        )
        assertEquals(q1Result, q1.get())
        assertEquals(q2Result, q2.get())
        assertEquals(q3Result, q3.get())
    }

    @Test
    fun settingsTest() {
        val settingsRes = SaleSettings(
            SaleOrdering.DEFAULT,
            null, null, null, null, null, null, null
        )
        val settings = query.searchByMaxPrice(10.0f).getSettings()
        assertEquals(settingsRes, settings)
    }

    @Test
    fun getSettingsAfterFromSettingsShouldCorrespond() {
        val settingsRes = SaleSettings(
            SaleOrdering.DEFAULT,
            null, null, null, null, null, null, null
        )

        val settings = query.fromSettings(settingsRes).getSettings()
        assertEquals(settingsRes, settings)
    }
}