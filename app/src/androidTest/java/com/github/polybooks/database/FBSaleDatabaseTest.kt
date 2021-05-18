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
        val testSales = saleDB.execute(SaleQuery()).get().filter { it.seller == testUser }
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
    fun t_searchByTitle() {
        val sale1 = saleDB.addSale(dummySale).get()
        val sale2 =
            saleDB.addSale(dummySale.copy(book = olBookDB.getBook("9782376863069").get()!!)).get()
        val sales = saleDB.execute(SaleQuery(title = dummySale.book.title)).get()
        assertTrue(sales.contains(sale1))
        assertFalse(sales.contains(sale2))

        saleDB.deleteSale(sale1).get()
        saleDB.deleteSale(sale2).get()

        assertTrue(saleDB.execute(SaleQuery(title = "SSBhbSBhcG9sbG9uIHgK")).get().isEmpty())
    }

    @Test
    fun t_searchMinPrice() {

        val sale1 = saleDB.addSale(dummySale.copy(price = 1.0f)).get()
        val sale2 = saleDB.addSale(dummySale.copy(price = 10.0f)).get()
        val sale3 = saleDB.addSale(dummySale.copy(price = 100.0f)).get()

        assertSame(
            saleDB.execute(SaleQuery()).get(),
            saleDB.execute(SaleQuery(minPrice = 0f)).get()
        )

        val salesgt10 = saleDB.execute(SaleQuery(minPrice = 10f)).get()
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
        assertTrue(saleDB.execute(SaleQuery(maxPrice = -1f)).get().isEmpty())

        val sale1 = saleDB.addSale(dummySale.copy(price = 0f)).get()
        val sale2 = saleDB.addSale(dummySale.copy(price = 1f)).get()
        val sale3 = saleDB.addSale(dummySale.copy(price = 10f)).get()
        val sale4 = saleDB.addSale(dummySale.copy(price = 100f)).get()

        val saleslt0 = saleDB.execute(SaleQuery(maxPrice = 0f)).get()
        assertTrue(saleslt0.all { it.price <= 0f })
        assertTrue(saleslt0.contains(sale1))

        val saleslt10 = saleDB.execute(SaleQuery(maxPrice = 10f)).get()
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
        assertTrue(saleDB.execute(SaleQuery(minPrice = 5f, maxPrice = 2f)).get().isEmpty())

        val sale1 = saleDB.addSale(dummySale.copy(price = 0f)).get()
        val sale2 = saleDB.addSale(dummySale.copy(price = 5f)).get()
        val sale3 = saleDB.addSale(dummySale.copy(price = 6f)).get()
        val sale4 = saleDB.addSale(dummySale.copy(price = 10f)).get()
        val sale5 = saleDB.addSale(dummySale.copy(price = 100f)).get()

        val salesgt5lt10 = saleDB.execute(SaleQuery(minPrice = 5f, maxPrice = 10f)).get()
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

        val salesWorn = saleDB.execute(SaleQuery(conditions = setOf(WORN))).get()
        assertTrue(salesWorn.all { it.condition == WORN })
        assertTrue(salesWorn.contains(sale2))

        val salesGoodWorn = saleDB.execute(SaleQuery(conditions = setOf(WORN, GOOD))).get()
        assertTrue(salesGoodWorn.all { it.condition == WORN || it.condition == GOOD })
        assertTrue(salesGoodWorn.contains(sale2))
        assertTrue(salesGoodWorn.contains(sale1))

        assertSame(
            saleDB.execute(SaleQuery()).get(),
            saleDB.execute(SaleQuery(conditions = setOf(NEW, GOOD, WORN))).get()
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

        val salesActive = saleDB.execute(SaleQuery(states = setOf(ACTIVE))).get()
        assertTrue(salesActive.all { it.state == ACTIVE })
        assertTrue(salesActive.contains(sale1))

        val salesActiveRetracted =
            saleDB.execute(SaleQuery(states = setOf(ACTIVE, RETRACTED))).get()
        assertTrue(salesActiveRetracted.all { it.state == ACTIVE || it.state == RETRACTED })
        assertTrue(salesActiveRetracted.contains(sale2))
        assertTrue(salesActiveRetracted.contains(sale1))

        assertSame(
            saleDB.execute(SaleQuery()).get(),
            saleDB.execute(SaleQuery(states = setOf(ACTIVE, RETRACTED, CONCLUDED))).get()
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
            return saleDB.execute(SaleQuery(isbn = sale.book.isbn)).get().any {
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
    fun canSearchBothByStateAndCondition() {
        val saleRetractedWorn =
            saleDB.addSale(dummySale.copy(state = RETRACTED, condition = WORN)).get()
        val saleRetractedGood =
            saleDB.addSale(dummySale.copy(state = RETRACTED, condition = GOOD)).get()
        val saleActiveGood = saleDB.addSale(dummySale.copy(state = ACTIVE, condition = GOOD)).get()
        val saleActiveWorn = saleDB.addSale(dummySale.copy(state = ACTIVE, condition = WORN)).get()

        val res1 =
            saleDB.execute(SaleQuery(conditions = setOf(WORN), states = setOf(RETRACTED))).get()
        assertTrue(res1.contains(saleRetractedWorn))
        assertFalse(res1.contains(saleRetractedGood))
        assertFalse(res1.contains(saleActiveGood))
        assertFalse(res1.contains(saleActiveWorn))

        val res2 =
            saleDB.execute(SaleQuery(conditions = setOf(GOOD), states = setOf(RETRACTED))).get()
        assertTrue(res2.contains(saleRetractedGood))
        assertFalse(res2.contains(saleRetractedWorn))
        assertFalse(res2.contains(saleActiveWorn))
        assertFalse(res2.contains(saleActiveGood))

        val res3 =
            saleDB.execute(SaleQuery(conditions = setOf(GOOD), states = setOf(ACTIVE))).get()
        assertTrue(res3.contains(saleActiveGood))
        assertFalse(res3.contains(saleActiveWorn))
        assertFalse(res3.contains(saleRetractedGood))
        assertFalse(res3.contains(saleRetractedWorn))

        val res4 =
            saleDB.execute(SaleQuery(isbn = dummySale.book.isbn, conditions = setOf(WORN))).get()
        assertTrue(res4.contains(saleActiveWorn))
        assertTrue(res4.contains(saleRetractedWorn))
        assertFalse(res4.contains(saleActiveGood))
        assertFalse(res4.contains(saleRetractedGood))

        val res5 = saleDB.execute(SaleQuery(conditions = setOf(WORN), states = setOf(ACTIVE, RETRACTED))).get()
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
