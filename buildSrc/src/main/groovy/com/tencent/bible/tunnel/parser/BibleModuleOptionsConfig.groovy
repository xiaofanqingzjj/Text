package com.tencent.bible.tunnel.parser

import groovy.json.JsonSlurper


class BibleModuleOptionsConfig {

    private HashMap<String, String> workSpaceDirs = new HashMap<>()


    private String bibleComponentsDir
    private String bizComponentsDir
    private String bizModulesDir

    BibleModuleOptionsConfig(String bibleComponentsDir = null, String bizComponentsDir = null, String bizModulesDir = null) {
        this.bibleComponentsDir = bibleComponentsDir
        this.bizComponentsDir = bizComponentsDir
        this.bizModulesDir = bizModulesDir
        parseModuleOptions()
    }

    private parseJSON(File jsonFile) {
        def jsonSlurper = new JsonSlurper()
        def reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), "UTF-8"))
        def data = jsonSlurper.parse(reader)
        return data
    }

    private def parseModuleOptions() {
        File jsonFile = new File(System.getProperty("user.home") + "/.bible/android/module_options.json")
        if (jsonFile.exists()) {
            def data = parseJSON(jsonFile)
            data.workspace_dirs.each { k, v ->
                if (v instanceof File) {
                    workSpaceDirs.put(k, v.absolutePath)
                } else {
                    workSpaceDirs.put(k, v)
                }
            }
        }
    }

    def getBibleComponentsDir() {
        if (bibleComponentsDir != null && !bibleComponentsDir.isEmpty()) {
            return bibleComponentsDir
        }
        return workSpaceDirs.get("bible_components")
    }

    def getBizComponentsDir() {
        if (bizComponentsDir != null && !bizComponentsDir.isEmpty()) {
            return bizComponentsDir
        }
        return workSpaceDirs.get("biz_components")
    }

    def getBizModulesDir() {
        if (bizModulesDir != null && !bizModulesDir.isEmpty()) {
            return bizModulesDir
        }
        return workSpaceDirs.get("biz_modules")
    }
}