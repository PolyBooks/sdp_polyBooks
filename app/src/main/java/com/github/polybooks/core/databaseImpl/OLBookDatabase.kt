package com.github.polybooks.core.databaseImpl

import android.annotation.SuppressLint
import com.github.polybooks.core.Book
import com.github.polybooks.core.Interest
import com.github.polybooks.core.database.*
import com.github.polybooks.core.database.BookOrdering.*
import com.github.polybooks.utils.url2json
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture


private const val TITLE_FIELD_NAME = "full_title"
private const val AUTHORS_FIELD_NAME = "authors"
private const val FORMAT_FIELD_NAME = "physical_format"
private const val ISBN13_FIELD_NAME = "isbn_13"
private const val PUBLISHER_FIELD_NAME = "publishers"
private const val PUBLISH_DATE_FIELD_NAME = "publish_date"

private const val DATE_FORMAT = "MMM dd, yyyy"
private const val ISBN13_FORMAT = """(\d|-){12}\d"""
private const val ISBN10_FORMAT = """(\d|-){9}\d"""
private const val ISBN_FORMAT = "($ISBN10_FORMAT)|($ISBN13_FORMAT)"

private const val OL_BASE_ADDR = """https://openlibrary.org"""

/**
 * An implementation of a book database based on the Open Library online database
 * */
@SuppressLint("NewApi")
class OLBookDatabase : BookDatabase {

    override fun queryBooks(): BookQuery = OLBookQuery()

    private inner class OLBookQuery : BookQuery {

        private var ordering = DEFAULT

        private var empty : Boolean = false
        private var title : String? = null
        private var isbn : String? = null

        override fun onlyIncludeInterests(interests: Collection<Interest>): BookQuery {
            System.err.println("Warning: onlyIncludeInterest not fully implemented for OLBookQuery")
            this.empty = true
            return this
        }

        override fun searchByTitle(title: String): BookQuery {
            this.empty = false
            this.title = title
            this.isbn = null
            return this
        }

        override fun searchByISBN13(isbn13: String): BookQuery {
            this.empty = false
            this.title = null
            this.isbn = isbn13
            return this
        }

        override fun withOrdering(ordering: BookOrdering): BookQuery {
            this.ordering = ordering
            return this
        }

        override fun getAll(): CompletableFuture<List<Book>> {
            if (empty) return CompletableFuture.completedFuture(Collections.emptyList())
            else if (title != null) {
                return TODO("Search by title")
            } else { assert(isbn != null)
                isbn = isbn!!.trim()
                if (isbn!!.matches(Regex(ISBN_FORMAT))) {
                    val bookURL = "$OL_BASE_ADDR/isbn/$isbn.json"
                    val bookJsonFuture = url2json(bookURL)
                    return bookJsonFuture.handleAsync { bookJson, exception ->
                        //handle a FileNotFound
                        if (exception is FileNotFoundException) return@handleAsync Collections.emptyList<Book>()
                        //do not handle any other exception
                        if (exception != null) throw exception
                        //If things go smoothly return the book
                        return@handleAsync listOf(parseBook(bookJson))
                    }
                } else {
                    val future = CompletableFuture<List<Book>>()
                    future.completeExceptionally(Exception("ISBN is not valid"))
                    return future
                }
            }
        }

        override fun getN(n: Int, page: Int): CompletableFuture<List<Book>> {
            TODO("Not yet implemented")
        }

        override fun getCount(): CompletableFuture<Int> {
            TODO("Not yet implemented")
        }

    }

}

private const val errorMessage = "Cannot parse OpenLibrary book because : "

/**
 * Function for internal use in OLBookDatabase. Takes the json of a book, and makes a Book from it.
 * */
@SuppressLint("NewApi")
fun parseBook(jsonBook : JsonElement) : Book {
    val jsonBookObject = asJsonObject(jsonBook);
    val title = getJsonField(jsonBookObject, TITLE_FIELD_NAME)
                .map{parseTitle(it)}
                .orElseThrow(cantParseException(TITLE_FIELD_NAME))!!
    val isbn13 = getJsonField(jsonBookObject, ISBN13_FIELD_NAME)
                .map{parseISBN13(it)}
                .orElseThrow(cantParseException(ISBN13_FIELD_NAME))!!
    val authors = getJsonField(jsonBookObject, AUTHORS_FIELD_NAME)
                .map{parseAuthors(it)}
                .orElse(null)
    val format = getJsonField(jsonBookObject, FORMAT_FIELD_NAME)
                .map{parseFormat(it)}
                .orElse(null)
    val publisher = getJsonField(jsonBookObject, PUBLISHER_FIELD_NAME)
                .map{parsePublisher(it)}
                .orElse(null)
    val publishDate = getJsonField(jsonBookObject, PUBLISH_DATE_FIELD_NAME)
                .map{parsePublishDate(it)}
                .orElse(null)

    return Book(isbn13, authors, title, null, null,
                publisher, publishDate, format)

}

private fun parseTitle(jsonTitle : JsonElement) : String = asString(jsonTitle)

private fun parseISBN13(jsonISBN13 : JsonElement) : String {
    val first : JsonElement? = asJsonArray(jsonISBN13).firstOrNull()
    if (first == null) throw cantParseException("$ISBN13_FIELD_NAME[0]")()
    else return asString(first)
}

@SuppressLint("NewApi")
private fun parseAuthors(jsonAuthors : JsonElement) : List<String> {
    return asJsonArray(jsonAuthors)
        .iterator().asSequence()
        .map {
            val authorOption = getJsonField(asJsonObject(it), "key")
            val authorJson = authorOption.orElseThrow(cantParseException("$AUTHORS_FIELD_NAME[n].key"))
            asString(authorJson)
        } //TODO to get the actual names of the authors: need to execute more requests
        .toList()
}

private fun parseFormat(jsonFormat : JsonElement) : String = asString(jsonFormat)

private fun parsePublisher(jsonPublisher : JsonElement) : String {
    val first : JsonElement? = asJsonArray(jsonPublisher).firstOrNull()
    return if (first == null) ""
    else asString(first)
}

@SuppressLint("SimpleDateFormat")
private fun parsePublishDate(jsonPublishDate : JsonElement) : Date {
    val dateString = asString(jsonPublishDate)
    val dateFormat = SimpleDateFormat(DATE_FORMAT)
    dateFormat.isLenient = false
    return dateFormat.parse(dateString)
}

private fun asJsonObject(jsonElement : JsonElement) : JsonObject {
    if (!jsonElement.isJsonObject) {
        throw Exception(errorMessage + "Json is not a JsonObject")
    }
    return jsonElement.asJsonObject!!
}

private fun asJsonArray(jsonElement: JsonElement) : JsonArray {
    if (!jsonElement.isJsonArray) {
        throw Exception(errorMessage + "Json is not a JsonArray")
    }
    return jsonElement.asJsonArray!!
}

private fun asString(jsonElement: JsonElement) : String {
    if (!jsonElement.isJsonPrimitive) {
        throw Exception(errorMessage + "Json is not a JsonPrimitive")
    }
    val primitive = jsonElement.asJsonPrimitive!!
    if (!primitive.isString) {
        throw Exception(errorMessage + "Json is not a String")
    }
    return primitive.asString!!
}

//try to access a field of a json object and return an optional instead of a nullable
@SuppressLint("NewApi")
private fun getJsonField(jsonObject: JsonObject, fieldName : String) : Optional<JsonElement> {
    return Optional.ofNullable(jsonObject.get(fieldName))
}

private fun cantParseException(fieldName : String) : () -> Exception {
    return { Exception("$errorMessage: Json has no field $fieldName.") }
}