package app.ma.scripts.feedback

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun main() {
    val input = ""
    try {
        val json = Json { ignoreUnknownKeys = true }
        val messages = json.parseToJsonElement(input).jsonArray

        for (i in messages.indices) {
            val item = messages[i]
            val message = item.jsonObject["message"]?.jsonPrimitive?.content ?: ""
            if (message.isNotBlank()) {
                println("${i + 1}. $message")
                println("--------")
            }
        }
    } catch (e: Exception) {
        println("error parsing json: ${e.message}")
    }
}