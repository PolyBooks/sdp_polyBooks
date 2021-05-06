package com.github.polybooks

import android.os.SystemClock
import com.github.polybooks.core.*
import com.github.polybooks.database.interfaces.BookOrdering
import com.github.polybooks.database.interfaces.BookQuery
import com.github.polybooks.database.interfaces.BookSettings
import com.google.firebase.Timestamp
import java.util.concurrent.CompletableFuture

val default_books: List<Book> = listOf(
        Book("Book1", listOf("Tolkien"), "Lord of the Rings", "?", "?", "?", Timestamp(format.parse("2016-05-05")!!), "?"),
        Book("Book2", listOf("Hugo"), "Les Miserables", "?", "?", "?", Timestamp(format.parse("2016-05-05")!!), "?"),
        Book("Book3", listOf("Baudelaire"), "Les fleurs du mal", "?", "?", "?", Timestamp(format.parse("2016-05-05")!!), "?")
)

class DummyBookQuery(private val books : List<Book> = default_books) : BookQuery {

    override fun onlyIncludeInterests(interests: Collection<Interest>) : BookQuery {
       return DummyBookQuery()
    }

    override fun searchByTitle(title : String) : BookQuery {
       return DummyBookQuery()
    }

    override fun searchByISBN(isbns: Set<String>): BookQuery {
        return DummyBookQuery()
    }


    override fun withOrdering(ordering : BookOrdering) : BookQuery {
        return DummyBookQuery()
    }

    override fun getAll(): CompletableFuture<List<Book>> {
        return CompletableFuture.supplyAsync {
            SystemClock.sleep(2000)
            books
        }
    }

    override fun getN(n: Int, page: Int): CompletableFuture<List<Book>> {
        TODO("Not yet implemented")
    }

    override fun getCount(): CompletableFuture<Int> {
        TODO("Not yet implemented")
    }

    override fun getSettings() : BookSettings {
        return BookSettings(BookOrdering.DEFAULT, null, null, null)
    }

    override fun fromSettings(settings: BookSettings): BookQuery {
        return DummyBookQuery()
    }
}