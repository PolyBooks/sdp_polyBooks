package com.github.polybooks.utils

import com.github.polybooks.core.Book
import com.github.polybooks.core.Course
import com.github.polybooks.core.Field
import com.github.polybooks.core.Semester

fun anonymousBook(title: String): Book =
        Book("", null, title, null, null, null, null, null)

val mockFields : List<Field> = listOf(
        Field("Biology"),
        Field("Computer Science"),
        Field("Architecture"))

val mockCourses : List<Course> = listOf(
        Course("COM-101"),
        Course("CS-306"),
        Course("CS-323"),
        Course("EE-280"),
        Course("MSE-210"),
        Course("HUM-201"),
        Course("DH-405"),
        Course("ENV-444"),
        Course("MICRO-511")
)

val mockSemesters : List<Semester> = listOf(
        Semester("IN", "BA1"),
        Semester("SV", "BA1"),
        Semester("GC", "MA2"),
        Semester("SC", "BA6"),
        Semester("MT", "BA2"),
        Semester("MX", "BA3"),
        Semester("AR", "MA1"),
        Semester("CD", "BA4"),
        Semester("ENV", "BA5")
)