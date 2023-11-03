package com.tencent.nativechannel;


import java.lang.reflect.Method;

public class NativeChannelPluginManagerHelper {

    /**
     * 注册所有使用@FlutterPlugin修饰的插件
     */
    public static void registerPlugins() {
        try {
            Class clazz = Class.forName("com.tencent.nativechannel.generate.$PluginRegister");
            Method method = clazz.getDeclaredMethod("registers");
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("init :" + e);
        }
    }
}
