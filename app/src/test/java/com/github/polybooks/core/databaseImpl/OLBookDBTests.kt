package com.github.polybooks.core.databaseImpl

import com.github.polybooks.utils.url2json
import org.junit.Test

import org.junit.Assert.*

class OLBookDBTests {

    @Test
    fun canGetBookByISBN() {
        val olDB = OLBookDatabase()
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
        assertEquals(6,book.publishDate!!.month)
        assertEquals(2020-1900,book.publishDate!!.year)
        assertEquals(3,book.publishDate!!.date)
    }

    @Test
    fun weirdISBNFormatStillWork() {
        val olDB = OLBookDatabase()
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
        assertEquals(6,book.publishDate!!.month)
        assertEquals(2020-1900,book.publishDate!!.year)
        assertEquals(3,book.publishDate!!.date)
    }

    @Test
    fun isbn10alsoWorks() {
        val olDB = OLBookDatabase()
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
        assertEquals(6,book.publishDate!!.month)
        assertEquals(2020-1900,book.publishDate!!.year)
        assertEquals(3,book.publishDate!!.date)
    }

    @Test
    fun wrongISBNyieldsEmptyList() {
        val olDB = OLBookDatabase()
        val future = olDB.queryBooks().searchByISBN13("1234567890666").getAll()
        val books = future.get()
        assertEquals(0, books.size)
    }

    @Test
    fun countCorrect() {
        val olDB = OLBookDatabase()
        val query0 = olDB.queryBooks().searchByISBN13("1234567890666")
        assertEquals(0, query0.getCount().get())
        val query1 = olDB.queryBooks().searchByISBN13("9782376863069")
        assertEquals(1, query1.getCount().get())
    }

    @Test
    fun authorsAreCorrect() {
        val olDB = OLBookDatabase()
        val future = olDB.queryBooks().searchByISBN13("9782376863069").getAll()
        val book = future.get()[0]
        assertEquals(2, book.authors!!.size)
        assertEquals("Steven Brust", book.authors!![0])
        assertEquals("Megan Lindholm", book.authors!![1])
    }

    @Test
    fun getNalsoWorks() {
        val olDB = OLBookDatabase()
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
        assertEquals(6,book.publishDate!!.month)
        assertEquals(2020-1900,book.publishDate!!.year)
        assertEquals(3,book.publishDate!!.date)
    }
}