package com.tencent.nativechannel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.CLASS;

@Target(ElementType.TYPE)
@Retention(CLASS)
public @interface FlutterPlugin {

    /**
     * 要定义的插件名
     * @return
     */
    String value();
}
