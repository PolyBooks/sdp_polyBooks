package com.github.polybooks.database

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.MainActivity
import com.github.polybooks.core.*
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleSettings
import com.github.polybooks.core.database.LocalUserException
import com.github.polybooks.core.database.implementation.FBBookDatabase
import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.github.polybooks.utils.unwrapException
import com.github.polybooks.utils.url2json
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.AssertionFailedError
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.ExpectedException
import java.lang.IllegalArgumentException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

class SaleDatabaseTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private val firestore = FirebaseFirestore.getInstance()
    private val olBookDB = OLBookDatabase { string -> url2json(string) }
    private val bookDB = FBBookDatabase(firestore, olBookDB)
    private val saleDB = SaleDatabase(firestore, bookDB)

    private val testUser = LoggedUser(301966, "Le givre")
    private val testBook = Book("9780156881807",null, "Tartuffe, by Moliere", null, null, null, null, null)

    private val dummySale: Sale = Sale(
        testBook,
        testUser,
        500f,
        BookCondition.WORN,
        Timestamp.now(),
        SaleState.RETRACTED,
        null
    )

    fun addDummySale() : Sale {
        return saleDB.addSale(dummySale.book.isbn,dummySale.seller,dummySale.price, dummySale.condition, dummySale.state, dummySale.image).get()
    }

    @Before fun setUp() {
        Intents.init()
    }

    @After fun cleanUp() {
        val testSales = saleDB.listAllSales().get().filter { it.seller == testUser }
        testSales.forEach { saleDB.deleteSale(it).get() }
        Intents.release()
    }

    @Rule @JvmField val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun t_getCount() {
        val allSales: CompletableFuture<List<Sale>> = saleDB.querySales().getAll()
        val count: CompletableFuture<Int> = saleDB.querySales().getCount()

        assertEquals(allSales.get().size, count.get())
    }

    @Test
    fun t_listAllSales() {
        val allSales: List<Sale> = saleDB.querySales().getAll().get()
        var listAllSales: List<Sale> = saleDB.listAllSales().get()

        val expectedSize: Int = allSales.size
        assertEquals(expectedSize, listAllSales.size)
        assertEquals(allSales, listAllSales)
    }

    @Test
    fun t_searchByTitle() {
        val initialCount: Int = saleDB.querySales().searchByTitle(dummySale.book.title).getCount().get()
        val dummySale = addDummySale()
        val secondCount: Int = saleDB.querySales().searchByTitle(dummySale.book.title).getCount().get()
        //saleDB.deleteSale(dummySale)

        assertEquals(initialCount + 1, secondCount)
        assertEquals(0, saleDB.querySales().searchByTitle("SSBhbSBhcG9sbG9uIHgK").getCount().get())
    }

    @Test
    fun t_searchMinPrice() {
        assertEquals(
                saleDB.listAllSales().get().size,
                saleDB.querySales().searchByMinPrice(0f).getCount().get()
        )

        assertEquals(
                saleDB.listAllSales().get().filter { s -> s.price >= 10f }.size,
                saleDB.querySales().searchByMinPrice(10f).getCount().get()
        )

        assertEquals(
                saleDB.listAllSales().get().filter { s -> s.price >= 150f }.size,
                saleDB.querySales().searchByMinPrice(150f).getCount().get()
        )
    }

    @Test
    fun t_searchMaxPrice() {
        assertEquals(
                saleDB.listAllSales().get().filter { s -> s.price == 0f }.size,
                saleDB.querySales().searchByMaxPrice(0f).getCount().get()
        )

        assertEquals(
                saleDB.listAllSales().get().filter { s -> s.price <= 10f }.size,
                saleDB.querySales().searchByMaxPrice(10f).getCount().get()
        )

        assertEquals(
                0,
                saleDB.querySales().searchByMaxPrice(-1f).getCount().get()
        )
    }

    @Test
    fun t_searchByPrice() {
        assertEquals(
                saleDB.listAllSales().get().filter { s -> (s.price in 5f..20f) }.size,
                saleDB.querySales().searchByPrice(5f, 20f).getCount().get()
        )

        assertEquals(
                0,
                saleDB.querySales().searchByPrice(5f, 2f).getCount().get()
        )
    }

    @Test
    fun t_searchByCondition() {
        // empty collection should be ignored
        assertEquals(
                saleDB.querySales().getCount().get(),
                saleDB.querySales().searchByCondition(emptySet()).getCount().get()
        )

        assertEquals(
                saleDB.listAllSales().get().filter { s -> s.condition == BookCondition.NEW }.size,
                saleDB.querySales().searchByCondition(setOf(BookCondition.NEW)).getCount().get()
        )

        assertEquals(
                saleDB.listAllSales().get().filter { s -> (s.condition == BookCondition.NEW || s.condition == BookCondition.WORN)}.size,
                saleDB.querySales().searchByCondition(setOf(BookCondition.NEW, BookCondition.WORN)).getCount().get()
        )

        assertEquals(
                saleDB.querySales().getCount().get(),
                saleDB.querySales().searchByCondition(setOf(BookCondition.NEW, BookCondition.GOOD, BookCondition.WORN)).getCount().get()
        )
    }

    @Test
    fun t_searchByState() {
        // empty collection should be ignored
        assertEquals(
                saleDB.querySales().getCount().get(),
                saleDB.querySales().searchByState(emptySet()).getCount().get()
        )

        assertEquals(
                saleDB.listAllSales().get().filter { s -> s.state == SaleState.ACTIVE }.size,
                saleDB.querySales().searchByState(setOf(SaleState.ACTIVE)).getCount().get()
        )
    }

    fun <T>customAssertFutureThrows(future: CompletableFuture<T>, n: Int, page: Int) {
        try {
            future.get()
            fail("Future should not succeed")
        } catch (e: ExecutionException) {
            if (n < 0) assertEquals("java.lang.IllegalArgumentException: Cannot return a negative ($n) number of results", e.message)
            else assertEquals("java.lang.IllegalArgumentException: Cannot return a negative ($page) page number", e.message)
        } catch (e: Throwable) {
            fail("Wrong exception type")
        }
    }

    @Test
    fun t_getN() {
        var future: CompletableFuture<List<Sale>> = saleDB.querySales().getN(-1, 10)
        assertTrue(future.isCompletedExceptionally)
        customAssertFutureThrows(future, -1, 10)

        future = saleDB.querySales().getN(10, -1)
        assertTrue(future.isCompletedExceptionally)
        customAssertFutureThrows(future, 10, -1)

        future = saleDB.querySales().getN(-1, -1)
        assertTrue(future.isCompletedExceptionally)
        customAssertFutureThrows(future, -1, -1)

        future = saleDB.querySales().getN(0, 0)
        assertEquals(0, future.get().size)

        future = saleDB.querySales().getN(1, 0)
        assertTrue(future.get().size <= 1)

        addDummySale()
        future = saleDB.querySales().getN(1, 0)
        assertTrue(future.get().size <= 1)
    }

    @Test
    fun addAsLocalUser(){
        try {
            saleDB.addSale(testBook.isbn, LocalUser, 666f, BookCondition.WORN, SaleState.RETRACTED, null).get()
        } catch (e: Throwable) {
            when (unwrapException(e)) {
                is LocalUserException -> return
                else -> fail("Throws wrong exception type: ${unwrapException(e).javaClass}")
            }
        }
        fail("Didn't throw exception")
    }

    @Test
    fun addDelete(){
        fun saleExists(sale : Sale) : Boolean {
            return saleDB.querySales().searchByISBN(sale.book.isbn).getAll().get().any {
                it.date == sale.date && it.seller == sale.seller
            }
        }

        val sale = saleDB.addSale(testBook.isbn, LoggedUser(300437, "testUser"), 666f, BookCondition.WORN, SaleState.RETRACTED, null).get()
        assertTrue(saleExists(sale))
        saleDB.deleteSale(sale).get()
        assertFalse(saleExists(sale))
    }

    @Test
    fun getSettingsAndFromSettingsMatch() {
        val settings = SaleSettings(
                SaleOrdering.DEFAULT,
                "111222333444",
                "A Book",
                setOf(
                        Course("COM-301"),
                        Field("Biology"),
                        Semester("IC", "BA3")),
                setOf(SaleState.RETRACTED),
                setOf(BookCondition.WORN, BookCondition.NEW),
                3.0f,
                10.0f
        )

        assertEquals(
                settings,
                saleDB.querySales().fromSettings(settings).getSettings()
        )
    }

    @Test
    fun deleteSaleFromLocalUser() {
        try {
            saleDB.deleteSale(
                Sale(
                    testBook,
                    LocalUser,
                    666f,
                    BookCondition.WORN,
                    Timestamp.now(),
                    SaleState.RETRACTED,
                    null
                )
            ).get()
        } catch (e: Throwable) {
            val exception = unwrapException(e)
            when (exception) {
                is IllegalArgumentException -> return
                else -> throw AssertionFailedError("Wrong exception type thrown")
            }
        }
        throw AssertionFailedError("Exception expected to be thrown but wasn't")
    }

    @Test
    fun settingsModifiesStateOfQuery() {
        val settings = SaleSettings(
                SaleOrdering.DEFAULT, null,null, null,
                setOf(SaleState.RETRACTED), null, null,null
        )
        val sale = saleDB.addSale("9780156881807", testUser, 666f, BookCondition.WORN, SaleState.RETRACTED, null).get()
        assertNotEquals(
                saleDB.querySales().searchByState(setOf(SaleState.ACTIVE)).fromSettings(settings).getCount().get(),
                saleDB.querySales().searchByState(setOf(SaleState.ACTIVE)).getCount().get()
        )
        saleDB.deleteSale(sale).get()
    }

    @Test
    fun settingsQueriesTheSameWayAsOnlyIncludeInterests() {
        val settings = SaleSettings(
            SaleOrdering.DEFAULT, null,title = "Tartuffe", null,
            null, null, null,null
        )

        assertEquals(
                saleDB.querySales().fromSettings(settings).getCount().get(),
                saleDB.querySales().searchByTitle("Tartuffe").getCount().get()
        )
    }

    @Test
    fun settingsQueriesTheSameWayAsQueryFunctions() {
        val title = "Tartuffe"
        val minPrice = 0.0f
        val maxPrice = 10.0f

        val settings = SaleSettings(
                SaleOrdering.DEFAULT, null, title, null, null
                ,null, minPrice, maxPrice
        )
        assertEquals(
                saleDB.querySales().fromSettings(settings).getCount().get(),
                saleDB.querySales()
                    .searchByTitle("Tartuffe")
                    .searchByPrice(minPrice, maxPrice).getCount().get()

        )
    }

}
