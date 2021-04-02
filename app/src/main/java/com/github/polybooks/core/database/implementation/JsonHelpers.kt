package com.github.polybooks.core.database.implementation

import android.annotation.SuppressLint
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*

private const val errorMessage = "Cannot parse book because : "

internal fun asJsonObject(jsonElement : JsonElement) : JsonObject {
    if (!jsonElement.isJsonObject) {
        throw Exception(errorMessage + "Json is not a JsonObject")
    }
    return jsonElement.asJsonObject!!
}

internal fun asJsonArray(jsonElement: JsonElement) : JsonArray {
    if (!jsonElement.isJsonArray) {
        throw Exception(errorMessage + "Json is not a JsonArray")
    }
    return jsonElement.asJsonArray!!
}

internal fun asString(jsonElement: JsonElement) : String {
    if (!jsonElement.isJsonPrimitive) {
        throw Exception(errorMessage + "Json is not a JsonPrimitive")
    }
    val primitive = jsonElement.asJsonPrimitive!!
    if (!primitive.isString) {
        throw Exception(errorMessage + "Json is not a String")
    }
    return primitive.asString!!
}

internal fun asInt(jsonElement: JsonElement) : Int {
    if (!jsonElement.isJsonPrimitive) {
        throw Exception(errorMessage + "Json is not a JsonPrimitive")
    }
    val primitive = jsonElement.asJsonPrimitive!!
    if (!primitive.isNumber) {
        throw Exception(errorMessage + "Json is not a Number")
    }
    return primitive.asInt!!
}

//try to access a field of a json object and return an optional instead of a nullable
@SuppressLint("NewApi")
internal fun getJsonField(jsonObject: JsonObject, fieldName : String) : Optional<JsonElement> {
    return Optional.ofNullable(jsonObject.get(fieldName))
}

internal fun cantParseException(fieldName : String) : () -> Exception {
    return { Exception("$errorMessage: Json has no field $fieldName.") }
}