package com.github.polybooks

import com.github.polybooks.utils.StringsManip.listAuthorsToString
import org.junit.Assert.assertEquals
import org.junit.Test

class FillSaleActivityUnitTests {
    @Test
    fun listAuthorsNullToString() {
        val expected = ""
        val input = null
        assertEquals(expected, listAuthorsToString(input))
    }

    @Test
    fun listAuthorsEmptyToString() {
        val expected = ""
        val input = arrayListOf<String>()
        assertEquals(expected, listAuthorsToString(input))
    }

    @Test
    fun listAuthors1ToString() {
        val expected = "Fowler"
        val input = listOf("Fowler")
        assertEquals(expected, listAuthorsToString(input))
    }

    @Test
    fun listAuthors2ToString() {
        val expected = "Fowler and Beck"
        val input = listOf("Fowler", "Beck")
        assertEquals(expected, listAuthorsToString(input))
    }

    @Test
    fun listAuthors3ToString() {
        val expected = "Fowler, Beck and Evans"
        val input = listOf("Fowler", "Beck", "Evans")
        assertEquals(expected, listAuthorsToString(input))
    }

}