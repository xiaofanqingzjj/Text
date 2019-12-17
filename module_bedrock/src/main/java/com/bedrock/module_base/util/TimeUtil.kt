package com.bedrock.module_base.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {

    private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    fun format(time: Long, format: String? = DEFAULT_DATE_FORMAT): String {
        return SimpleDateFormat(format).format(Date().apply {
            setTime(time)
        })
    }
}