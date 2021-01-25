package com.tencent.gamehelper.flutter_plugin;


import java.lang.reflect.Method;

public class PluginCenterManager {

    public static void init(PluginCenter pluginCenter) {
        try {
            Class clazz = Class.forName("com.tencent.fp.generate.PluginRegister");
            Object obj = clazz.newInstance();
            Method method = obj.getClass().getMethod("registers", PluginCenter.class);
            method.invoke(clazz, pluginCenter);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("init :" + e);
        }
    }
}
