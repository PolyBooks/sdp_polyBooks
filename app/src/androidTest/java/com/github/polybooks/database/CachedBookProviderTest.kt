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
                isbns.mapNotNull { backing[it] }
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
        val cached = CachedBookProvider(TestBookProvider, EmptyBookProvider)
        val future = cached.getBook("9782376863069")
        assertNotNull(future.get())
        assertEquals(TestBookProvider.getBook("9782376863069").get(), cached.getBook("9782376863069").get())
    }

    @Test
    fun canGetBookFromCache() {
        val cached = CachedBookProvider(EmptyBookProvider, TestBookProvider)
        val future = cached.getBook("9780156881807")
        assertNotNull(future.get())
        assertEquals(TestBookProvider.getBook("9780156881807").get(), cached.getBook("9780156881807").get())
    }

    @Test
    fun valueGetsCached() {
        val mapProvider = MapBookProvider()
        val cached = CachedBookProvider(TestBookProvider, mapProvider)
        assertNotNull(cached.getBook("9781985086593").get())
        assertNotNull(mapProvider.getBook("9781985086593"))
        assertEquals(TestBookProvider.getBook("9781985086593").get(), mapProvider.getBook("9781985086593").get())
    }


}