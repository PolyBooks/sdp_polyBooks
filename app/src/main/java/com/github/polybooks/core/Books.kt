/**
 * This package contains code for the core (as opposed to the UI) of the app
 * */
package com.github.polybooks.core

/**
 * The Book class contains all the information about a book. A Book should not be confused with
 * a Sale which is a concrete instance of a Book being sold by a user.
 * @property isbn13 The ISBN 13 of the book, it's unique identifier.
 * @property authors A list of the author(s) of the book.
 * @property title The title of the book.
 * */
data class Book(val isbn13 : Long, val authors : List<String>, val title : String) //TODO add all the usefull information
