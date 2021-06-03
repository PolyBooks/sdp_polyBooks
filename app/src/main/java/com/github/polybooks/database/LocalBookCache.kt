package com.github.polybooks.database

import android.content.Context
import com.github.polybooks.core.Book
import com.github.polybooks.core.ISBN
import java.io.*
import java.util.concurrent.CompletableFuture

class LocalBookCache(context: Context): BookProvider {

    companion object {
        const val BOOKS_CACHE_DIR_NAME = "Books"
    }

    private val booksDir: File = File(context.cacheDir, BOOKS_CACHE_DIR_NAME)

    init {
        if (booksDir.exists()  && !booksDir.isDirectory && !booksDir.delete())
            throw IOException("Could not delete the file blocking the creation of the book cache directory.")
        if (!booksDir.exists() && !booksDir.mkdir())
            throw IOException("Could not create the book cache directory")
    }

    override fun getBooks(
        isbns: Collection<ISBN>,
        ordering: BookOrdering
    ): CompletableFuture<List<Book>> {
        return CompletableFuture.supplyAsync {
            val filesToGet = isbns.map { fileForBook(it) }
            val bookFiles = booksDir.listFiles { file: File -> filesToGet.contains(file) }!!
            bookFiles.map { deserialize(it) }
        }
    }

    override fun addBook(book: Book): CompletableFuture<Unit> {
        return CompletableFuture.supplyAsync {
            val bookFile = fileForBook(book.isbn)
            //I want to override the file if it exists. By precaution I will delete it. It might not be necessary.
            if (bookFile.exists() && !bookFile.delete())
                throw IOException("Could not override cached book")
            if (!bookFile.createNewFile())
                throw IOException("Could not create file for book caching")
            serialize(book, bookFile)
        }
    }

    private fun fileForBook(isbn: ISBN): File {
        return File(booksDir, "$isbn.book")
    }

    private fun deserialize(bookFile: File): Book {
        val inputStream = FileInputStream(bookFile)
        val deserializer = ObjectInputStream(inputStream)
        return deserializer.readObject() as Book
    }

    private fun serialize(book: Book, dest: File) {
        val outputStream = FileOutputStream(dest)
        val serializer = ObjectOutputStream(outputStream)
        serializer.writeObject(book)
    }

}