package com.github.polybooks.utils

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CompletableFuture

const val CONNECTION_TIMEOUT = 5000
const val READ_TIMEOUT = 10000

// TODO this would be the release version and we'd have a separate file also containing the fun url2json() with the mock in debug
/**
 * Given an URL fetches the json at that address and
 * parses it as JSON
 * */
fun url2json(urlString : String) : CompletableFuture<JsonElement> {
    return CompletableFuture.supplyAsync {
        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = CONNECTION_TIMEOUT
        connection.readTimeout = READ_TIMEOUT
        connection.instanceFollowRedirects = true
        val stream : InputStream = connection.inputStream
        val jsonObject = JsonParser.parseReader(InputStreamReader(stream))
        connection.disconnect()
        jsonObject
    }
}