package com.github.polybooks.utils

import android.content.Context

interface FieldWithName {
    fun fieldName(c: Context? = null): String
}