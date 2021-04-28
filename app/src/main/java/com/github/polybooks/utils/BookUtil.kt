package com.github.polybooks.utils

import com.github.polybooks.core.Book
import com.github.polybooks.core.Course
import com.github.polybooks.core.Field
import com.github.polybooks.core.Semester

fun anonymousBook(title: String): Book =
        Book("", null, title, null, null, null, null, null)