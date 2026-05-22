package app.ma.scripts.place

import app.ma.scripts.common.RES_PATH
import kotlinx.serialization.json.*
import java.io.File
import java.sql.DriverManager

fun main() {
    val dbFile = File(RES_PATH + "data.sqlite")
    val outputFile = File(RES_PATH + "places.json")

    val conn = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
    val stmt = conn.createStatement()

    val json = Json { prettyPrint = true }
    val tableName = "poi"

    val placeArray = buildJsonArray {
        val rs = stmt.executeQuery("SELECT * FROM '$tableName'")
        val meta = rs.metaData
        val columnCount = meta.columnCount

        while (rs.next()) {
            add(buildJsonObject {
                for (i in 1..columnCount) {
                    val columnName = meta.getColumnName(i)
                    val value = rs.getObject(i)
                    put(
                        key = columnName,
                        element = when (value) {
                            null -> JsonNull
                            is Number -> JsonPrimitive(value)
                            is Boolean -> JsonPrimitive(value)
                            is String -> JsonPrimitive(value)
                            else -> JsonPrimitive(value.toString())
                        }
                    )
                }
            })
        }
        rs.close()
    }
    stmt.close()
    conn.close()

    outputFile.writeText(json.encodeToString(JsonArray.serializer(), placeArray))
    println("json created successfully: ${outputFile.absolutePath}")
}

