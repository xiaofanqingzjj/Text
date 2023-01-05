package com.fortunexiao.tktx

import android.database.Cursor


/**
 * 把数据库Cusor转成一个列表
 */
fun <T>  Cursor.toList(parseItem: (cursor: Cursor)->T): List<T> {
    val list = mutableListOf<T>()
    while (this.moveToNext()) {
        val data = parseItem(this)
        list.add(data)
    }
    this.close()
    return list
}