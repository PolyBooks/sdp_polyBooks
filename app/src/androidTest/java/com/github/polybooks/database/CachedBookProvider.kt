package com.github.polybooks.database

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.activities.MainActivity
import com.github.polybooks.core.Book
import com.github.polybooks.core.ISBN
import org.junit.*
import org.junit.Assert.*
import java.util.concurrent.CompletableFuture

class CachedBookProviderTests {

    companion object {
        private val usedBooks = listOf("9782376863069", "9780156881807", "9781985086593")
        @BeforeClass
        @JvmStatic
        fun initDB() {
            usedBooks.forEach { book -> FBBookDatabase.addBook(OLBookDatabase.getBook(book).get()!!) }
        }
    }

    object EmptyBookProvider : BookProvider {
        override fun getBooks(
            isbns: Collection<ISBN>,
            ordering: BookOrdering
        ): CompletableFuture<List<Book>> {
            return CompletableFuture.completedFuture(listOf())
        }

        override fun addBook(book: Book): CompletableFuture<Unit> {
            return CompletableFuture.completedFuture(Unit)
        }
    }

    class MapBookProvider : BookProvider {

        val backing = mutableMapOf<String, Book>()

        override fun getBooks(
            isbns: Collection<ISBN>,
            ordering: BookOrdering
        ): CompletableFuture<List<Book>> {
            return CompletableFuture.supplyAsync {
                isbns.flatMap { backing[it]?.let { listOf(it) } ?: listOf() }
            }
        }

        override fun addBook(book: Book): CompletableFuture<Unit> {
            backing[book.isbn] = book
            return CompletableFuture.completedFuture(Unit)
        }

    }

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun canGetBookFromBacking() {
        val cached = CachedBookProvider(FBBookDatabase, EmptyBookProvider)
        val future = cached.getBook("9782376863069")
        assertNotNull(future.get())
        assertEquals(FBBookDatabase.getBook("9782376863069").get(), cached.getBook("9782376863069").get())
    }

    @Test
    fun canGetBookFromCache() {
        val cached = CachedBookProvider(EmptyBookProvider, FBBookDatabase)
        val future = cached.getBook("9780156881807")
        assertNotNull(future.get())
        assertEquals(FBBookDatabase.getBook("9780156881807").get(), cached.getBook("9780156881807").get())
    }

    @Test
    fun valueGetsCached() {
        val mapProvider = MapBookProvider()
        val cached = CachedBookProvider(FBBookDatabase, mapProvider)
        assertNotNull(cached.getBook("9781985086593").get())
        assertNotNull(mapProvider.getBook("9781985086593"))
        assertEquals(FBBookDatabase.getBook("9781985086593").get(), mapProvider.getBook("9781985086593").get())
    }


}