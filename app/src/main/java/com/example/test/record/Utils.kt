package com.example.test.record

import java.text.DecimalFormat


/**
 * 格式化显示文件大小
 */
fun formatSize(size: Long, format: String?= null): String {

    val formater = DecimalFormat(format ?: "####")


    return when {
        size < 1024 -> "${size}b"
        size < 1024 * 1024 -> {
            val kbSize = size / 1024f
            formater.format(kbSize) + "K"
        }
        size < 1024 * 1024 * 1024 -> {
            val mbSize = size / 1024f / 1024f
            formater.format(mbSize) + "M"
        }
//        size < 1024 * 1024 * 1024 * 1024 -> {
//            val gbSize = size / 1024f / 1024f / 1024f
//            formater.format(gbSize) + "G"
//        }
        else -> {
            //"size: error"

            val formatorG = DecimalFormat("####.##")

            val gbSize = size / 1024f / 1024f / 1024f
            formatorG.format(gbSize) + "G"
        }
    }


}