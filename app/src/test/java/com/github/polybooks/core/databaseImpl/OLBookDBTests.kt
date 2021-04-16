package com.github.polybooks.core.databaseImpl

import com.github.polybooks.core.database.implementation.OLBookDatabase
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
        Pair("/authors/OL7511250A.json", "OL7511250A.json"),
        Pair("/authors/OL7482089A.json", "OL7482089A.json"),
        Pair("/isbn/9782376863069.json", "9782376863069.json"),
        Pair("/isbn/2376863066.json", "9782376863069.json")
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
        val future = olDB.queryBooks().searchByISBN(setOf("9782376863069")).getAll()
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn13)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        assertEquals(6,book.publishDate!!.month)
        assertEquals(2020-1900,book.publishDate!!.year)
        assertEquals(3,book.publishDate!!.date)
    }

    @Test
    fun weirdISBNFormatStillWork() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN(setOf("  978-2376863069 ")).getAll()
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn13)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        assertEquals(6,book.publishDate!!.month)
        assertEquals(2020-1900,book.publishDate!!.year)
        assertEquals(3,book.publishDate!!.date)
    }

    @Test
    fun isbn10alsoWorks() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.getBook("2376863066")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn13)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        assertEquals(6,book.publishDate!!.month)
        assertEquals(2020-1900,book.publishDate!!.year)
        assertEquals(3,book.publishDate!!.date)
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
        val future = olDB.queryBooks().searchByISBN(setOf("9782376863069")).getAll()
        val book = future.get()[0]
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
        assertEquals("9782376863069", book.isbn13)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        assertEquals(6,book.publishDate!!.month)
        assertEquals(2020-1900,book.publishDate!!.year)
        assertEquals(3,book.publishDate!!.date)
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
            olDB.queryBooks().getN(0, -4)
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

}