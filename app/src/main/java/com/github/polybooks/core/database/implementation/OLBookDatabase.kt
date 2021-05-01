package com.github.polybooks.core.database.implementation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.polybooks.core.Book
import com.github.polybooks.core.database.DatabaseException
import com.github.polybooks.core.database.interfaces.BookDatabase
import com.github.polybooks.core.database.interfaces.BookQuery
import com.github.polybooks.utils.listOfFuture2FutureOfList
import com.github.polybooks.utils.unwrapException
import com.google.firebase.Timestamp
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.FileNotFoundException
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture


// TODO add to/create listOf as we discover new fields
private val TITLE_FIELD_NAMES = listOf("title", "full_title")
private const val AUTHORS_FIELD_NAME = "authors"
private const val FORMAT_FIELD_NAME = "physical_format"
private val ISBN_FIELD_NAMES = listOf("isbn_13", "isbn_10")
private const val PUBLISHER_FIELD_NAME = "publishers"
private const val PUBLISH_DATE_FIELD_NAME = "publish_date"
private const val AUTHOR_NAME_FIELD_NAME = "name"

private const val DATE_FORMAT = "MMM dd, yyyy"
private const val DATE_FORMAT2 = "yyyy"

private const val OL_BASE_ADDR = """https://openlibrary.org"""

/**
 * An implementation of a book database based on the Open Library online database
 * */
