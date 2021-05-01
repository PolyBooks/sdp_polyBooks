package com.github.polybooks.utils

/**
 * A utility object class for all manner of strings manipulation
 */
object StringsManip {
    private const val ISBN13_FORMAT = """[0-9]{13}"""
    private const val ISBN10_FORMAT = """[0-9]{9}[0-9X]"""
    private const val ISBN_FORMAT = """($ISBN10_FORMAT)|($ISBN13_FORMAT)"""

    /**
     * listAuthorsToString takes a nullable list of Strings as parameters
     * and return a single string formatted under the concatenation of all strings of the list
     * separated by ", " except for the last one separated by " and "
     * Empty strings are returned if the list is null or empty
     */
    fun listAuthorsToString(authors: List<String>?): String {
        if (authors == null) {
            return ""
        } else {
            val sb = StringBuilder()
            for (i in authors.indices) {
                sb.append(authors[i])
                if (authors.size != 1) {
                    if (i == authors.size - 2) {
                        sb.append(" and ")
                    } else if (i != authors.size - 1) {
                        sb.append(", ")
                    }
                }
            }
            return sb.toString()
        }
    }

    fun isbnHasCorrectFormat(isbn: String): Boolean {
        return isbn.matches(Regex(ISBN_FORMAT))
    }
}
