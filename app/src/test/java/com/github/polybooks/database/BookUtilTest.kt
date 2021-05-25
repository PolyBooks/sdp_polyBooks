package com.github.polybooks.database

import com.github.polybooks.core.Book
import com.github.polybooks.utils.anonymousBook
import org.junit.Test
import kotlin.test.assertEquals

class BookUtilTest {
    @Test
    fun anonymousBookTest(){
        assertEquals(Book("", null, "Tests for Dummies", null, null, null, null, null),
            anonymousBook("Tests for Dummies"))
    }


}