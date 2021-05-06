package com.github.polybooks.core.databaseImpl

import com.github.polybooks.core.Field
import com.github.polybooks.core.LoggedUser
import com.github.polybooks.DummyInterestDatabase
import org.junit.Assert.assertEquals
import org.junit.Test

class DummyInterestDatabaseTest {
    private val fields = DummyInterestDatabase.mockFields
    private val courses = DummyInterestDatabase.mockCourses
    private val semesters = DummyInterestDatabase.mockSemesters

    private val db = DummyInterestDatabase()

    @Test
    fun t_listAllFields() {
        val res = db.listAllFields()

        assertEquals(fields, res.get())
    }

    @Test
    fun t_listAllSemesters() {
        val res = db.listAllSemesters()

        assertEquals(semesters, res.get())
    }

    @Test
    fun t_listAllCourses() {
        val res = db.listAllCourses()

        assertEquals(courses, res.get())
    }

    @Test(expected = NotImplementedError::class)
    fun unimplementedThrows_getUserInterests() {
        val u = LoggedUser(1, "unknown")
        db.getUserInterests(u)
    }

    @Test(expected = NotImplementedError::class)
    fun unimplementedThrows_setUserInterests() {
        val u = LoggedUser(1, "unknown")
        db.setUserInterests(u, listOf(Field("Biology")))
    }
}
