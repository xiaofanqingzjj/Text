package com.example.testaop.aop

import android.util.Log
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

/**
 * 拦截TLog的日志打印
 */
@Aspect
class TLogAspect {

    companion object {
        const val TAG = "TLogAspect"
    }

    @Pointcut("execution(* com.tencent.common.log.TLog.*(..))")
    fun log() {

    }


    @Around("log()")
    fun interceptLog(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature.toLongString()
        val args = joinPoint.args
        Log.d(TAG, "signature:" + signature + ", args:" + Helper.printArgs(args))
        return joinPoint.proceed()
    }
}

