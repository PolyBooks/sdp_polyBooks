package com.github.polybooks.database

import com.github.polybooks.core.Book
import com.github.polybooks.utils.anonymousBook
import com.github.polybooks.utils.order
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

    @Test
    fun bookOrderingWorks() {
        val books = TestBookProvider.books.values.toList()
        val dec = order(books, BookOrdering.TITLE_DEC)
        val inc = order(books, BookOrdering.TITLE_INC)
        assertEquals(dec.reversed(), inc)
        assertEquals(books.sortedBy { it.title }, inc)
    }


}