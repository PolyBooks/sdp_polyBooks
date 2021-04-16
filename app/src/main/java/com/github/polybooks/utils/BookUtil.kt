package com.github.polybooks.utils

import com.github.polybooks.core.Book

fun anonymousBook(title: String): Book =
        Book("", null, title, null, null, null, null, null)