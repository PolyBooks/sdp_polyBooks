package com.github.polybooks.database

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.MainActivity
import com.github.polybooks.core.*
import com.github.polybooks.core.SaleFields.*
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleSettings
import com.github.polybooks.core.database.LocalUserException
import com.github.polybooks.utils.unwrapException
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.ExpectedException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class SaleDatabaseTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private val saleRef: CollectionReference = FirebaseFirestore.getInstance().collection("sale")
    private val db = SaleDatabase()

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

    fun addDummySale(payload: Sale = dummySale) : Sale {
        return db.addSale(payload.book.isbn,payload.seller,payload.price, payload.condition, payload.state, payload.image).get()
    }

    @Before fun setUp() {
        Intents.init()
    }

    @After fun cleanUp() {
        val testSales = db.listAllSales().get().filter { it.seller == testUser }
        testSales.forEach { db.deleteSale(it).get() }
        Intents.release()
    }

    @Rule @JvmField val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun t_getCount() {
        val allSales: CompletableFuture<List<Sale>> = db.querySales().getAll()
        val count: CompletableFuture<Int> = db.querySales().getCount()

        assertEquals(allSales.get().size, count.get())
    }

    @Test
    fun t_listAllSales() {
        val allSales: List<Sale> = db.querySales().getAll().get()
        var listAllSales: List<Sale> = db.listAllSales().get()

        val expectedSize: Int = allSales.size
        assertEquals(expectedSize, listAllSales.size)
        assertEquals(allSales, listAllSales)
    }

    @Test
    fun t_searchByTitle() {
        val initialCount: Int = db.querySales().searchByTitle(testBook.title).getCount().get()
        addDummySale()
        val secondCount: Int = db.querySales().searchByTitle(testBook.title).getCount().get()

        assertEquals(secondCount, initialCount + 1)
        assertEquals(0, db.querySales().searchByTitle("SSBhbSBhcG9sbG9uIHgK").getCount().get())
    }

    @Test
    fun t_searchMinPrice() {
        assertEquals(
                db.listAllSales().get().size,
                db.querySales().searchByMinPrice(0f).getCount().get()
        )

        assertEquals(
                db.listAllSales().get().filter { s -> s.price >= 10f }.size,
                db.querySales().searchByMinPrice(10f).getCount().get()
        )

        assertEquals(
                db.listAllSales().get().filter { s -> s.price >= 150f }.size,
                db.querySales().searchByMinPrice(150f).getCount().get()
        )
    }

    @Test
    fun t_searchMaxPrice() {
        assertEquals(
                db.listAllSales().get().filter { s -> s.price == 0f }.size,
                db.querySales().searchByMaxPrice(0f).getCount().get()
        )

        assertEquals(
                db.listAllSales().get().filter { s -> s.price <= 10f }.size,
                db.querySales().searchByMaxPrice(10f).getCount().get()
        )

        assertEquals(
                0,
                db.querySales().searchByMaxPrice(-1f).getCount().get()
        )
    }

    @Test
    fun t_searchByPrice() {
        assertEquals(
                db.listAllSales().get().filter { s -> (s.price in 5f..20f) }.size,
                db.querySales().searchByPrice(5f, 20f).getCount().get()
        )

        assertEquals(
                0,
                db.querySales().searchByPrice(5f, 2f).getCount().get()
        )
    }

    @Test
    fun t_searchByCondition() {
        // empty collection should be ignored
        assertEquals(
                db.querySales().getCount().get(),
                db.querySales().searchByCondition(emptySet()).getCount().get()
        )

        assertEquals(
                db.listAllSales().get().filter { s -> s.condition == BookCondition.NEW }.size,
                db.querySales().searchByCondition(setOf(BookCondition.NEW)).getCount().get()
        )

        assertEquals(
                db.listAllSales().get().filter { s -> (s.condition == BookCondition.NEW || s.condition == BookCondition.WORN)}.size,
                db.querySales().searchByCondition(setOf(BookCondition.NEW, BookCondition.WORN)).getCount().get()
        )

        assertEquals(
                db.querySales().getCount().get(),
                db.querySales().searchByCondition(setOf(BookCondition.NEW, BookCondition.GOOD, BookCondition.WORN)).getCount().get()
        )
    }

    @Test
    fun t_searchByState() {
        // empty collection should be ignored
        assertEquals(
                db.querySales().getCount().get(),
                db.querySales().searchByState(emptySet()).getCount().get()
        )

        assertEquals(
                db.listAllSales().get().filter { s -> s.state == SaleState.ACTIVE }.size,
                db.querySales().searchByState(setOf(SaleState.ACTIVE)).getCount().get()
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
        var future: CompletableFuture<List<Sale>> = db.querySales().getN(-1, 10)
        assertTrue(future.isCompletedExceptionally)
        customAssertFutureThrows(future, -1, 10)

        future = db.querySales().getN(10, -1)
        assertTrue(future.isCompletedExceptionally)
        customAssertFutureThrows(future, 10, -1)

        future = db.querySales().getN(-1, -1)
        assertTrue(future.isCompletedExceptionally)
        customAssertFutureThrows(future, -1, -1)

        future = db.querySales().getN(0, 0)
        assertEquals(0, future.get().size)

        future = db.querySales().getN(1, 0)
        assertTrue(future.get().size <= 1)

        addDummySale()
        future = db.querySales().getN(1, 0)
        assertTrue(future.get().size <= 1)
    }

    @Test
    fun addAsLocalUser(){
        try {
            db.addSale(testBook.isbn, LocalUser, 666f, BookCondition.WORN, SaleState.RETRACTED, null).get()
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
            return db.querySales().searchByISBN(sale.book.isbn).getAll().get().any {
                it.date == sale.date && it.seller == sale.seller
            }
        }

        val sale = db.addSale(testBook.isbn, LoggedUser(300437, "testUser"), 666f, BookCondition.WORN, SaleState.RETRACTED, null).get()
        assertTrue(saleExists(sale))
        db.deleteSale(sale).get()
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
                db.querySales().fromSettings(settings).getSettings()
        )
    }

    @Test
    fun settingsModifiesStateOfQuery() {
        val settings = SaleSettings(
                SaleOrdering.DEFAULT, null,null, null,
                setOf(SaleState.RETRACTED), null, null,null
        )
        assertNotEquals(
                db.querySales().fromSettings(settings).getCount().get(),
                db.querySales().searchByState(setOf(SaleState.ACTIVE)).getCount().get()
        )
    }

    @Test
    fun settingsQueriesTheSameWayAsOnlyIncludeInterests() {
        val settings = SaleSettings(
            SaleOrdering.DEFAULT, null,title = "Tartuffe", null,
            null, null, null,null
        )

        assertEquals(
                db.querySales().fromSettings(settings).getCount().get(),
                db.querySales().searchByTitle("Tartuffe").getCount().get()
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
                db.querySales().fromSettings(settings).getCount().get(),
                db.querySales()
                    .searchByTitle("Tartuffe")
                    .searchByPrice(minPrice, maxPrice).getCount().get()

        )
    }

}
