package com.tencent.bible.tunnel.plugin

import com.tencent.bible.tunnel.parser.BibleModuleOptionsConfig

class BibleWorkspace {

    String bibleComponentsWorkspace

    String bizComponentsWorkspace

    String bizModulesWorkspace


    /**
     * 非业务组件
     * @param workspace
     * @return
     */
    def bibleComponents(String workspace) {
        this.bibleComponentsWorkspace = workspace
    }

    /**
     * 配置业务组件
     * @param workspace
     * @return
     */
    def bizComponents(String workspace) {
        this.bizComponentsWorkspace = workspace
    }


    /**
     * 配置业务模块
     * @param workspace
     * @return
     */
    def bizModules(String workspace) {
        this.bizModulesWorkspace = workspace
    }


    def getBibleComponentsWorkspace() {
        if (bibleComponentsWorkspace != null) {
            return bibleComponentsWorkspace
        }
        return createModuleOptionsConfigIfNeeded().bibleComponentsDir
    }

    def getBizComponentsWorkspace() {
        if (bizComponentsWorkspace != null) {
            return bizComponentsWorkspace
        }
        return createModuleOptionsConfigIfNeeded().bizComponentsDir
    }

    def getBizModulesWorkspace() {
        if (bizModulesWorkspace != null) {
            return bizModulesWorkspace
        }
        return createModuleOptionsConfigIfNeeded().bizModulesDir
    }


    private def createModuleOptionsConfigIfNeeded() {
        if (moduleOptionsConfig == null) {
            moduleOptionsConfig = new BibleModuleOptionsConfig()
        }
        return moduleOptionsConfig
    }

    volatile BibleModuleOptionsConfig moduleOptionsConfig

}