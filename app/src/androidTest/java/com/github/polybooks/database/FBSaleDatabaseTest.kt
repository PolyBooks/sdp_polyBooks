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
    private val testBook =
        Book("9780156881807", null, "Tartuffe, by Moliere", null, null, null, null, null)

    private val dummySale: Sale = Sale(
        testBook,
        testUser,
        500f,
        WORN,
        Timestamp.now(),
        RETRACTED,
        null
    )

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun cleanUp() {
        val testSales = saleDB.listAllSales().get().filter { it.seller == testUser }
        testSales.forEach { saleDB.deleteSale(it).get() }
        Intents.release()
    }

    @Rule
    @JvmField
    val expectedException: ExpectedException = ExpectedException.none()

    //Checks that the content of both collection is the same
    fun <T> assertSame(a: Collection<T>, b: Collection<T>) {
        assertTrue(a.containsAll(b))
        assertTrue(b.containsAll(a))
    }

    @Test
    fun t_listAllSales() {
        val allSales: List<Sale> = saleDB.querySales().getAll().get()
        val listAllSales: List<Sale> = saleDB.listAllSales().get()

        assertSame(allSales, listAllSales)
    }

    @Test
    fun t_searchByTitle() {
        val sale1 = saleDB.addSale(dummySale).get()
        val sale2 =
            saleDB.addSale(dummySale.copy(book = olBookDB.getBook("9782376863069").get()!!)).get()
        val sales = saleDB.querySales().searchByTitle(dummySale.book.title).getAll().get()
        assertTrue(sales.contains(sale1))
        assertFalse(sales.contains(sale2))

        saleDB.deleteSale(sale1).get()
        saleDB.deleteSale(sale2).get()

        assertTrue(
            saleDB.querySales().searchByTitle("SSBhbSBhcG9sbG9uIHgK").getAll().get().isEmpty()
        )
    }

    @Test
    fun doesntCrashWhenNoBooksAreFound() {
        saleDB.querySales().searchByTitle("SSBhbSBhcG9sbG9uIHgK").getAll().get()
        saleDB.querySales().searchByTitle("SSBhbSBhcG9sbG9uIHgK").getN(2,0).get()
    }

    @Test
    fun t_searchMinPrice() {

        val sale1 = saleDB.addSale(dummySale.copy(price = 1.0f)).get()
        val sale2 = saleDB.addSale(dummySale.copy(price = 10.0f)).get()
        val sale3 = saleDB.addSale(dummySale.copy(price = 100.0f)).get()

        assertSame(
            saleDB.listAllSales().get(),
            saleDB.querySales().searchByMinPrice(0f).getAll().get()
        )

        val salesgt10 = saleDB.querySales().searchByMinPrice(10.0f).getAll().get()
        assertFalse(salesgt10.contains(sale1))
        assertTrue(salesgt10.contains(sale2))
        assertTrue(salesgt10.contains(sale3))
        assertTrue(salesgt10.all { it.price >= 10.0f })

        saleDB.deleteSale(sale1).get()
        saleDB.deleteSale(sale2).get()
        saleDB.deleteSale(sale3).get()
    }

    @Test
    fun t_searchMaxPrice() {
        assertTrue(saleDB.querySales().searchByMaxPrice(-1f).getAll().get().isEmpty())

        val sale1 = saleDB.addSale(dummySale.copy(price = 0f)).get()
        val sale2 = saleDB.addSale(dummySale.copy(price = 1f)).get()
        val sale3 = saleDB.addSale(dummySale.copy(price = 10f)).get()
        val sale4 = saleDB.addSale(dummySale.copy(price = 100f)).get()

        val saleslt0 = saleDB.querySales().searchByMaxPrice(0f).getAll().get()
        assertTrue(saleslt0.all { it.price <= 0f })
        assertTrue(saleslt0.contains(sale1))

        val saleslt10 = saleDB.querySales().searchByMaxPrice(10f).getAll().get()
        assertTrue(saleslt0.all { it.price <= 10f })
        assertTrue(saleslt10.contains(sale1))
        assertTrue(saleslt10.contains(sale2))
        assertTrue(saleslt10.contains(sale3))
        assertFalse(saleslt10.contains(sale4))

        saleDB.deleteSale(sale1).get()
        saleDB.deleteSale(sale2).get()
        saleDB.deleteSale(sale3).get()
        saleDB.deleteSale(sale4).get()

    }

    @Test
    fun t_searchByPrice() {
        assertTrue(saleDB.querySales().searchByPrice(5f, 2f).getAll().get().isEmpty())

        val sale1 = saleDB.addSale(dummySale.copy(price = 0f)).get()
        val sale2 = saleDB.addSale(dummySale.copy(price = 5f)).get()
        val sale3 = saleDB.addSale(dummySale.copy(price = 6f)).get()
        val sale4 = saleDB.addSale(dummySale.copy(price = 10f)).get()
        val sale5 = saleDB.addSale(dummySale.copy(price = 100f)).get()

        val salesgt5lt10 = saleDB.querySales().searchByPrice(5f, 10f).getAll().get()
        assertTrue(salesgt5lt10.all { it.price in 5f..10f })
        assertFalse(salesgt5lt10.contains(sale1))
        assertTrue(salesgt5lt10.contains(sale2))
        assertTrue(salesgt5lt10.contains(sale3))
        assertTrue(salesgt5lt10.contains(sale4))
        assertFalse(salesgt5lt10.contains(sale5))

        saleDB.deleteSale(sale1).get()
        saleDB.deleteSale(sale2).get()
        saleDB.deleteSale(sale3).get()
        saleDB.deleteSale(sale4).get()
        saleDB.deleteSale(sale5).get()
    }

    @Test
    fun t_searchByCondition() {

        val sale1 = saleDB.addSale(dummySale.copy(condition = GOOD)).get()
        val sale2 = saleDB.addSale(dummySale.copy(condition = WORN)).get()
        val sale3 = saleDB.addSale(dummySale.copy(condition = NEW)).get()

        assertSame(
            saleDB.querySales().getAll().get(),
            saleDB.querySales().searchByCondition(emptySet()).getAll().get()
        )

        val salesWorn = saleDB.querySales().searchByCondition(setOf(WORN)).getAll().get()
        assertTrue(salesWorn.all { it.condition == WORN })
        assertTrue(salesWorn.contains(sale2))

        val salesGoodWorn = saleDB.querySales().searchByCondition(setOf(WORN, GOOD)).getAll().get()
        assertTrue(salesGoodWorn.all { it.condition == WORN || it.condition == GOOD })
        assertTrue(salesGoodWorn.contains(sale2))
        assertTrue(salesGoodWorn.contains(sale1))

        assertSame(
            saleDB.querySales().getAll().get(),
            saleDB.querySales().searchByCondition(setOf(NEW, GOOD, WORN)).getAll().get()
        )

        saleDB.deleteSale(sale1).get()
        saleDB.deleteSale(sale2).get()
        saleDB.deleteSale(sale3).get()
    }

    @Test
    fun t_searchByState() {

        val sale1 = saleDB.addSale(dummySale.copy(state = ACTIVE)).get()
        val sale2 = saleDB.addSale(dummySale.copy(state = RETRACTED)).get()
        val sale3 = saleDB.addSale(dummySale.copy(state = CONCLUDED)).get()

        assertSame(
            saleDB.querySales().getAll().get(),
            saleDB.querySales().searchByState(emptySet()).getAll().get()
        )

        val salesActive = saleDB.querySales().searchByState(setOf(ACTIVE)).getAll().get()
        assertTrue(salesActive.all { it.state == ACTIVE })
        assertTrue(salesActive.contains(sale1))

        val salesActiveRetracted =
            saleDB.querySales().searchByState(setOf(ACTIVE, RETRACTED)).getAll().get()
        assertTrue(salesActiveRetracted.all { it.state == ACTIVE || it.state == RETRACTED })
        assertTrue(salesActiveRetracted.contains(sale2))
        assertTrue(salesActiveRetracted.contains(sale1))

        assertSame(
            saleDB.querySales().getAll().get(),
            saleDB.querySales().searchByState(setOf(ACTIVE, RETRACTED, CONCLUDED)).getAll().get()
        )

        saleDB.deleteSale(sale1).get()
        saleDB.deleteSale(sale2).get()
        saleDB.deleteSale(sale3).get()
    }

    @Test
    fun addAsLocalUser() {
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
    fun addDelete() {
        fun saleExists(sale: Sale): Boolean {
            return saleDB.querySales().searchByISBN(sale.book.isbn).getAll().get().any {
                it.date == sale.date && it.seller == sale.seller
            }
        }

        val sale = saleDB.addSale(
            testBook.isbn,
            LoggedUser(300437, "testUser"),
            666f,
            WORN,
            RETRACTED,
            null
        ).get()
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
                Semester("IC", "BA3")
            ),
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
            when (unwrapException(e)) {
                is IllegalArgumentException -> return
                else -> throw AssertionFailedError("Wrong exception type thrown")
            }
        }
        throw AssertionFailedError("Exception expected to be thrown but wasn't")
    }

    @Test
    fun settingsModifiesStateOfQuery() {
        val settings = SaleSettings(
            SaleOrdering.DEFAULT, null, null, null,
            setOf(RETRACTED), null, null, null
        )

        val sale1 = saleDB.addSale(dummySale.copy(state = ACTIVE)).get()
        val sale2 = saleDB.addSale(dummySale.copy(state = RETRACTED)).get()
        val sale3 = saleDB.addSale(dummySale.copy(state = CONCLUDED)).get()

        val salesRetracted =
            saleDB.querySales().searchByState(setOf(ACTIVE)).fromSettings(settings).getAll().get()
        assertTrue(salesRetracted.all { it.state == RETRACTED })
        val salesActive = saleDB.querySales().searchByState(setOf(ACTIVE)).getAll().get()
        assertTrue(salesActive.all { it.state == ACTIVE })

        saleDB.deleteSale(sale1).get()
        saleDB.deleteSale(sale2).get()
        saleDB.deleteSale(sale3).get()
    }

    @Test
    fun fromSettingsIsEquivalent() {
        val title = dummySale.book.title
        val settings = SaleSettings(
            SaleOrdering.DEFAULT, null, title, null,
            null, null, null, null
        )
        assertSame(
            saleDB.querySales().searchByTitle(title).getAll().get(),
            saleDB.querySales().fromSettings(settings).getAll().get()
        )

    }

    @Test
    fun fromSettingsIsEquivalent2() {
        val title = dummySale.book.title
        val minPrice = 0.0f
        val maxPrice = 10.0f

        val settings = SaleSettings(
            SaleOrdering.DEFAULT, null, title, null, null,
            null, minPrice, maxPrice
        )

        assertSame(
            saleDB.querySales().searchByTitle(title).searchByPrice(minPrice, maxPrice).getAll()
                .get(),
            saleDB.querySales().fromSettings(settings).getAll().get()
        )

    }

    @Test
    fun canSearchBothByStateAndCondition() {
        val saleRetractedWorn =
            saleDB.addSale(dummySale.copy(state = RETRACTED, condition = WORN)).get()
        val saleRetractedGood =
            saleDB.addSale(dummySale.copy(state = RETRACTED, condition = GOOD)).get()
        val saleActiveGood = saleDB.addSale(dummySale.copy(state = ACTIVE, condition = GOOD)).get()
        val saleActiveWorn = saleDB.addSale(dummySale.copy(state = ACTIVE, condition = WORN)).get()

        val res1 =
            saleDB.querySales().searchByCondition(setOf(WORN)).searchByState(setOf(RETRACTED))
                .getAll().get()
        assertTrue(res1.contains(saleRetractedWorn))
        assertFalse(res1.contains(saleRetractedGood))
        assertFalse(res1.contains(saleActiveGood))
        assertFalse(res1.contains(saleActiveWorn))

        val res2 =
            saleDB.querySales().searchByCondition(setOf(GOOD)).searchByState(setOf(RETRACTED))
                .getAll().get()
        assertTrue(res2.contains(saleRetractedGood))
        assertFalse(res2.contains(saleRetractedWorn))
        assertFalse(res2.contains(saleActiveWorn))
        assertFalse(res2.contains(saleActiveGood))

        val res3 =
            saleDB.querySales().searchByCondition(setOf(GOOD)).searchByState(setOf(ACTIVE)).getAll()
                .get()
        assertTrue(res3.contains(saleActiveGood))
        assertFalse(res3.contains(saleActiveWorn))
        assertFalse(res3.contains(saleRetractedGood))
        assertFalse(res3.contains(saleRetractedWorn))

        val res4 =
            saleDB.querySales().searchByISBN(dummySale.book.isbn).searchByCondition(setOf(WORN))
                .getAll().get()
        assertTrue(res4.contains(saleActiveWorn))
        assertTrue(res4.contains(saleRetractedWorn))
        assertFalse(res4.contains(saleActiveGood))
        assertFalse(res4.contains(saleRetractedGood))

        val res5 = saleDB.querySales().searchByCondition(setOf(WORN))
            .searchByState(setOf(ACTIVE, RETRACTED)).getAll().get()
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