class OLBookDatabase(private val url2json: (String) -> CompletableFuture<JsonElement>):
    BookDatabase {

    override fun queryBooks(): BookQuery = OLBookQuery()

    private inner class OLBookQuery: AbstractBookQuery() {

        @RequiresApi(Build.VERSION_CODES.N)
        override fun getAll(): CompletableFuture<List<Book>> {
            return if (isbns == null) CompletableFuture.completedFuture(Collections.emptyList())
            else {
                val futures = isbns!!.map { getBookByISBN(it) }
                listOfFuture2FutureOfList(futures).thenApply { it.filterNotNull() }
            }
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun getN(n: Int, page: Int): CompletableFuture<List<Book>> {
            if (n <= 0 || page < 0) {
                throw IllegalArgumentException(
                    if (n <= 0) "Cannot return a negative/null ($n) number of results"
                    else "Cannot return a negative ($page) page number"
                )
            }
            return getAll().thenApply { list ->
                val lowRange = min(n * page, list.size)
                val highRange = min(n * page + n, list.size)
                list.subList(lowRange, highRange)
            }
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun getCount(): CompletableFuture<Int> {
            return getAll().thenApply { it.size }
        }

    }

    //makes an URL to the OpenLibrary page out of an isbn
    private fun isbn2URL(isbn: String): String {
        return "$OL_BASE_ADDR/isbn/$isbn.json"
    }

    private val errorMessage = "Cannot parse OpenLibrary book because : "

    private fun getBookByISBN(isbn: String): CompletableFuture<Book?> {
        val url = isbn2URL(isbn)
        return url2json(url)
            .thenApply { parseBook(it) }
            .thenCompose { updateBookWithAuthorName(it) }
            .exceptionally { exception ->
                val unwraped = unwrapException(exception)
                if (unwraped is FileNotFoundException) {
                    return@exceptionally null
                } else throw unwraped
            }
    }


    //takes a book that has the authors in the form /authors/<authorID>
    //and fetches the actual name of the author
    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateBookWithAuthorName(book: Book): CompletableFuture<Book> {
        if (book.authors == null) return CompletableFuture.completedFuture(book)
        //This is a list of futures that are concurrently fetching the name of the authors
        val newAuthorsFutures = book.authors.map { authorID ->
            val authorsUrl = "$OL_BASE_ADDR$authorID.json"
            url2json(authorsUrl).thenApply { parseAuthor(it) }
        }

        //Combine those futures into one future of a list of names
        val combined = listOfFuture2FutureOfList(newAuthorsFutures)
        //update the book with the names of the authors
        return combined.thenApply { newAuthors -> book.copy(authors = newAuthors) }
    }

    /**
     * Function for internal use in OLBookDatabase. Takes the json of a book, and makes a Book from it.
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun parseBook(jsonBook: JsonElement): Book {
        val jsonBookObject = asJsonObject(jsonBook)
        val title = getJsonFields(jsonBookObject, TITLE_FIELD_NAMES)
            .map { parseTitle(it) }
            .orElseThrow(cantParseException(TITLE_FIELD_NAMES[0]))!!
        val isbn13 = getJsonFields(jsonBookObject, ISBN_FIELD_NAMES)
            .map { parseISBN13(it) }
            .orElseThrow(cantParseException(ISBN_FIELD_NAMES[0]))!!
        val authors = getJsonField(jsonBookObject, AUTHORS_FIELD_NAME)
            .map { parseAuthors(it) }
            .orElse(null)
        val format = getJsonField(jsonBookObject, FORMAT_FIELD_NAME)
            .map { parseFormat(it) }
            .orElse(null)
        val publisher = getJsonField(jsonBookObject, PUBLISHER_FIELD_NAME)
            .map { parsePublisher(it) }
            .orElse(null)
        val publishDate = getJsonField(jsonBookObject, PUBLISH_DATE_FIELD_NAME)
            .map { parsePublishDate(it) }
            .orElse(null)
        // TODO languages and edition!!!

        return Book(
            isbn13, authors, title, null, null,
            publisher, publishDate, format
        )

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun parseAuthor(jsonAuthor: JsonElement): String {
        val nameField = getJsonField(asJsonObject(jsonAuthor), AUTHOR_NAME_FIELD_NAME)
        return nameField.map { asString(it) }
            .orElseThrow(cantParseException(AUTHOR_NAME_FIELD_NAME))
    }

    private fun parseTitle(jsonTitle: JsonElement): String = asString(jsonTitle)

    private fun parseISBN13(jsonISBN13: JsonElement): String {
        val first: JsonElement? = asJsonArray(jsonISBN13).firstOrNull()
        if (first == null) {
            throw cantParseException(ISBN_FIELD_NAMES[0])()
        } else return asString(first)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun parseAuthors(jsonAuthors: JsonElement): List<String> {
        return asJsonArray(jsonAuthors)
            .iterator().asSequence()
            .map {
                val authorOption = getJsonField(asJsonObject(it), "key")
                val authorJson =
                    authorOption.orElseThrow(cantParseException("$AUTHORS_FIELD_NAME[n].key"))
                asString(authorJson)
            }
            .toList()
    }

    private fun parseFormat(jsonFormat: JsonElement): String = asString(jsonFormat)

    private fun parsePublisher(jsonPublisher: JsonElement): String {
        val first: JsonElement? = asJsonArray(jsonPublisher).firstOrNull()
        return if (first == null) ""
        else asString(first)
    }

    @SuppressLint("SimpleDateFormat")
    private fun parsePublishDate(jsonPublishDate: JsonElement): Timestamp {
        val dateString = asString(jsonPublishDate)
        val dateFormat1 = SimpleDateFormat(DATE_FORMAT)
        val dateFormat2 = SimpleDateFormat(DATE_FORMAT2)
        dateFormat1.isLenient = false
        dateFormat2.isLenient = false
        return try {
            Timestamp(dateFormat1.parse(dateString)!!)
        } catch (e: java.text.ParseException) {
            Timestamp(dateFormat2.parse(dateString)!!)
        }
    }

    private fun asJsonObject(jsonElement: JsonElement): JsonObject {
        if (!jsonElement.isJsonObject) {
            throw DatabaseException(errorMessage + "Json is not a JsonObject")
        }
        return jsonElement.asJsonObject!!
    }

    private fun asJsonArray(jsonElement: JsonElement): JsonArray {
        if (!jsonElement.isJsonArray) {
            throw DatabaseException(errorMessage + "Json is not a JsonArray")
        }
        return jsonElement.asJsonArray!!
    }

    private fun asString(jsonElement: JsonElement): String {
        if (!jsonElement.isJsonPrimitive) {
            throw DatabaseException(errorMessage + "Json is not a JsonPrimitive")
        }
        val primitive = jsonElement.asJsonPrimitive!!
        if (!primitive.isString) {
            throw DatabaseException(errorMessage + "Json is not a String")
        }
        return primitive.asString!!
    }

    //try to access a field of a json object and return an optional instead of a nullable
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getJsonField(jsonObject: JsonObject, fieldName: String): Optional<JsonElement> {
        return Optional.ofNullable(jsonObject.get(fieldName))
    }

    //try to access a field of a json object and return an optional instead of a nullable
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getJsonFields(
        jsonObject: JsonObject,
        fieldNames: List<String>
    ): Optional<JsonElement> {
        for (field in fieldNames) {
            if (jsonObject.get(field) != null) {
                return Optional.ofNullable(jsonObject.get(field))
            }
        }
        return Optional.empty()
    }

    private fun cantParseException(fieldName: String): () -> Exception {
        return { DatabaseException("$errorMessage: Json has no field $fieldName.") }
    }
}