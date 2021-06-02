/**
 * This package contains code for the core (as opposed to the UI) of the app
 * */
package com.github.polybooks.core


import java.io.Serializable
import java.util.*

typealias ISBN = String

/**
 * The Book class contains all the information about a book. A Book should not be confused with
 * a Sale which is a concrete instance of a Book being sold by a user.
 * @property isbn The ISBN 13 of the book, it's unique identifier.
 * @property authors A list of the author(s) of the book.
 * @property title The title of the book.
 * @property edition The edition of the book.
 * @property language The language in which the book is written.
 * @property publisher The publisher of the book.
 * @property publishDate The publish date (UNIX timestamp) of the book
 * @property format The format of the book (Hard cover, pocket book, magazine, ...)
 * */
data class Book(
    val isbn : ISBN,
    val authors : List<String>?,
    val title : String,
    val edition : String?,
    val language : String?,
    val publisher : String?,
    val publishDate : Date?,
    val format : String?,
    val totalStars: Double? = null,
    val numberVotes: Int? = null
) : Serializable

/**
 * Allows access to the name of a field
 */
enum class BookFields(val fieldName: String) {
    ISBN("isbn"),
    AUTHORS("authors"),
    TITLE("title"),
    EDITION("edition"),
    LANGUAGE("language"),
    PUBLISHER("publisher"),
    PUBLISHDATE("publishDate"),
    FORMAT("format"),
    PICTUREFILENAME("pictureFileName"),
    RATING("rating")
}
