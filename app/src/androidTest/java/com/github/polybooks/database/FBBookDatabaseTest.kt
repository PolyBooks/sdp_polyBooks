package com.github.polybooks.database

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.MainActivity
import com.github.polybooks.core.database.implementation.FBBookDatabase
import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.github.polybooks.utils.url2json
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.AssertionFailedError
import org.junit.*
import org.junit.Assert.*
import java.io.FileNotFoundException
import java.util.*
import java.util.concurrent.CompletableFuture

class FBBookDatabaseTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private val firebase = FirebaseFirestore.getInstance()
    private val olBookDB = OLBookDatabase{url2json(it)}
    private val fbBookDB = FBBookDatabase(firebase, olBookDB)

    //the OL book database wont return any useful information. will need to use firebase :)
    private val fbWithoutOL = FBBookDatabase(firebase, OLBookDatabase{
            CompletableFuture.supplyAsync{throw FileNotFoundException()
        }
    })

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun canGetBookByISBN() {
        val future = fbBookDB.getBook("9782376863069")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        val publishDate = Date(2020 - 1900, 6, 3)
        assertEquals(publishDate, book.publishDate!!.toDate())
    }

    @Test
    fun canGetBookByTitle() {
        fbBookDB.getBook("9780156881807").get() //insure at least one Tartuffe book is in the database
        val future = fbBookDB.queryBooks().searchByTitle("Tartuffe").getAll()
        val books = future.get()
        assertTrue(books.isNotEmpty())
        books.forEach {
            assertTrue(it.title.contains("Tartuffe"))
        }

    }

    @Test
    fun canGetBookByTitle2() {
        fbBookDB.getBook("9781985086593").get() //insure at least one OSTEP book is in the database
        val future = fbBookDB.queryBooks().searchByTitle("Operat").getAll()
        val books = future.get()
        assertTrue(books.isNotEmpty())
        books.forEach {
            assertTrue(it.title.contains("Operat"))
        }

    }

    @Test
    fun usesFirebaseAsCache() {
        //ensure the database had an opportunity to cache
        val getBookWithRegularDB = fbBookDB.getBook("9782376863069").get()
        val future = fbWithoutOL.getBook("9782376863069")
        val book = future.get() ?: throw AssertionFailedError("Book was not cached")
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        val publishDate = Date(2020 -1900,6,3)
        assertEquals(publishDate, book.publishDate!!.toDate())
    }

    @Test
    fun usesFirebaseAsCache2() {
        //ensure the database had an opportunity to cache
        val getBookWithRegularDB = fbBookDB.getBook("9780156881807").get()
        val future = fbWithoutOL.getBook("9780156881807")
        val book = future.get() ?: throw AssertionFailedError("Book was not cached")
    }

    @Ignore
    @Test
    fun isbn10alsoWorksWithoutOL() {
        //ensure the database had an opportunity to cache
        val getBookWithRegularDB = fbBookDB.getBook("2376863066").get()
        val future = fbWithoutOL.getBook("2376863066")
        val book = future.get() ?: throw AssertionFailedError("Firebase can't retrieve book with alternative ISBN")
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        val publishDate = Date(2020 -1900,6,3)
        assertEquals(publishDate, book.publishDate!!.toDate())
    }

    @Test
    fun wrongISBNyieldsEmptyList() {
        val future = fbBookDB.queryBooks().searchByISBN(setOf("1234567890666")).getAll()
        val books = future.get()

        assertEquals(0, books.size)
    }

    @Ignore
    @Test
    fun getNalsoWorks() {
        val future = fbBookDB.queryBooks().searchByISBN(setOf("9782376863069")).getN(1,0)
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        val publishDate = Date(2020 -1900,6,3)
        assertEquals(publishDate, book.publishDate!!.toDate())
    }

    @Ignore
    @Test
    fun getNalsoWorks2() {
        val future = fbBookDB.queryBooks().searchByISBN(setOf("9781985086593", "9782376863069")).getN(1,1)
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        val publishDate = Date(2020 -1900,6,3)
        assertEquals(publishDate, book.publishDate!!.toDate())
    }

    @Ignore
    @Test
    fun getNalsoWorks3() {
        val future = fbBookDB.queryBooks().searchByISBN(setOf("9781985086593", "9782376863069")).getN(1,0)
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("9781985086593", book.isbn)
    }

    @Ignore
    @Test
    fun getNalsoWorks4() {
        val future = fbBookDB.queryBooks().searchByISBN(setOf("9781985086593", "9782376863069")).getN(4,0)
        val books = future.get()
        assertEquals(2, books.size)
    }

    @Test
    fun getMultipleBooksWorks() {
        val future = fbBookDB.queryBooks().searchByISBN(setOf("9782376863069", "9781985086593")).getAll()
        val books = future.get()
        assertEquals(2, books.size)
    }

    @Test
    fun getMultipleBooksWorks2() {
        val future = fbBookDB.queryBooks().searchByISBN(setOf("9782376863069", "9781985086593", "1234567890666")).getAll()
        val books = future.get()
        assertEquals(2, books.size)
    }

}