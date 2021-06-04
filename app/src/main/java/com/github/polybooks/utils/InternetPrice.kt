package com.github.polybooks.utils

import com.github.polybooks.core.ISBN
import com.google.gson.JsonParser
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CompletableFuture

val DOLLAR_TO_CHF: Double = 0.9
val INTERNET_PRICE_UNAVAILABLE: String = "__UNAVAILABLE__"

fun getInternetPrice(isbn : ISBN): CompletableFuture<String> {
    return CompletableFuture.supplyAsync {
        val appId = "?key=1orhhq0s3nrtl4kf93r3"
        val urlString = "https://booksrun.com/api/price/sell/$isbn$appId"
        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = CONNECTION_TIMEOUT
        connection.readTimeout = READ_TIMEOUT
        connection.instanceFollowRedirects = true
        connection.connect()

        val stream: InputStream = connection.inputStream
        val jsonObject = JsonParser.parseReader(InputStreamReader(stream)).asJsonObject //JsonObject
        connection.disconnect()

        if (jsonObject?.get("result")?.asJsonObject?.get("status")?.asJsonPrimitive?.asString?:"ERROR" != "success") {
            INTERNET_PRICE_UNAVAILABLE
        } else {
            val price = jsonObject?.get("result")?.asJsonObject?.get("text")?.asJsonObject?.get("Average")?.asJsonPrimitive?.asString?:"Unavailable"
            price.toFloatOrNull()?.let {
                if (it == 0f) INTERNET_PRICE_UNAVAILABLE else "%.2f".format(it * DOLLAR_TO_CHF)
            } ?: price
        }
    }

}