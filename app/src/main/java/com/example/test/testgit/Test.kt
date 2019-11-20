package com.example.test.testgit



object SimpleContentAddr {

    private val map = mutableMapOf<String, Any>()

    fun put(content: Any) : String{
        val key = content.hashCode().toString()
        map[key] = content
        return key
    }

    fun get(key: String): Any? {
        return map[key]
    }

}