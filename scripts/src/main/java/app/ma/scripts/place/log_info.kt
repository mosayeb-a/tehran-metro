package app.ma.scripts.place

import app.ma.scripts.common.RES_PATH
import kotlinx.serialization.json.*
import java.io.File

fun main() {
    val jsonFile = File(RES_PATH + "places.json")
    val json = Json { ignoreUnknownKeys = true }
    val placeArray = json.decodeFromString<JsonArray>(jsonFile.readText())

    val types = placeArray.mapNotNull { it.jsonObject["type"]?.jsonPrimitive?.content }.toSet()
    val categories =
        placeArray.mapNotNull { it.jsonObject["category"]?.jsonPrimitive?.content }.toSet()

    println("distinct types (${types.size}):")
    types.forEachIndexed { index, type ->
        println("  ${index + 1}. $type")
    }
    println("------------")
    println("distinct categories (${categories.size}):")
    categories.forEachIndexed { index, category ->
        println("  ${index + 1}. $category")
    }
}