package com.github.polybooks.utils

import java.lang.StringBuilder

/**
 * A utility object class for all manner of strings manipulation
 */
object StringsManip {
    /**
     * listAuthorsToString takes a nullable list of Strings as parameters
     * and return a single string formatted under the concatenation of all strings of the list
     * separated by ", " except for the last one separated by " and "
     * Empty strings are returned if the list is null or empty
     */
    fun listAuthorsToString(authors: List<String>?): String {
        if(authors == null) {
            return ""
        } else {
            val sb = StringBuilder()
            for(i in authors.indices) {
                sb.append(authors[i])
                if (authors.size != 1) {
                    if (i == authors.size - 2) {
                        sb.append(" and ")
                    } else if(i != authors.size - 1) {
                        sb.append(", ")
                    }
                }
            }
            return sb.toString()
        }
    }
}
