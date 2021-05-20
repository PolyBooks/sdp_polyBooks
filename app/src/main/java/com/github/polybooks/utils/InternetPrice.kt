package com.github.polybooks.utils

import com.github.polybooks.core.ISBN
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CompletableFuture

val DOLLAR_TO_CHF = 0.9

fun getInternetPrice(isbn : ISBN): CompletableFuture<String> {
    return CompletableFuture.supplyAsync {
        val appid2 : String = "?key=1orhhq0s3nrtl4kf93r3"
        val urlString = "https://booksrun.com/api/price/sell/$isbn$appid2"
        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        //connection.setRequestProperty("X-EBAY-API-IAF-TOKEN", appid)
        connection.requestMethod = "GET"
        connection.connectTimeout = CONNECTION_TIMEOUT
        connection.readTimeout = READ_TIMEOUT
        connection.instanceFollowRedirects = true
        connection.connect()
        val stream : InputStream = connection.inputStream
        val jsonObject = JsonParser.parseReader(InputStreamReader(stream)).asJsonObject //JsonObject
        connection.disconnect()
        print(jsonObject)
        if( jsonObject?.get("result")?.asJsonObject?.get("status")?.asJsonPrimitive?.asString?:"ERROR" != "success"){
            "Unavailable"
        } else {
            val price = jsonObject?.get("result")?.asJsonObject?.get("text")?.asJsonObject?.get("Average")?.asJsonPrimitive?.asString?:"Unavailable"
            price.toFloatOrNull()?.let {
                if (it == 0f){
                    "Unavailable"
                }else "%.2f".format(it * DOLLAR_TO_CHF).toString() +".-"
            }?:price
        }
        /*
        Trying to get ebay to work
        val appid = "JoshuaBe-PolyBook-PRD-e55154608-9d55b3bb"
        val urlString = "https://open.api.ebay.com/shopping?callName=FindProducts" +
                "&appid=$appid" +
                "&ProductID.Value=$isbn" +
                "&ProductID.Type=ISBN" +
                "&responseencoding=JSON&version=1119"
        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        //connection.setRequestProperty("X-EBAY-API-IAF-TOKEN", appid)
        connection.requestMethod = "GET"
        connection.connectTimeout = CONNECTION_TIMEOUT
        connection.readTimeout = READ_TIMEOUT
        connection.instanceFollowRedirects = true
        connection.connect()
        val stream : InputStream = connection.inputStream
        val jsonObject = JsonParser.parseReader(InputStreamReader(stream)).asJsonObject //JsonObject
        connection.disconnect()
        print(jsonObject)

        if (jsonObject["Ack"]?.asJsonPrimitive?.asString?:"Failure" == "Failure" ) {
            "Unavailable"
        } else {
            val epid = jsonObject["Product"]?.asJsonObject?.get("ProductID")?.asJsonObject?.get("Reference")?.asJsonPrimitive?.asString
            epid?:"Unavailable"?.let {
                val urlString = "https://open.api.ebay.com/shopping?callName=GetItemStatus" +
                        "&appid=$appid" +
                        "&ItemID=$it" +
                        "&responseencoding=JSON&version=1119"
                connection.requestMethod = "GET"
                connection.connectTimeout = CONNECTION_TIMEOUT
                connection.readTimeout = READ_TIMEOUT
                connection.instanceFollowRedirects = true
                connection.connect()
                val stream : InputStream = connection.inputStream
                val jsonObject = JsonParser.parseReader(InputStreamReader(stream)).asJsonObject //JsonObject
                connection.disconnect()
                jsonObject.toString()
            }
        }

         */
    }

}