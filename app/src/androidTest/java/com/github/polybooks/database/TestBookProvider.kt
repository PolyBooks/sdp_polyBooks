package com.github.polybooks.database

import com.github.polybooks.core.Book
import com.github.polybooks.core.ISBN
import java.util.*
import java.util.concurrent.CompletableFuture

object TestBookProvider : BookProvider {

    val books = mutableMapOf<ISBN, Book>(
        "9780007269709" to Book(
            "9780007269709",
            listOf("J.R.R. Tolkien"),
            "The Fellowship of the Ring",
            null,
            null,
            "Harpercollins",
            Date(108, 3, 1),
            "paperback"
        ),
        "9782376863069" to Book(
            "9782376863069",
            listOf("Steven Brust", "Megan Lindholm"),
            "Liavek",
            null,
            null,
            "ACTUSF",
            Date(120, 6, 3),
            "paperback"
        ),
        "9780156881807" to Book(
            "9780156881807",
            listOf("Molière"),
            "Tartuffe, by Moliere",
            null,
            "English",
            "Harvest Books",
            Date(68, 0, 10),
            "paperback"
        ),
        "9781985086593" to Book(
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

    override fun getBooks(
        isbns: Collection<ISBN>,
        ordering: BookOrdering
    ): CompletableFuture<List<Book>> {
        val b = isbns.mapNotNull { books[it] }
        return CompletableFuture.completedFuture(b)
    }

    override fun addBook(book: Book): CompletableFuture<Unit> {
        books[book.isbn] = book
        return CompletableFuture.completedFuture(Unit)
    }
}