package com.github.polybooks.database

import com.google.gson.JsonParser
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture

private val urlRegex = """.*openlibrary.org(.*)""".toRegex()
private val url2filename = mapOf(
    "/authors/OL7511250A.json" to "OL7511250A.json",
    "/authors/OL7482089A.json" to "OL7482089A.json",
    "/authors/OL8315711A.json" to  "OL8315711A.json",
    "/authors/OL8315712A.json" to  "OL8315712A.json",
    "/authors/OL6899222A.json" to "OL6899222A.json",
    "/isbn/9782376863069.json" to "9782376863069.json",
    "/isbn/2376863066.json" to "9782376863069.json",
    "/isbn/9781985086593.json" to "9781985086593.json",
    "/isbn/9780156881807.json" to "9780156881807.json"
)
const val baseDir = "src/test/java/com/github/polybooks/core/databaseImpl"

val url2json = { url : String ->
    CompletableFuture.supplyAsync {
        val regexMatch = urlRegex.matchEntire(url) ?: throw FileNotFoundException("File Not Found : $url")
        val address = regexMatch.groups[1]?.value ?: throw Error("The regex is wrong")
        val filename = url2filename[address] ?: throw FileNotFoundException("File Not Found : $url")
        val file = File("$baseDir/$filename")
        val stream = file.inputStream()
        JsonParser.parseReader(InputStreamReader(stream))
    }
}