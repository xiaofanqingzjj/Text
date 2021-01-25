package com.mingwei.aptdemo;

import com.tencent.gamehelper.flutter_plugin.PluginCenter;
import com.tencent.gamehelper.flutter_plugin.PluginCenterManager;

import java.util.HashMap;

public class GameHelperPluginCenter implements PluginCenter {


    HashMap<String, Object> pluginsMap = new HashMap<>();

    public static GameHelperPluginCenter instance = new GameHelperPluginCenter();

    public static void init() {

//        PluginCenterManager.init();

        PluginCenterManager.init(instance);
    }

//    public static void registerPlugin(String name, Object plugin) {
//        pluginsMap.put(name, plugin);
//    }

    @Override
    public void resister(String name, Object plugin) {
        pluginsMap.put(name, plugin);

    }
}
