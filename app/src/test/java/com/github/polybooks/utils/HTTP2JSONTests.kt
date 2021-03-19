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

    @Test
    fun url2jsonDontCrash2() {
        val json = url2json("this isn't even an url")
                .handle { json, exception ->
                    assertNull(json)
                    assertNotNull(exception)
                }
    }

    @Test
    fun url2jsonDontCrash3() {
        val json = url2json("https://fsf.org") //have you heard the good news?
                .handle { json, exception ->
                    assertNull(json)
                    assertNotNull(exception) // should throw an exception because it's not json
                }
    }

}