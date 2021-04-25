package com.github.polybooks.utils

import com.github.polybooks.core.Book
import com.github.polybooks.core.Course
import com.github.polybooks.core.Field
import com.github.polybooks.core.Semester


private const val ISBN13_FORMAT = """[0-9]{13}"""
private const val ISBN10_FORMAT = """[0-9]{9}[0-9X]"""
private const val ISBN_FORMAT = """($ISBN10_FORMAT)|($ISBN13_FORMAT)"""

fun anonymousBook(title: String): Book =
        Book("", null, title, null, null, null, null, null)

//takes a string and try to interpret it as an isbn
fun regulariseISBN(userISBN : String) : String? {
        val regularised = userISBN.replace("[- ]".toRegex(), "")
        return if (!regularised.matches(Regex(ISBN_FORMAT))) null
        else regularised
}