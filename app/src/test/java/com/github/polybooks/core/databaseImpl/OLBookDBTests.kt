package com.github.polybooks.core.databaseImpl

import com.github.polybooks.utils.url2json
import org.junit.Test

import org.junit.Assert.*

class OLBookDBTests {

    @Test
    fun bookConversionWorks() {
        val json = url2json("https://openlibrary.org/books/OL32058322M.json").get()
        val book = parseBook(json)
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