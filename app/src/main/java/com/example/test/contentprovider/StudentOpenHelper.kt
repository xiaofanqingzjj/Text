package com.example.test.contentprovider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


/**
 *
 */
class StudentOpenHelper(context: Context)
    : SQLiteOpenHelper(context, "my.db", null, 1) {

    companion object {
        const val TABLE_PERSON = "person"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // 创建表格
        val sql = "create table $TABLE_PERSON (id integer primary key autoincrement, name varchar(20), phone varchar(20), age integer, address varchar(50))";
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}