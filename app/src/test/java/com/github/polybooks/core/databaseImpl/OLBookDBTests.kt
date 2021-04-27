package com.github.polybooks.core.databaseImpl

import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.github.polybooks.core.database.interfaces.BookOrdering
import com.github.polybooks.core.database.interfaces.BookSettings
import com.google.firebase.Timestamp
import com.google.gson.JsonParser
import junit.framework.AssertionFailedError
import org.junit.Test

import org.junit.Assert.*
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.CompletableFuture

class OLBookDBTests {

    val urlRegex = """.*openlibrary.org(.*)""".toRegex()
    val url2filename = mapOf(
        "/authors/OL7511250A.json" to "OL7511250A.json",
        "/authors/OL7482089A.json" to "OL7482089A.json",
        "/authors/OL8315711A.json" to  "OL8315711A.json",
        "/authors/OL8315712A.json" to  "OL8315712A.json",
        "/authors/OL6899222A.json" to "OL6899222A.json",
        "/authors/OL752714A.json" to "OL752714A.json",
        "/isbn/9782376863069.json" to "9782376863069.json",
        "/isbn/2376863069.json" to "9782376863069.json",
        "/isbn/9781985086593.json" to "9781985086593.json",
        "/isbn/9780156881807.json" to "9780156881807.json",
        "/isbn/9781603090476.json" to "9781603090476.json"
    )
    val baseDir = "src/test/java/com/github/polybooks/core/databaseImpl"
    val url2json = { url : String ->
        CompletableFuture.supplyAsync {
            val regexMatch = urlRegex.matchEntire(url) ?: throw FileNotFoundException("File Not Found : $url")
            val address = regexMatch.groups[1]?.value ?: throw Error("The regex is wrong")
            val filename = url2filename[address] ?: throw FileNotFoundException("File Not Found : $url")
            val file = File("$baseDir/$filename")
            val stream = file.inputStream()
            JsonParser.parseReader(InputStreamReader(stream))
        }
    }

    @Test
    fun canGetBookByISBN() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.getBook("9782376863069")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        val publishDate = Date(2020 -1900,6,3)
        assertEquals(Timestamp(publishDate), book.publishDate)
    }

    @Test
    fun canGetBookByISBN2() {
        //check for a book that does not have a precise publish date
        val olDB = OLBookDatabase(url2json)
        val future = olDB.getBook("9781603090476")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
    }

    @Test
    fun canGetBookWithNoFieldFullTitle() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.getBook("9780156881807")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
    }

    @Test
    fun weirdISBNFormatStillWork() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.getBook("  978-2376863069 ")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        val publishDate = Date(2020 -1900,6,3)
        assertEquals(Timestamp(publishDate), book.publishDate)
    }

    @Test
    fun isbn10alsoWorks() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.getBook("2376863069")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        val publishDate = Date(2020 -1900,6,3)
        assertEquals(Timestamp(publishDate), book.publishDate)
    }

    @Test
    fun wrongISBNyieldsEmptyList() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN(setOf("1234567890666")).getAll()
        val books = future.get()

        assertEquals(0, books.size)
    }

    @Test
    fun countCorrect() {
        val olDB = OLBookDatabase(url2json)
        val query0 = olDB.queryBooks().searchByISBN(setOf("1234567890666"))
        assertEquals(0, query0.getCount().get())
        val query1 = olDB.queryBooks().searchByISBN(setOf("9782376863069"))
        assertEquals(1, query1.getCount().get())
    }

    @Test
    fun authorsAreCorrect() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.getBook("9782376863069")
        val book = future.get()!!
        assertEquals(2, book.authors!!.size)
        assertEquals("Steven Brust", book.authors!![0])
        assertEquals("Megan Lindholm", book.authors!![1])
    }

    @Test
    fun getNalsoWorks() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN(setOf("9782376863069")).getN(1,0)
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
        assertEquals(Timestamp(publishDate), book.publishDate)
    }

    @Test
    fun getNalsoWorks2() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN(setOf("9781985086593", "9782376863069")).getN(1,1)
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
        assertEquals(Timestamp(publishDate), book.publishDate)
    }

    @Test
    fun getNalsoWorks3() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN(setOf("9781985086593", "9782376863069")).getN(1,0)
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("9781985086593", book.isbn)
    }

    @Test
    fun getNalsoWorks4() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN(setOf("9781985086593", "9782376863069")).getN(4,0)
        val books = future.get()
        assertEquals(2, books.size)
    }

    @Test
    fun getMultipleBooksWorks() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN(setOf("9782376863069", "9781985086593")).getAll()
        val books = future.get()
        assertEquals(2, books.size)
    }

    @Test
    fun getMultipleBooksWorks2() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN(setOf("9782376863069", "9781985086593", "1234567890666")).getAll()
        val books = future.get()
        assertEquals(2, books.size)
    }


    @Test
    fun rejectsWrongISBN1() {
        val olDB = OLBookDatabase(url2json)
        try {
            olDB.queryBooks().searchByISBN(setOf("this is no ISBN"))
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
        val olDB = OLBookDatabase(url2json)
        try {
            olDB.queryBooks().searchByISBN(setOf("1234"))
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
        val olDB = OLBookDatabase(url2json)
        try {
            olDB.queryBooks().getN(-4, 0)
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
        val olDB = OLBookDatabase(url2json)
        try {
            olDB.queryBooks().getN(1, -4)
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
        val olDB = OLBookDatabase(url2json)
        olDB.queryBooks().onlyIncludeInterests(Collections.emptyList())
        olDB.queryBooks().searchByTitle("title")
    }

    @Test
    fun getSettingsAndFromSettingsMatch() {
        val olDB = OLBookDatabase(url2json)
        val settings = BookSettings(
                BookOrdering.DEFAULT,
                listOf("9782376863069", "1234567890666"),
                "A Book",
                null // TODO update when interests are ready
        )
        assertEquals(
                settings,
                olDB.queryBooks().fromSettings(settings).getSettings()
        )
    }

    @Test
    fun settingsModifiesStateOfQuery() {
        val olDB = OLBookDatabase(url2json)
        val settings = BookSettings(
                BookOrdering.DEFAULT,
                listOf("9782376863069", "1234567890666"),null, null
        )
        assertNotEquals(
                olDB.queryBooks().fromSettings(settings).getCount().get(),
                olDB.queryBooks().searchByISBN(setOf("9782376863069", "9781985086593")).getCount().get()
        )
    }

    @Test
    fun settingsQueriesTheSameWayAsSearchByISBN() {
        val olDB = OLBookDatabase(url2json)
        val settings = BookSettings(
                BookOrdering.DEFAULT,
                listOf("9782376863069", "9781985086593"),null, null
        )

        assertEquals(
                olDB.queryBooks().fromSettings(settings).getCount().get(),
                olDB.queryBooks().searchByISBN(setOf("9782376863069", "9781985086593")).getCount().get()
        )
    }

}