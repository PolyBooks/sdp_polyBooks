package com.github.polybooks.utils

import com.github.polybooks.core.Semester
import org.junit.Test
import org.junit.Assert.*
import com.github.polybooks.utils.StringsManip.listAuthorsToString
import com.github.polybooks.utils.StringsManip.mergeSectionAndSemester

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
        val expected = "Fowler, Beck, and Evans"
        val input = listOf("Fowler", "Beck", "Evans")
        assertEquals(expected, listAuthorsToString(input))
    }

    @Test
    fun listAuthors4ToString() {
        val expected = "Fowler, Beck, Evans, and Denis Brogniart"
        val input = listOf("Fowler", "Beck", "Evans", "Denis Brogniart")
        assertEquals(expected, listAuthorsToString(input))
    }

    @Test
    fun mergeSectionAndSemesterTest() {
        val expected = "IN-BA6"
        val input = Semester("IN", "BA6")
        assertEquals(expected, mergeSectionAndSemester(input))
    }

}