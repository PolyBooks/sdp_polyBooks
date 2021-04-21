package com.github.polybooks.database

import android.util.Log
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.MainActivity
import com.github.polybooks.core.*
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleSettings
import com.github.polybooks.core.database.LocalUserException
import com.github.polybooks.utils.anonymousBook
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

    private val testSaleName = "test-123456"

    private val format : DateFormat = SimpleDateFormat("yyyy-mm-dd")

    private val dummySale: MutableMap<String, Any> = HashMap()

    init {
        dummySale[SaleFields.BOOK.fieldName] = anonymousBook("test-book-6Zzn8ZNPeK-cXFEfCvqbs-spf0eCVEqa")
        dummySale[SaleFields.PRICE.fieldName] = 500f
        dummySale[SaleFields.CONDITION.fieldName] = BookCondition.WORN
        dummySale[SaleFields.STATE.fieldName] = SaleState.ACTIVE
        dummySale[SaleFields.PUBLICATION_DATE.fieldName] = Timestamp(format.parse("2016-05-05")!!)
        dummySale[SaleFields.SELLER.fieldName] = LoggedUser(301966, "Le givre")
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
    fun t_listExistingOrderings() {
        assertEquals(SaleOrdering.values().toList(), db.listExistingOrderings())
    }

    @Test
    fun t_listExistingStates() {
        assertEquals(SaleState.values().toList(), db.listExistingStates())
    }

    @Test
    fun t_listExistingBookConditions() {
        assertEquals(BookCondition.values().toList(), db.listExistingsBookConditions())
    }

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

        val isolatedAddition = listAllSales.filter { s -> s.book == dummySale[SaleFields.BOOK.fieldName] }
        assertEquals(1, isolatedAddition.size)
        assertEquals(dummySale[SaleFields.BOOK.fieldName], isolatedAddition[0].book)
        assertEquals(dummySale[SaleFields.PRICE.fieldName], isolatedAddition[0].price)
        assertEquals(dummySale[SaleFields.CONDITION.fieldName], isolatedAddition[0].condition)
        assertEquals(dummySale[SaleFields.STATE.fieldName], isolatedAddition[0].state)
        assertEquals(dummySale[SaleFields.PUBLICATION_DATE.fieldName], isolatedAddition[0].date)
        assertEquals(dummySale[SaleFields.SELLER.fieldName], isolatedAddition[0].seller)
    }

    @Test
    fun t_searchByTitle() {
        val initialCount: Int = db.querySales().searchByTitle((dummySale[SaleFields.BOOK.fieldName] as Book).title).getCount().get()
        addDummySaleTest()
        val secondCount: Int = db.querySales().searchByTitle((dummySale[SaleFields.BOOK.fieldName] as Book).title).getCount().get()

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

        addDummySaleTest()
        future = db.querySales().getN(1, 0)
        assertTrue(future.get().size <= 1)
    }

    @Test
    fun addAsLocalUser(){
        val saleTest = Sale(anonymousBook("test-tqwjdhsfalkfdhjasdhlfkahdfjklhdjhfl.adfjasdhflka-adjklshfjklasdhfjklhasd"),
                LocalUser, 666f,
                BookCondition.WORN,
                Timestamp(format.parse("2016-05-05")!!),
                SaleState.RETRACTED, null )
        try {
            db.addSale(saleTest)
        }catch (e: LocalUserException) {
           assertEquals("Cannot add sale as LocalUser", e.message)
        } catch (e: Throwable) {
            fail("Wrong exception type")
        }
    }

    @Test
    fun deleteAsLocalUser(){
        val saleTest = Sale(anonymousBook("test-tqwjdhsfalkfdhjasdhlfkahdfjklhdjhfl.adfjasdhflka-adjklshfjklasdhfjklhasd"),
                LocalUser, 666f,
                BookCondition.WORN,
                Timestamp(format.parse("2016-05-05")!!),
                SaleState.RETRACTED, null )
        try {
            db.deleteSale(saleTest)
        }catch (e: LocalUserException) {
            assertEquals("Cannot add sale as LocalUser", e.message)
        } catch (e: Throwable) {
            fail("Wrong exception type")
        }
    }

    @Test
    fun addDelete(){
        val saleTest = Sale(anonymousBook("test-tqwjdhsfalkfdhjasdhlfkahdfjklhdjhfl.adfjasdhflka-adjklshfjklasdhfjklhasd"),
            LoggedUser(301943, "The best"), 666f,
            BookCondition.WORN,
            Timestamp(format.parse("2016-05-05")!!),
            SaleState.RETRACTED, null )
        db.deleteSale(saleTest)
        BaristaSleepInteractions.sleep(2000, TimeUnit.MILLISECONDS)
        assertEquals(0,db.querySales().searchByTitle(saleTest.book.title).getCount().get())
        db.addSale(saleTest)
        BaristaSleepInteractions.sleep(2000, TimeUnit.MILLISECONDS)
        assertEquals( listOf(saleTest), db.querySales().searchByTitle(saleTest.book.title).getAll().get())
        db.deleteSale(saleTest)
        BaristaSleepInteractions.sleep(2000, TimeUnit.MILLISECONDS)
        assertEquals(0,db.querySales().searchByTitle(saleTest.book.title).getCount().get())
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
            SaleOrdering.DEFAULT, null,null, setOf(Field("Biology")),
            null, null, null,null
        )

        assertEquals(
                db.querySales().fromSettings(settings).getCount().get(),
                db.querySales().onlyIncludeInterests(setOf(Field("Biology"))).getCount().get()
        )
    }

    @Test
    fun settingsQueriesTheSameWayAsQueryFunctions() {
        val interests = setOf(Field("Biology"), Course("COM-301"))
        val minPrice = 0.0f
        val maxPrice = 10.0f

        var settings = SaleSettings(
                SaleOrdering.DEFAULT, null, null, interests, null
                ,null, minPrice, maxPrice
        )
        assertEquals(
                db.querySales().fromSettings(settings).getCount().get(),
                db.querySales()
                        .onlyIncludeInterests(interests)
                        .searchByPrice(minPrice, maxPrice).getCount().get()

        )
    }

    @Ignore
    @Test
    fun Delete(){
        //Used to manually delete sales
        val saleTest = Sale(anonymousBook("test1"),
                LoggedUser(301943, "The best"),
                666f,
                BookCondition.WORN,
                Timestamp(format.parse("2016-05-05")!!),
                SaleState.RETRACTED, null )
        db.deleteSale(saleTest)
    }
    @Ignore
    @Test
    fun See(){
        fun snapshotToBook(map: HashMap<String,Any>): Book {
            return Book(
                    map[BookFields.ISBN.fieldName] as String,
                    map[BookFields.AUTHORS.fieldName] as List<String>?,
                    map[BookFields.TITLE.fieldName] as String,
                    map[BookFields.EDITION.fieldName] as String?,
                    map[BookFields.LANGUAGE.fieldName] as String?,
                    map[BookFields.PUBLISHER.fieldName] as String?,
                    map[BookFields.PUBLISHDATE.fieldName] as java.sql.Timestamp?,
                    map[BookFields.FORMAT.fieldName] as String?
            )

        }
        saleRef.whereEqualTo("book.title", "test1").get().addOnSuccessListener { documents ->
            val book = documents.map { document ->
                document.get(SaleFields.BOOK.fieldName) as HashMap<String, Any>//as Book
            }
            
            println(book)
        }
    }

    @Ignore
    @Test
    fun Add(){
        //Used to manually insert sales
        val saleTest = Sale(anonymousBook("Phisics for dummies"),
                LoggedUser(301966, "La chevre"),
                49.5f,
                BookCondition.NEW,
                Timestamp(format.parse("2022-01-01")!!),
                SaleState.ACTIVE, null )
        db.addSale(saleTest)
    }


}
