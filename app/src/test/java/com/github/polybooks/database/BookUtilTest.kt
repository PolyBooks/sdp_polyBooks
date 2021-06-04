package com.github.polybooks.database

import com.github.polybooks.core.Book
import com.github.polybooks.core.ISBN
import com.github.polybooks.utils.anonymousBook
import com.github.polybooks.utils.order
import org.junit.Test
import java.util.*
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
        val books = listOf<Book>(Book(
                "9780007269709",
                listOf("J.R.R. Tolkien"),
                "The Fellowship of the Ring",
                null,
                null,
                "Harpercollins",
                Date(108, 3, 1),
                "paperback"
            ),Book(
                "9782376863069",
                listOf("Steven Brust", "Megan Lindholm"),
                "Liavek",
                null,
                null,
                "ACTUSF",
                Date(120, 6, 3),
                "paperback"
            ),Book(
                "9780156881807",
                listOf("Molière"),
                "Tartuffe, by Moliere",
                null,
                "English",
                "Harvest Books",
                Date(68, 0, 10),
                "paperback"
            ),Book(
                "9781985086593",
                listOf("Remzi H Arpaci-Dusseau", "Andrea C Arpaci-Dusseau"),
                "Operating Systems",
                null,
                null,
                "CreateSpace Independent Publishing Platform",
                Date(118, 8, 1),
                "paperback"
            )
        )
        val dec = order(books, BookOrdering.TITLE_DEC)
        val inc = order(books, BookOrdering.TITLE_INC)
        assertEquals(dec.reversed(), inc)
        assertEquals(books.sortedBy { it.title }, inc)
    }


}