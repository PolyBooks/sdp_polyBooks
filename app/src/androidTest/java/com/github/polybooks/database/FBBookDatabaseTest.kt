package com.github.polybooks.database

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.activities.MainActivity
import com.github.polybooks.core.*
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
            CompletableFuture.supplyAsync{ throw FileNotFoundException() }
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
            firebase.collection("book")
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
            firebase.collection("book")
                .document(isbn).update("interests",hashed)
                .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not set interests for $isbn")) }
                .addOnSuccessListener { future.complete(Unit) }
            return future
        }

        val testBook: ISBN = "9782889152728"

        fun doTest(interest : Interest) {
            //Check it finds book associated with interest
            setInterests(testBook, listOf(interest)).get()
            val booksWithInterest_1 =
                fbBookDB.queryBooks().onlyIncludeInterests(setOf(interest)).getAll().get()
            assertTrue("Should find $testBook when searching for $interest",
                booksWithInterest_1.any { it.isbn == testBook } )

            //Check it doesnt show book not associated with interest
            setInterests(testBook, listOf()).get()
            val booksWithInterest_2 =
                fbBookDB.queryBooks().onlyIncludeInterests(setOf(interest)).getAll().get()
            assertTrue("Should not find $testBook when searching for $interest",
                booksWithInterest_2.none { it.isbn == testBook } )
        }

        //insure the book is in the database
        fbBookDB.getBook(testBook).get()!!
        doTest(Course("CS-101"))
        doTest(Field("Computer Science"))
        doTest(Semester("IN", "BA1"))

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

    @Test
    fun getNalsoWorks3() {
        val future = fbBookDB.queryBooks().searchByISBN(setOf("9781985086593", "9782376863069")).getN(1,0)
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("9781985086593", book.isbn)
    }

    @Test
    fun getNalsoWorks4() {
        val future = fbBookDB.queryBooks().searchByISBN(setOf("9781985086593", "9782376863069")).getN(4,0)
        val books = future.get()
        assertEquals(2, books.size)
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

    @Test
    fun canModifyBook() {
        val bookTartuffe: Book? = fbBookDB.getBook("9780156881807").get()
        assertNotNull(bookTartuffe)

        val modifiedBook: Book = Book(
            bookTartuffe!!.isbn, bookTartuffe.authors, bookTartuffe.title, bookTartuffe.edition,
            bookTartuffe.language, bookTartuffe.publisher, bookTartuffe.publishDate, bookTartuffe.format,
            (bookTartuffe.totalStars ?: 0.0) + 4.0, (bookTartuffe.numberVotes ?: 0) + 1
        )
        fbBookDB.addBook(modifiedBook).get()

        val book: Book? = fbBookDB.getBook(modifiedBook.isbn).get()
        assertNotNull(book)

        assertEquals(modifiedBook.title, book!!.title)
        assertEquals(modifiedBook.isbn, book.isbn)
        assertEquals(modifiedBook.totalStars, book.totalStars)
        assertEquals(modifiedBook.numberVotes, book.numberVotes)
    }
}