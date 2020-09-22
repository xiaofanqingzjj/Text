package com.tencent.bible.tunnel.plugin

import org.gradle.util.ConfigureUtil

class BibleTunnelExtension {

    /**
     * 是否是debug模式
     */
    boolean isDebugModule

    boolean hasSetDebug

    boolean isSupportBatchPublish = true

    boolean isUseDevApi

    boolean hasSetUseDevApi

    // 输出的版本信息文件名称
    String outputVersionFileName

    // 工作空间
    BibleWorkspace workspace

    // bbeTunnel的全局exclude设置
    List<Map<String, String>> excludeSettings = new ArrayList()

    def debug(boolean isDebugModule) {
        this.hasSetDebug = true
        this.isDebugModule = isDebugModule
    }

    def exclude(Map args) {
        excludeSettings.add(args)
    }

    /**
     * 添加工作目录
     * @param closure
     * @return
     */
    def workspace(Closure closure) {

        // 把某个对象给某个闭包，作为闭包的代理对象
        workspace = ConfigureUtil.configure(closure, new BibleWorkspace())
    }

    def outputVersionFile(String name) {
        outputVersionFileName = name
    }

    def useDevApi(boolean enable) {
        this.isUseDevApi = enable
        this.hasSetUseDevApi = true
    }

    def supportBacthPublish(boolean support) {
        isSupportBatchPublish = support
    }

}