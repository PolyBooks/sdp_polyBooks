package com.github.polybooks.database

import com.github.polybooks.core.Book
import com.github.polybooks.utils.anonymousBook
import org.junit.Test
import kotlin.test.assertEquals

class UtilTest {
    @Test
    fun anonymousBookTest(){
        assertEquals(Book("", null, "Tests for Dummies", null, null, null, null, null),
            anonymousBook("Tests for Dummies"))
    }

    @Test
    fun dataBaseExceptionTest(){
        assert(DatabaseException("Hello").message == DatabaseException("Hello").message)
    }


}