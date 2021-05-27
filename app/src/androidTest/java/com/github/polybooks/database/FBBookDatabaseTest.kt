package com.github.polybooks.database

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.activities.MainActivity
import com.github.polybooks.core.*
import junit.framework.AssertionFailedError
import org.junit.*
import org.junit.Assert.*
import java.io.FileNotFoundException
import java.util.*
import java.util.concurrent.CompletableFuture

class FBBookDatabaseTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val firestore = FirebaseProvider.getFirestore()
    private val fbBookDB = Database.bookDatabase

    //the OL book database wont return any useful information. will need to use firebase :)
    private val fbWithoutOL =
        FBBookDatabase(OLBookDatabase {
            CompletableFuture.supplyAsync { throw FileNotFoundException() }
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

    @Test
    fun usesOpenLibraryWhenBookNotStored() {

        fun deleteBook(isbn : String) : CompletableFuture<Unit> {
            val future = CompletableFuture<Unit>()
            firestore.collection("book")
                .document(isbn).delete()
                .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not delete $isbn")) }
                .addOnSuccessListener { future.complete(Unit) }
            return future
        }

        deleteBook("9782376863069").get()
        val future = fbBookDB.getBook("9782376863069")
        val book = future.get() ?: throw AssertionFailedError("Book was not fetched from OpenLibrary")

    }

    @Test
    fun canSearchByInterest() {

        fun setInterests(isbn: ISBN, interests : List<Interest>) : CompletableFuture<Unit> {
            //TODO use an interface to do that instead of reimplementing it here.
            val future = CompletableFuture<Unit>()
            val hashed = interests.map { it.hashCode() }
            firestore.collection("book")
                .document(isbn).update("interests",hashed)
                .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not set interests for $isbn.")) }
                .addOnSuccessListener { future.complete(Unit) }
            return future
        }

        val testBook: ISBN = "9780156881807"

        fun doTest(interest : Interest) {
            //Check it finds book associated with interest
            setInterests(testBook, listOf(interest)).get()
            val booksWithInterest1 =
                fbBookDB.queryBooks().onlyIncludeInterests(setOf(interest)).getAll().get()
            assertTrue("Should find $testBook when searching for $interest",
                booksWithInterest1.any { it.isbn == testBook } )

            //Check it doesnt show book not associated with interest
            setInterests(testBook, listOf()).get()
            val booksWithInterest2 =
                fbBookDB.queryBooks().onlyIncludeInterests(setOf(interest)).getAll().get()
            assertTrue("Should not find $testBook when searching for $interest",
                booksWithInterest2.none { it.isbn == testBook } )
        }

        //insure the book is in the database
        fbBookDB.getBook(testBook).get()!!
        doTest(Course("CS-101"))
        doTest(Field("Computer Science"))
        doTest(Semester("IN", "BA1"))

    }

    @Ignore("This is a test for a feature that is not yet implemented")
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

    @Test
    fun getAllBooksDoesntCrash() {
        fbBookDB.queryBooks().getAll().get()
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

    @Test
    fun searchByInterestDoesntCrash() {
        val future = fbBookDB.queryBooks().onlyIncludeInterests(setOf(Field("Test"))).getAll()
        val book = future.get()
    }

}