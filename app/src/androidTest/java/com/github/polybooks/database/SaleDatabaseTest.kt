package com.github.polybooks.database

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.MainActivity
import com.github.polybooks.core.Book
import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.Sale
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleFields
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.ExpectedException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class SaleDatabaseTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private val saleRef: CollectionReference = FirebaseFirestore.getInstance().collection("sale")
    private val db = SaleDatabase()

    private val testSaleName = "test-123456"


    private val format : DateFormat = SimpleDateFormat("yyyy-mm-dd")

    private val dummySale: MutableMap<String, Any> = HashMap()

    init {
        dummySale[SaleFields.TITLE.fieldName] = "test-book-6Zzn8ZNPeK-cXFEfCvqbs-spf0eCVEqa"
        dummySale[SaleFields.PRICE.fieldName] = 500f
        dummySale[SaleFields.CONDITION.fieldName] = BookCondition.WORN
        dummySale[SaleFields.STATE.fieldName] = SaleState.ACTIVE
        dummySale[SaleFields.PUBLICATION_DATE.fieldName] = Timestamp(com.github.polybooks.database.format.parse("2016-05-05")!!)
        dummySale[SaleFields.SELLER.fieldName] = 301966
    }


    fun addDummySaleTest(payload: MutableMap<String, Any>? = null) {
        if (payload != null) {
            saleRef.document(testSaleName).set(payload)
        } else {


            saleRef.document(testSaleName).set(dummySale)
        }

        BaristaSleepInteractions.sleep(250, TimeUnit.MILLISECONDS)
    }

    fun deleteDummySaleTest() {
        saleRef.document(testSaleName).delete()
        BaristaSleepInteractions.sleep(250, TimeUnit.MILLISECONDS)
    }



    @Before fun setUp() {
        Intents.init()
    }

    @After fun cleanUp() {
        deleteDummySaleTest()
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

        // Adding a lonely dummy test sale
        addDummySaleTest()

        listAllSales = db.listAllSales().get()
        assertEquals(expectedSize + 1, listAllSales.size)

        val isolatedAddition = listAllSales.filter { s -> s.title == dummySale[SaleFields.TITLE.fieldName] }
        assertEquals(1, isolatedAddition.size)
        assertEquals(dummySale[SaleFields.TITLE.fieldName], isolatedAddition[0].title)
        assertEquals(dummySale[SaleFields.PRICE.fieldName], isolatedAddition[0].price)
        assertEquals(dummySale[SaleFields.CONDITION.fieldName], isolatedAddition[0].condition)
        assertEquals(dummySale[SaleFields.STATE.fieldName], isolatedAddition[0].state)
        assertEquals(dummySale[SaleFields.PUBLICATION_DATE.fieldName], isolatedAddition[0].date)
        assertEquals(dummySale[SaleFields.SELLER.fieldName], isolatedAddition[0].seller)
    }

    @Test
    fun t_searchByTitle() {
        val initialCount: Int = db.querySales().searchByTitle(dummySale[SaleFields.TITLE.fieldName].toString()).getCount().get()
        addDummySaleTest()
        val secondCount: Int = db.querySales().searchByTitle(dummySale[SaleFields.TITLE.fieldName].toString()).getCount().get()

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
                db.querySales().searchByCondition(emptyList()).getCount().get()
        )

        assertEquals(
                db.listAllSales().get().filter { s -> s.condition == BookCondition.NEW }.size,
                db.querySales().searchByCondition(listOf(BookCondition.NEW)).getCount().get()
        )

        assertEquals(
                db.listAllSales().get().filter { s -> (s.condition == BookCondition.NEW || s.condition == BookCondition.WORN)}.size,
                db.querySales().searchByCondition(listOf(BookCondition.NEW, BookCondition.WORN)).getCount().get()
        )

        assertEquals(
                db.querySales().getCount().get(),
                db.querySales().searchByCondition(listOf(BookCondition.NEW, BookCondition.GOOD, BookCondition.WORN)).getCount().get()
        )
    }

    @Test
    fun t_searchByState() {
        // empty collection should be ignored
        assertEquals(
                db.querySales().getCount().get(),
                db.querySales().searchByState(emptyList()).getCount().get()
        )

        assertEquals(
                db.listAllSales().get().filter { s -> s.state == SaleState.ACTIVE }.size,
                db.querySales().searchByState(listOf(SaleState.ACTIVE)).getCount().get()
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

        addDummySaleTest()
        future = db.querySales().getN(1, 0)
        assertTrue(future.get().size <= 1)
    }

    @Test
    fun addDelete(){
        val saleTest = Sale("test-tqwjdhsfalkfdhjasdhlfkahdfjklhdjhfl.adfjasdhflka-adjklshfjklasdhfjklhasd",
            301943, 666f,
            BookCondition.WORN,
            Timestamp(com.github.polybooks.database.format.parse("2016-05-05")!!),
            SaleState.RETRACTED )
        db.deleteSale(saleTest)
        BaristaSleepInteractions.sleep(2000, TimeUnit.MILLISECONDS)
        assertEquals(0,db.querySales().searchByTitle(saleTest.title).getCount().get())
        db.addSale(saleTest)
        BaristaSleepInteractions.sleep(2000, TimeUnit.MILLISECONDS)
        assertEquals( listOf(saleTest), db.querySales().searchByTitle(saleTest.title).getAll().get())
        db.deleteSale(saleTest)
        BaristaSleepInteractions.sleep(2000, TimeUnit.MILLISECONDS)
        assertEquals(0,db.querySales().searchByTitle(saleTest.title).getCount().get())


    }
    @Test
    fun Delete(){
        val saleTest = Sale("test-tqwjdhsfalkfdhjasdhlfkahdfjklhdjhfl.adfjasdhflka-adjklshfjklasdhfjklhasd",
            301943, 666f,
            BookCondition.WORN,
            Timestamp(com.github.polybooks.database.format.parse("2016-05-05")!!),
            SaleState.RETRACTED )
        db.deleteSale(saleTest)
    }


}