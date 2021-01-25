package com.tencent.fp;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PluginCenterManager {

    public static void init(PluginCenter pluginCenter) {


        try {
            Class clazz = Class.forName("com.tencent.fp.generate.$PluginRegister");
            Object obj = clazz.newInstance();
            Method method = obj.getClass().getMethod("registers", PluginCenter.class);
            method.invoke(clazz, pluginCenter);
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("PluginCenterManager", "", e);
        }

        //

//        sPluginMaps["account"] = AccountMangerPlugin()


        // 通过发射调用就行了
    }
}
