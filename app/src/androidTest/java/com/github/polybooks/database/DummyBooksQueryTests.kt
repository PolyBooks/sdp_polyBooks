package com.github.polybooks.database

import androidx.core.os.persistableBundleOf
import com.github.polybooks.core.Book
import com.github.polybooks.core.database.implementation.DummyBookQuery
import com.github.polybooks.core.database.interfaces.*
import com.github.polybooks.database.interfaces.BookOrdering
import com.github.polybooks.database.interfaces.BookQuery
import com.github.polybooks.database.interfaces.BookSettings
import com.google.firebase.Timestamp
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class DummyBooksQueryTests {

    private val query : BookQuery = DummyBookQuery()

    val default_books: List<Book> = listOf(
            Book("Book1", listOf("Tolkien"), "Lord of the Rings", "?", "?", "?", Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!), "?"),
            Book("Book2", listOf("Hugo"), "Les Miserables", "?", "?", "?", Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!), "?"),
            Book("Book3", listOf("Baudelaire"), "Les fleurs du mal", "?", "?", "?", Timestamp(com.github.polybooks.core.database.implementation.format.parse("2016-05-05")!!), "?")
    )

    @Test
    fun getAllWorks() {
        val q1 = query.getAll()
        assertEquals(default_books, q1.get())
    }

    @Test(expected = NotImplementedError::class)
    fun getNThrows() {
        val q1 = query.getN(3,3)
    }

    @Test(expected = NotImplementedError::class)
    fun getCountThrows() {
        val q1 = query.getCount()
    }

    @Test
    fun allFunctionsWork() {
        val q1 = query.onlyIncludeInterests(emptySet()).getAll()
        val q2 = query.searchByTitle("").getAll()
        val q3 = query.searchByISBN(setOf("blabla")).getAll()
        val q4 = query.searchByISBN(setOf("hello")).withOrdering(BookOrdering.TITLE_DEC).getAll()

        assertEquals(default_books, q1.get())
        assertEquals(default_books, q2.get())
        assertEquals(default_books, q3.get())
        assertEquals(default_books, q4.get())
    }

    @Test
    fun settingsTest() {
        val settingsRes = BookSettings(
            BookOrdering.DEFAULT,
                null, null, null)
        val settings = query.withOrdering(BookOrdering.TITLE_DEC).getSettings()
        assertEquals(settingsRes, settings)
    }

    @Test
    fun getSettingsAfterFromSettingsShouldCorrespond() {
        val settingsRes = BookSettings(
            BookOrdering.DEFAULT,
                null, null, null)

        val settings = query.fromSettings(settingsRes).getSettings()
        assertEquals(settingsRes, settings)
    }
}
