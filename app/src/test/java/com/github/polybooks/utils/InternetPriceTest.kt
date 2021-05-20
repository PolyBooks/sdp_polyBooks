package com.github.polybooks.utils

import org.junit.Test

import org.junit.Assert.*

class InternetPriceTest {

    @Test
    fun url2jsonDontCrash() {
        val json = getInternetPrice("9780007269709")
            .get()
        assertEquals("Unavailable",json)
    }

    @Test
    fun url3jsonDontCrash() {
        val json = getInternetPrice("9780099549482")
            .get()
        assertNotEquals("Unavailable",json)
    }

    @Test
    fun url4jsonDontCrash() {
        val json = getInternetPrice("9782889152728")
            .get()
        assertEquals("Unavailable",json)
    }


}