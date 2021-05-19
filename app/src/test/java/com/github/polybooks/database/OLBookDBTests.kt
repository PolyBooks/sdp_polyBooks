package com.github.polybooks.database


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

    val urlRegex = """.*openlibrary.org(.*)""".toRegex()
    val url2filename = mapOf(
        "/languages/eng.json" to "eng.json",
        "/languages/fre.json" to "fre.json",
        "/authors/OL52830A.json" to "OL52830A.json",
        "/authors/OL7511250A.json" to "OL7511250A.json",
        "/authors/OL7482089A.json" to "OL7482089A.json",
        "/authors/OL8315711A.json" to "OL8315711A.json",
        "/authors/OL8315712A.json" to "OL8315712A.json",
        "/authors/OL6899222A.json" to "OL6899222A.json",
        "/authors/OL752714A.json" to "OL752714A.json",
        "/isbn/0030137314.json" to "0030137314.json",
        "/isbn/9782376863069.json" to "9782376863069.json",
        "/isbn/2376863066.json" to "9782376863069.json",
        "/isbn/9781985086593.json" to "9781985086593.json",
        "/isbn/9780156881807.json" to "9780156881807.json",
        "/isbn/9781603090476.json" to "9781603090476.json"
    )
    val baseDir = "src/test/java/com/github/polybooks/database"
    val url2json = { url: String ->
        CompletableFuture.supplyAsync {
            val regexMatch =
                urlRegex.matchEntire(url) ?: throw FileNotFoundException("File Not Found : $url")
            val address = regexMatch.groups[1]?.value ?: throw Error("The regex is wrong")
            val filename =
                url2filename[address] ?: throw FileNotFoundException("File Not Found : $url")
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
        val publishDate = Date(2020 - 1900, 6, 3)
        assertEquals(publishDate, book.publishDate!!.toDate())
    }

    @Test
    fun canGetBookByISBN2() {
        //check for a book that does not have a precise publish date
        val olDB = OLBookDatabase(url2json)
        val future = olDB.getBook("9781603090476")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
    }

    @Test
    fun canGetLanguage() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.getBook("9781603090476")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
        assertEquals("English", book.language)
    }

    @Test
    fun canGetEdition() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.getBook("0030137314")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
        assertEquals("2d ed.", book.edition)
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
        val publishDate = Date(2020 - 1900, 6, 3)
        assertEquals(publishDate, book.publishDate!!.toDate())
    }

    @Test
    fun isbn10alsoWorks() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.getBook("2376863066")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        val publishDate = Date(2020 - 1900, 6, 3)
        assertEquals(publishDate, book.publishDate!!.toDate())
    }

    @Test
    fun wrongISBNyieldsEmptyList() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.execute(BookQuery(isbns = setOf("1234567890666")))
        val books = future.get()

        assertEquals(0, books.size)
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
    fun getMultipleBooksWorks() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.execute(BookQuery(isbns = setOf("9782376863069", "9781985086593")))
        val books = future.get()
        assertEquals(2, books.size)
    }

    @Test
    fun getMultipleBooksWorks2() {
        val olDB = OLBookDatabase(url2json)
        val future = olDB.execute(
            BookQuery(
                isbns = setOf(
                    "9782376863069",
                    "9781985086593",
                    "1234567890666"
                )
            )
        )
        val books = future.get()
        assertEquals(2, books.size)
    }


    @Test
    fun rejectsWrongISBN1() {
        val olDB = OLBookDatabase(url2json)
        try {
            olDB.execute(BookQuery(isbns = setOf("this is no ISBN")))
        } catch (e: IllegalArgumentException) {
            //success !
            return
        } catch (e: Exception) {
            throw AssertionFailedError("Expected IllegalArgumentException, got ${e.javaClass}")
        }
        throw AssertionFailedError("Expected IllegalArgumentException, got nothing")
    }

    @Test
    fun rejectsWrongISBN2() {
        val olDB = OLBookDatabase(url2json)
        try {
            olDB.execute(BookQuery(isbns = setOf("1234")))
        } catch (e: IllegalArgumentException) {
            //success !
            return
        } catch (e: Exception) {
            throw AssertionFailedError("Expected IllegalArgumentException, got ${e.javaClass}")
        }
        throw AssertionFailedError("Expected IllegalArgumentException, got nothing")
    }

}
