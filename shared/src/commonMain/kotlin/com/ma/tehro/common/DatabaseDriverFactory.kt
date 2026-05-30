//package com.ma.tehro.common
//
//import android.content.Context
//import androidx.sqlite.db.SupportSQLiteDatabase
//import app.cash.sqldelight.async.coroutines.synchronous
//import app.cash.sqldelight.db.QueryResult
//import app.cash.sqldelight.db.SqlDriver
//import app.cash.sqldelight.db.SqlSchema
//import app.cash.sqldelight.driver.android.AndroidSqliteDriver
//import ir.hemond.core.common.DB_FILE_NAME
//
///**
// * Platform-specific implementation of a factory class for creating a SQL driver on Android.
// *
// * This class provides the Android-specific `SqlDriver` needed by SQLDelight to interface
// * with a local SQLite database.
// *
// * @param context The Android [Context], typically the application context, required to
// *                create the database driver.
// */
//
//actual class DatabaseFactory(
//    private val context: Context
//) {
//    actual suspend fun createDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver {
//        return AndroidSqliteDriver(
//            schema.synchronous(), context = context, DB_FILE_NAME,
//            callback = object : AndroidSqliteDriver.Callback(schema.synchronous()) {
//                override fun onOpen(db: SupportSQLiteDatabase) {
//                    db.setForeignKeyConstraintsEnabled(true)
//                }
//            }
//        )
//    }
//
//    actual fun deleteDatabase() {
//        context.deleteDatabase(DB_FILE_NAME)
//    }
//}