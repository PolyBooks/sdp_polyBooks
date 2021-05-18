package com.github.polybooks.database

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.activities.MainActivity
import com.github.polybooks.core.*
import com.github.polybooks.core.BookCondition.*
import com.github.polybooks.core.SaleState.*
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

class FBSaleDatabaseTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private val firestore = FirebaseFirestore.getInstance()
    private val olBookDB = OLBookDatabase { string -> url2json(string) }
    private val bookDB = FBBookDatabase(firestore, olBookDB)
    private val saleDB = FBSaleDatabase(firestore, bookDB)

    private val testUser = LoggedUser(301966, "Le givre")
    private val testBook = Book("9780156881807",null, "Tartuffe, by Moliere", null, null, null, null, null)

    private val dummySale: Sale = Sale(
        testBook,
        testUser,
        500f,
        WORN,
        Timestamp.now(),
        RETRACTED,
        null
    )

    fun addDummySale() : Sale {
        return saleDB.addSale(dummySale).get()
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
    fun t_listAllSales() {
        val allSales: List<Sale> = saleDB.querySales().getAll().get()
        var listAllSales: List<Sale> = saleDB.listAllSales().get()

        val expectedSize: Int = allSales.size
        assertEquals(expectedSize, listAllSales.size)
        assertEquals(allSales, listAllSales)
    }

    @Test
    fun t_searchByTitle() {
        val initialCount: Int = saleDB.querySales().searchByTitle(dummySale.book.title).getAll().get().size
        val dummySale = addDummySale()
        val secondCount: Int = saleDB.querySales().searchByTitle(dummySale.book.title).getAll().get().size
        //saleDB.deleteSale(dummySale)

        assertEquals(initialCount + 1, secondCount)
        assertEquals(0, saleDB.querySales().searchByTitle("SSBhbSBhcG9sbG9uIHgK").getAll().get().size)
    }

    @Test
    fun t_searchMinPrice() {
        assertEquals(
                saleDB.listAllSales().get().size,
                saleDB.querySales().searchByMinPrice(0f).getAll().get().size
        )

        assertEquals(
                saleDB.listAllSales().get().filter { s -> s.price >= 10f }.size,
                saleDB.querySales().searchByMinPrice(10f).getAll().get().size
        )

        assertEquals(
                saleDB.listAllSales().get().filter { s -> s.price >= 150f }.size,
                saleDB.querySales().searchByMinPrice(150f).getAll().get().size
        )
    }

    @Test
    fun t_searchMaxPrice() {
        assertEquals(
                saleDB.listAllSales().get().filter { s -> s.price == 0f }.size,
                saleDB.querySales().searchByMaxPrice(0f).getAll().get().size
        )

        assertEquals(
                saleDB.listAllSales().get().filter { s -> s.price <= 10f }.size,
                saleDB.querySales().searchByMaxPrice(10f).getAll().get().size
        )

        assertEquals(
                0,
                saleDB.querySales().searchByMaxPrice(-1f).getAll().get().size
        )
    }

    @Test
    fun t_searchByPrice() {
        assertEquals(
                saleDB.listAllSales().get().filter { s -> (s.price in 5f..20f) }.size,
                saleDB.querySales().searchByPrice(5f, 20f).getAll().get().size
        )

        assertEquals(
                0,
                saleDB.querySales().searchByPrice(5f, 2f).getAll().get().size
        )
    }

    @Test
    fun t_searchByCondition() {
        // empty collection should be ignored
        assertEquals(
                saleDB.querySales().getAll().get().size,
                saleDB.querySales().searchByCondition(emptySet()).getAll().get().size
        )

        assertEquals(
                saleDB.listAllSales().get().filter { s -> s.condition == NEW }.size,
                saleDB.querySales().searchByCondition(setOf(NEW)).getAll().get().size
        )

        assertEquals(
                saleDB.listAllSales().get().filter { s -> (s.condition == NEW || s.condition == WORN)}.size,
                saleDB.querySales().searchByCondition(setOf(NEW, WORN)).getAll().get().size
        )

        assertEquals(
                saleDB.querySales().getAll().get().size,
                saleDB.querySales().searchByCondition(setOf(NEW, GOOD, WORN)).getAll().get().size
        )
    }

    @Test
    fun t_searchByState() {
        // empty collection should be ignored
        assertEquals(
                saleDB.querySales().getAll().get().size,
                saleDB.querySales().searchByState(emptySet()).getAll().get().size
        )

        assertEquals(
                saleDB.listAllSales().get().filter { s -> s.state == ACTIVE }.size,
                saleDB.querySales().searchByState(setOf(ACTIVE)).getAll().get().size
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
    fun addAsLocalUser(){
        try {
            saleDB.addSale(testBook.isbn, LocalUser, 666f, WORN, RETRACTED, null).get()
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

        val sale = saleDB.addSale(testBook.isbn, LoggedUser(300437, "testUser"), 666f, WORN, RETRACTED, null).get()
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
                setOf(RETRACTED),
                setOf(WORN, NEW),
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
                    WORN,
                    Timestamp.now(),
                    RETRACTED,
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
                setOf(RETRACTED), null, null,null
        )
        val sale = saleDB.addSale("9780156881807", testUser, 666f, WORN, RETRACTED, null).get()
        assertNotEquals(
                saleDB.querySales().searchByState(setOf(ACTIVE)).fromSettings(settings).getAll().get().size,
                saleDB.querySales().searchByState(setOf(ACTIVE)).getAll().get().size
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
                saleDB.querySales().fromSettings(settings).getAll().get().size,
                saleDB.querySales().searchByTitle("Tartuffe").getAll().get().size
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
                saleDB.querySales().fromSettings(settings).getAll().get().size,
                saleDB.querySales()
                    .searchByTitle("Tartuffe")
                    .searchByPrice(minPrice, maxPrice).getAll().get().size

        )
    }

    @Test
    fun canSearchBothByStateAndCondition() {
        val saleRetractedWorn = saleDB.addSale(dummySale.copy(state = RETRACTED, condition = WORN)).get()
        val saleRetractedGood = saleDB.addSale(dummySale.copy(state = RETRACTED, condition = GOOD)).get()
        val saleActiveGood = saleDB.addSale(dummySale.copy(state = ACTIVE, condition = GOOD)).get()
        val saleActiveWorn = saleDB.addSale(dummySale.copy(state = ACTIVE, condition = WORN)).get()

        val res1 = saleDB.querySales().searchByCondition(setOf(WORN)).searchByState(setOf(RETRACTED)).getAll().get()
        assertTrue(res1.contains(saleRetractedWorn))
        assertFalse(res1.contains(saleRetractedGood))
        assertFalse(res1.contains(saleActiveGood))
        assertFalse(res1.contains(saleActiveWorn))

        val res2 = saleDB.querySales().searchByCondition(setOf(GOOD)).searchByState(setOf(RETRACTED)).getAll().get()
        assertTrue(res2.contains(saleRetractedGood))
        assertFalse(res2.contains(saleRetractedWorn))
        assertFalse(res2.contains(saleActiveWorn))
        assertFalse(res2.contains(saleActiveGood))

        val res3 = saleDB.querySales().searchByCondition(setOf(GOOD)).searchByState(setOf(ACTIVE)).getAll().get()
        assertTrue(res3.contains(saleActiveGood))
        assertFalse(res3.contains(saleActiveWorn))
        assertFalse(res3.contains(saleRetractedGood))
        assertFalse(res3.contains(saleRetractedWorn))

        val res4 = saleDB.querySales().searchByISBN(dummySale.book.isbn).searchByCondition(setOf(WORN)).getAll().get()
        assertTrue(res4.contains(saleActiveWorn))
        assertTrue(res4.contains(saleRetractedWorn))
        assertFalse(res4.contains(saleActiveGood))
        assertFalse(res4.contains(saleRetractedGood))

        val res5 = saleDB.querySales().searchByCondition(setOf(WORN)).searchByState(setOf(ACTIVE, RETRACTED)).getAll().get()
        assertTrue(res5.contains(saleActiveWorn))
        assertTrue(res5.contains(saleRetractedWorn))
        assertFalse(res5.contains(saleActiveGood))
        assertFalse(res5.contains(saleRetractedGood))

        saleDB.deleteSale(saleRetractedWorn)
        saleDB.deleteSale(saleRetractedGood)
        saleDB.deleteSale(saleActiveWorn)
        saleDB.deleteSale(saleActiveGood)
    }

}
