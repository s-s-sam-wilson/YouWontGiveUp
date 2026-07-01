package com.sam.youwontgiveup.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor
import com.sam.youwontgiveup.ui.dashboard.UrlItem
import com.sam.youwontgiveup.ui.notifications.HistoryItem

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    ) {

    companion object {
        private const val DATABASE_NAME = "focus_warden.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_QUOTES = "quotes"
        const val TABLE_URLS = "blocked_urls"
        const val TABLE_HISTORY = "history"
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE $TABLE_QUOTES(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                text TEXT NOT NULL,
                author TEXT,
                category TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_URLS(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                url TEXT NOT NULL,
                enabled INTEGER DEFAULT 1
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_HISTORY(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                domain TEXT NOT NULL,
                visited_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        insertDefaultQuotes(db)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUOTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_URLS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        onCreate(db)
    }

    private fun insertDefaultQuotes(db: SQLiteDatabase) {

        insertQuote(
            db,
            "Hard work is worthless for those that don't believe in themselves.",
            "Naruto Uzumaki",
            "Anime"
        )

        insertQuote(
            db,
            "The pain of discipline weighs ounces. The pain of regret weighs tons.",
            "Jim Rohn",
            "Discipline"
        )

        insertQuote(
            db,
            "You suffer more in imagination than in reality.",
            "Seneca",
            "Stoicism"
        )

        insertQuote(
            db,
            "No one is coming to save you.",
            "Unknown",
            "Discipline"
        )

        insertQuote(
            db,
            "Stay hard.",
            "David Goggins",
            "Motivation"
        )
    }

    private fun insertQuote(
        db: SQLiteDatabase,
        text: String,
        author: String,
        category: String
    ) {

        val values = ContentValues().apply {
            put("text", text)
            put("author", author)
            put("category", category)
        }

        db.insert(TABLE_QUOTES, null, values)
    }

    fun addUrl(
        url: String,
        enabled: Boolean
    ) {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("url", url)
            put("enabled", if (enabled) 1 else 0)
        }

        db.insert(TABLE_URLS, null, values)
    }

    fun addHistory(
        domain: String,
        timestamp: Long
    ) {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("domain", domain)
            put("visited_at", timestamp)
        }

        db.insert(TABLE_HISTORY, null, values)
    }

    fun getAllUrls(): MutableList<UrlItem> {

        val urlList = mutableListOf<UrlItem>()

        val db = readableDatabase

        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_URLS ORDER BY id DESC",
            null
        )

        if (cursor.moveToFirst()) {

            do {

                val id =
                    cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                    )

                val url =
                    cursor.getString(
                        cursor.getColumnIndexOrThrow("url")
                    )

                val enabled =
                    cursor.getInt(
                        cursor.getColumnIndexOrThrow("enabled")
                    ) == 1

                urlList.add(
                    UrlItem(
                        id = id,
                        url = url,
                        enabled = enabled
                    )
                )

            } while (cursor.moveToNext())
        }

        cursor.close()

        return urlList
    }

    fun deleteUrl(id: Int) {

        val db = writableDatabase

        db.delete(
            TABLE_URLS,
            "id=?",
            arrayOf(id.toString())
        )
    }

    fun updateUrlStatus(
        id: Int,
        enabled: Boolean
    ) {

        val db = writableDatabase

        val values = ContentValues().apply {

            put(
                "enabled",
                if (enabled) 1 else 0
            )
        }

        db.update(
            TABLE_URLS,
            values,
            "id=?",
            arrayOf(id.toString())
        )
    }


    fun getHistory(): MutableList<HistoryItem> {

        val historyList = mutableListOf<HistoryItem>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_HISTORY ORDER BY visited_at DESC",
            null
        )

        if (cursor.moveToFirst()) {

            do {

                historyList.add(
                    HistoryItem(
                        id = cursor.getInt(
                            cursor.getColumnIndexOrThrow("id")
                        ),
                        domain = cursor.getString(
                            cursor.getColumnIndexOrThrow("domain")
                        ),
                        timestamp = cursor.getLong(
                            cursor.getColumnIndexOrThrow("visited_at")
                        )
                    )
                )

            } while (cursor.moveToNext())
        }

        cursor.close()

        return historyList
    }

    fun clearHistory() {

        val db = writableDatabase

        db.delete(
            TABLE_HISTORY,
            null,
            null
        )
    }

    fun deleteHistoryItem(id: Int) {
      //
        val db = writableDatabase

        db.delete(
            TABLE_HISTORY,
            "id=?",
            arrayOf(id.toString())
        )
    }

    fun isUrlBlocked(
        domain: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT 1
        FROM $TABLE_URLS
        WHERE enabled = 1
        AND (
            url = ?
            OR ? LIKE '%.' || url
        )
        LIMIT 1
        """.trimIndent(),
            arrayOf(
                domain,
                domain
            )
        )

        val blocked = cursor.moveToFirst()

        cursor.close()

        return blocked
    }

}