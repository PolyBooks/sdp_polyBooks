package com.github.polybooks.utils

import com.github.polybooks.utils.url2json
import org.junit.Test

import org.junit.Assert.*

class HTTP2JSONTests {

    @Test
    fun url2jsonDontCrash() {
        val json = url2json("https://openlibrary.org/books/OL29583638M.json")
            .handle { json, exception ->
                assertNotNull(json)
            }

    }

}