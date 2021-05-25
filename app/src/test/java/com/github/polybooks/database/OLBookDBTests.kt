package com.github.polybooks.database


import com.github.polybooks.utils.url2json
import junit.framework.AssertionFailedError
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class OLBookDBTests {

    val olDB = OLBookDatabase

    @Test
    fun canGetBookByISBN() {

        val future = olDB.getBook("9782376863069")
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
        val future = olDB.getBook("9781603090476")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
    }

    @Test
    fun canGetLanguage() {
        val future = olDB.getBook("9781603090476")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
        assertEquals("English", book.language)
    }

    @Test
    fun canGetEdition() {
        val future = olDB.getBook("0030137314")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
        assertEquals("2d ed.", book.edition)
    }

    @Test
    fun canGetBookWithNoFieldFullTitle() {
        val future = olDB.getBook("9780156881807")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
    }

    @Test
    fun weirdISBNFormatStillWork() {
        val future = olDB.getBook("  978-2376863069 ")
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
        val future = olDB.getBook("2376863066")
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
        val future = olDB.getBooks(setOf("1234567890666"))
        val books = future.get()

        assertEquals(0, books.size)
    }

    @Test
    fun authorsAreCorrect() {
        val future = olDB.getBook("9782376863069")
        val book = future.get()!!
        assertEquals(2, book.authors!!.size)
        assertEquals("Steven Brust", book.authors!![0])
        assertEquals("Megan Lindholm", book.authors!![1])
    }

    @Test
    fun getMultipleBooksWorks() {
        val future = olDB.getBooks(setOf("9782376863069", "9781985086593"))
        val books = future.get()
        assertEquals(2, books.size)
    }

    @Test
    fun getMultipleBooksWorks2() {
        val future = olDB.getBooks(setOf("9782376863069", "9781985086593", "1234567890666"))
        val books = future.get()
        assertEquals(2, books.size)
    }


    @Test
    fun rejectsWrongISBN1() {
        try {
            olDB.getBooks(setOf("this is no ISBN"))
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
            olDB.getBooks(setOf("1234"))
        } catch (e : IllegalArgumentException) {
            //success !
            return
        } catch (e : Exception) {
            throw AssertionFailedError("Expected IllegalArgumentException, got ${e.javaClass}")
        }
        throw AssertionFailedError("Expected IllegalArgumentException, got nothing")
    }

}
