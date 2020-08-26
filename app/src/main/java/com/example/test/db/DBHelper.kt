package com.example.test.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


/**
 *
 */
class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "Database.db"
        const val DATABASE_VERSION = 1
    }

    /**
     *
     */
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(PersonInfo.createTableSql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}


class DbHelper2(context: Context) {
    companion object {
        const val DATABASE_NAME = "Database.db"
        const val DATABASE_VERSION = 1
    }

    private val sqliteHelper: SQLiteOpenHelper =
            // 构造器传入数据库名和版本号
            object : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase?) {
            // 这里是数据库创建回调，在这里通过
            db?.execSQL(PersonInfo.createTableSql)
        }
        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        }
    }

    /**
     * 获取数据库对象，
     */
    fun getDb(): SQLiteDatabase? {
        return sqliteHelper.writableDatabase
    }
}


data class PersonInfo(var id: Int = 0, var name: String? = null, var gender: Int = 0, var number: String? = null, var score: Int = 0) {

    companion object {
        const val TABLE_NAME = "student"

        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_NUMBER = "number"
        const val COLUMN_SCORE = "score"

        val TABLE_COLUMNS = arrayOf(
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_GENDER,
                COLUMN_NUMBER,
                COLUMN_SCORE
        )

        const val createTableSql =  "CREATE TABLE IF NOT EXISTS " +
                "$TABLE_NAME  (  " +
                "$COLUMN_ID  INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_GENDER INTEGER, " +
                "$COLUMN_NUMBER TEXT, " +
                "$COLUMN_SCORE INTEGER)"
    }
}