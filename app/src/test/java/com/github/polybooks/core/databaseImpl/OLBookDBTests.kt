package com.github.polybooks.core.databaseImpl

import com.github.polybooks.core.database.implementation.OLBookDatabase
import junit.framework.AssertionFailedError
import org.junit.Test

import org.junit.Assert.*
import java.lang.Exception
import java.lang.IllegalArgumentException

class OLBookDBTests {

    val url2json = { url : String -> com.github.polybooks.utils.url2json(url)}

    @Test
    fun canGetBookByISBN() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN13("9782376863069").getAll()
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn13)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        assertEquals(6,book.publishDate!!.toDate().month)
        assertEquals(2020-1900,book.publishDate!!.toDate().year)
        assertEquals(3,book.publishDate!!.toDate().date)
    }

    @Test
    fun weirdISBNFormatStillWork() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN13("  978-2376863069 ").getAll()
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn13)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        assertEquals(6,book.publishDate!!.toDate().month)
        assertEquals(2020-1900,book.publishDate!!.toDate().year)
        assertEquals(3,book.publishDate!!.toDate().date)
    }

    @Test
    fun isbn10alsoWorks() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN13("2376863066").getAll()
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn13)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        assertEquals(6,book.publishDate!!.toDate().month)
        assertEquals(2020-1900,book.publishDate!!.toDate().year)
        assertEquals(3,book.publishDate!!.toDate().date)
    }

    @Test
    fun wrongISBNyieldsEmptyList() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN13("1234567890666").getAll()
        val books = future.get()

        assertEquals(0, books.size)
    }

    @Test
    fun countCorrect() {
        val olDB = OLBookDatabase(url2json)
        val query0 = olDB.queryBooks().searchByISBN13("1234567890666")
        assertEquals(0, query0.getCount().get())
        val query1 = olDB.queryBooks().searchByISBN13("9782376863069")
        assertEquals(1, query1.getCount().get())
    }

    @Test
    fun authorsAreCorrect() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN13("9782376863069").getAll()
        val book = future.get()[0]
        assertEquals(2, book.authors!!.size)
        assertEquals("Steven Brust", book.authors!![0])
        assertEquals("Megan Lindholm", book.authors!![1])
    }

    @Test
    fun getNalsoWorks() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.queryBooks().searchByISBN13("9782376863069").getN(1,0)
        val books = future.get()
        assertEquals(1, books.size)
        val book = books[0]
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn13)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        assertEquals(6,book.publishDate!!.toDate().month)
        assertEquals(2020-1900,book.publishDate!!.toDate().year)
        assertEquals(3,book.publishDate!!.toDate().date)
    }

    @Test
    fun rejectsWrongISBN1() {
        val olDB = OLBookDatabase(url2json)
        try {
            olDB.queryBooks().searchByISBN13("this is no ISBN")
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
            olDB.queryBooks().searchByISBN13("1234")
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

}