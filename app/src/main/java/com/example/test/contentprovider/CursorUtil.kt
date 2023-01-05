package com.example.test.contentprovider

import android.database.Cursor


/**
 * 把数据库Cusor转成一个列表
 */
fun <T>  Cursor.toList(parseItem: (cursor: Cursor)->T): List<T> {
    var list = mutableListOf<T>()
    while (this.moveToNext()) {
        val data = parseItem(this)
        list.add(data)
    }
    this.close()
    return list
}