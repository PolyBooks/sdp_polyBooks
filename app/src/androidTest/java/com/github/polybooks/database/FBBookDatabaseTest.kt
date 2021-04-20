package com.github.polybooks.database

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.MainActivity
import com.github.polybooks.core.database.implementation.FBBookDatabase
import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.github.polybooks.utils.url2json
import junit.framework.AssertionFailedError
import org.junit.*
import org.junit.Assert.*

class FBBookDatabaseTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private val firebase = FirebaseFirestore.getInstance()
    private val olBookDB = OLBookDatabase{url2json(it)}
    private val fbBookDB = FBBookDatabase(firebase, olBookDB)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun canGetBookByISBN() {
        val future = fbBookDB.getBook("9782376863069")
        val book = future.get() ?: throw AssertionFailedError("Book was not found")
        assertEquals("Liavek", book.title)
        assertEquals("9782376863069", book.isbn)
        assertEquals("ACTUSF", book.publisher)
        assertNotNull(book.authors)
        assertEquals("paperback", book.format)
        assertNotNull(book.publishDate)
        assertEquals(6, book.publishDate!!.month)
        assertEquals(2020 - 1900, book.publishDate!!.year)
        assertEquals(3, book.publishDate!!.date)
    }

}