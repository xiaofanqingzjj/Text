package com.example.testaop.aop

object Helper {

    @JvmStatic
    fun printArgs(args: Array<Any>?): String {
        val sb = StringBuilder()
        sb.append("[")
        if (args != null) {
            for (i in args.indices) {
                sb.append(args[i])
                if (i != args.size - 1) {
                    sb.append(", ")
                }
            }
        }
        sb.append("]")
        return sb.toString()
    }
}