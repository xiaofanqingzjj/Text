package com.example.test.aop;

import android.os.SystemClock;
import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


/**
 * 任意定义个类，加上@Aspect注解来标识这是一个切面类
 */
@Aspect
public class TimeConsumeAspect {
    private static final String TAG = "TimeConsumeAspect";

    public static TimeConsumeAspect aspectOf(){
        return new TimeConsumeAspect();
    }


    /**
     * 任意定义个方法，使用@Pointcut注解来定义切入点
     * Pointcut可以指定切入点为某个类的某个方法，或者匹配一类方法
     *
     *
     */
    @Pointcut("execution(@com.example.test.aop.TimeConsume * *(..))")
    public void methodTimeConsumePoint(){

    }

    //通过注解，获取到被注解的方法
    @Around("methodTimeConsumePoint()")
    public Object aroundMethodConsume(ProceedingJoinPoint joinPoint) throws Throwable{
        long beforeTime = SystemClock.elapsedRealtime();
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "aspect aroundShopMall: exception");
            String name = joinPoint.getSignature().getName();
            long afterTime = SystemClock.elapsedRealtime();
            Log.d(TAG, "aspect2: "+name+" 耗时="+(afterTime-beforeTime)+"ms");
        }
        String name = joinPoint.getSignature().getName();
        long afterTime = SystemClock.elapsedRealtime();
        Log.d(TAG, "aspect: "+name+" 耗时="+(afterTime-beforeTime)+"ms");
        return result;
    }

}