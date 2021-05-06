package com.github.polybooks.core.databaseImpl


import com.github.polybooks.GlobalConstants.OLbookDB
import com.github.polybooks.database.implementation.OLBookDatabase
import com.github.polybooks.database.interfaces.BookOrdering
import com.github.polybooks.database.interfaces.BookSettings
import com.google.gson.JsonParser
import junit.framework.AssertionFailedError
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.CompletableFuture

class OLBookDBTests {

    @Test
    fun canGetBookByISBN() {
        val future = OLbookDB.getBook("9782376863069")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
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
    fun canGetBookByISBN2() {
        //check for a book that does not have a precise publish date
        val future = OLbookDB.getBook("9781603090476")
        future.get() ?: throw AssertionFailedError("Book was not found")
    }

    @Test
    fun canGetBookWithNoFieldFullTitle() {
        val future = OLbookDB.getBook("9780156881807")
        future.get() ?: throw AssertionFailedError("Book was not found")
    }

    @Test
    fun weirdISBNFormatStillWork() {
        val future = OLbookDB.getBook("  978-2376863069 ")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
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
    fun isbn10alsoWorks() {
        val future = OLbookDB.getBook("2376863066")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
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
        val future = OLbookDB.queryBooks().searchByISBN(setOf("1234567890666")).getAll()
        val books = future.get()

        assertEquals(0, books.size)
    }

    @Test
    fun countCorrect() {
        val query0 = OLbookDB.queryBooks().searchByISBN(setOf("1234567890666"))
        assertEquals(0, query0.getCount().get())
        val query1 = OLbookDB.queryBooks().searchByISBN(setOf("9782376863069"))
        assertEquals(1, query1.getCount().get())
    }

    @Test
    fun authorsAreCorrect() {
        val future = OLbookDB.getBook("9782376863069")
        val book = future.get()!!
        assertEquals(2, book.authors!!.size)
        assertEquals("Steven Brust", book.authors!![0])
        assertEquals("Megan Lindholm", book.authors!![1])
    }

    @Test
    fun getNalsoWorks() {
        val future = OLbookDB.queryBooks().searchByISBN(setOf("9782376863069")).getN(1,0)
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
        val future = OLbookDB.queryBooks().searchByISBN(setOf("9781985086593", "9782376863069")).getN(1,1)
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        //This hurts, but otherwise we have problems with CEST and CET
        val publishDate = Date(2020 -1900,6,3)
        assertEquals(publishDate, book.publishDate!!.toDate())
    }

    @Test
    fun getNalsoWorks3() {
        val future = OLbookDB.queryBooks().searchByISBN(setOf("9781985086593", "9782376863069")).getN(1,0)
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("9781985086593", book.isbn)
    }

    @Test
    fun getNalsoWorks4() {
        val future = OLbookDB.queryBooks().searchByISBN(setOf("9781985086593", "9782376863069")).getN(4,0)
        val books = future.get()
        assertEquals(2, books.size)
    }

    @Test
    fun getMultipleBooksWorks() {
        val future = OLbookDB.queryBooks().searchByISBN(setOf("9782376863069", "9781985086593")).getAll()
        val books = future.get()
        assertEquals(2, books.size)
    }

    @Test
    fun getMultipleBooksWorks2() {
        val future = OLbookDB.queryBooks().searchByISBN(setOf("9782376863069", "9781985086593", "1234567890666")).getAll()
        val books = future.get()
        assertEquals(2, books.size)
    }


    @Test
    fun rejectsWrongISBN1() {
        try {
            OLbookDB.queryBooks().searchByISBN(setOf("this is no ISBN"))
        } catch (e : IllegalArgumentException) {
            //success !
            return
        } catch (e : Exception) {
            throw AssertionFailedError("Expected IllegalArgumentException, got ${e.javaClass}")
        }
        throw AssertionFailedError("Expected IllegalArgumentException, got nothing")
    }

    @Test
    fun rejectsWrongISBN2() {
        try {
            OLbookDB.queryBooks().searchByISBN(setOf("1234"))
        } catch (e : IllegalArgumentException) {
            //success !
            return
        } catch (e : Exception) {
            throw AssertionFailedError("Expected IllegalArgumentException, got ${e.javaClass}")
        }
        throw AssertionFailedError("Expected IllegalArgumentException, got nothing")
    }

    @Test
    fun rejectWrongN() {
        try {
            OLbookDB.queryBooks().getN(-4, 0)
        } catch (e : IllegalArgumentException) {
            //success !
            return
        } catch (e : Exception) {
            throw AssertionFailedError("Expected IllegalArgumentException, got ${e.javaClass}")
        }
        throw AssertionFailedError("Expected IllegalArgumentException, got nothing")
    }

    @Test
    fun rejectWrongPage() {
        try {
            OLbookDB.queryBooks().getN(1, -4)
        } catch (e : IllegalArgumentException) {
            //success !
            return
        } catch (e : Exception) {
            throw AssertionFailedError("Expected IllegalArgumentException, got ${e.javaClass}")
        }
        throw AssertionFailedError("Expected IllegalArgumentException, got nothing")
    }

    @Test
    fun onlyIncludeXdontFail() {
        OLbookDB.queryBooks().onlyIncludeInterests(Collections.emptyList())
        OLbookDB.queryBooks().searchByTitle("title")
    }

    @Test
    fun getSettingsAndFromSettingsMatch() {
        val settings = BookSettings(
                BookOrdering.DEFAULT,
                listOf("9782376863069", "1234567890666"),
                "A Book",
                null // TODO update when interests are ready
        )
        assertEquals(
                settings,
            OLbookDB.queryBooks().fromSettings(settings).getSettings()
        )
    }

    @Test
    fun settingsModifiesStateOfQuery() {
        val settings = BookSettings(
                BookOrdering.DEFAULT,
                listOf("9782376863069", "1234567890666"),null, null
        )
        assertNotEquals(
            OLbookDB.queryBooks().fromSettings(settings).getCount().get(),
            OLbookDB.queryBooks().searchByISBN(setOf("9782376863069", "9781985086593")).getCount().get()
        )
    }

    @Test
    fun settingsQueriesTheSameWayAsSearchByISBN() {
        val settings = BookSettings(
                BookOrdering.DEFAULT,
                listOf("9782376863069", "9781985086593"),null, null
        )

        assertEquals(
            OLbookDB.queryBooks().fromSettings(settings).getCount().get(),
            OLbookDB.queryBooks().searchByISBN(setOf("9782376863069", "9781985086593")).getCount().get()
        )
    }

}
