package com.example.testaop.aop;

import android.os.SystemClock;
import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;


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
    @Pointcut("execution(@com.example.testaop.aop.TimeConsume * *(..))")
    public void methodTimeConsumePoint(){

    }

    @Pointcut("execution(* com.example.testaop.Foo2.*(..))")
    public void methodTimeConsumePoint2(){

    }



    //通过注解，获取到被注解的方法
    @Around("methodTimeConsumePoint()")
    public Object aroundMethodConsume(ProceedingJoinPoint joinPoint) throws Throwable{


        String signature = joinPoint.getSignature().toLongString();
        Object[] args = joinPoint.getArgs();
        Log.d(TAG, "signature:" + signature + ", args:" + Helper.printArgs(args));

        long beforeTime = SystemClock.elapsedRealtime();
        Object result = null;
        try {
            result = joinPoint.proceed();
            Log.d(TAG, "result:" + result);
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "aspect aroundShopMall: exception");
            String name = joinPoint.getSignature().getName();
            long afterTime = SystemClock.elapsedRealtime();
            Log.d(TAG, "aspect2: "+name+" 耗时="+(afterTime-beforeTime)+"ms");
        }
        String name = joinPoint.getSignature().toShortString();
        long afterTime = SystemClock.elapsedRealtime();
        Log.d(TAG, "aspect: " + name + " 耗时="+(afterTime-beforeTime)+"ms");
        return result;
    }

}