package com.tencent.bible.tunnel.parser

import groovy.json.JsonSlurper
import org.gradle.api.Project

class BibleModules {


    private static final String KEY_ROOT_MODULE = "rootModule"
    private BibleModuleOptionsConfig optionsConfig

    String bibleComponentsDir
    String bizComponentsDir
    String bizModulesDir

    private static parseJSON(File jsonFile) {
        def jsonSlurper = new JsonSlurper()
        def reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), "UTF-8"))
        def data = jsonSlurper.parse(reader)
        return data
    }

    private static def getBibleModulesJsonFile(String dir) {
        if (dir == null || dir.length() == 0) {
            return null
        }
        return new File(dir, "bible_modules.json")
    }

    private static def filterProjectName(String projectName) {
        if (projectName.startsWith(":")) {
            return projectName.substring(1)
        }
        return projectName
    }

    private def parseBlacklistModules(File blacklistJsonFile, Object settings) {
        return parseJsonFile(blacklistJsonFile, "blacklist", "bible_external_modules_black_list.json", settings)
    }

    private def parseWhitelistModules(File whitelistJsonFile, Object settings) {
        return parseJsonFile(whitelistJsonFile, "whitelist", "bible_external_modules_white_list.json", settings)
    }

    private BibleModuleOptionsConfig createOptionsConfigIfNeeded() {
        if (optionsConfig == null) {
            optionsConfig = new BibleModuleOptionsConfig(bibleComponentsDir, bizComponentsDir, bizModulesDir)
        }
    }

    private def parseJsonFile(File jsonFile, String jsonRoot, String defaultJsonFileName, Object settings) {
        if (jsonFile == null || !jsonFile.exists()) {
            jsonFile = new File(settings.getRootDir(), defaultJsonFileName)
        }
        def modules = new ArrayList()

        if (jsonFile != null && jsonFile.exists()) {
            def modulesData = parseJSON(jsonFile)
            modulesData."$jsonRoot".each {
                modules.add(filterProjectName(it))
            }
        }
        return modules
    }

    def includeAllModules(Object settings) {

        BibleModuleOptionsConfig bibleModuleOptionsConfig = createOptionsConfigIfNeeded()

        def jsonFiles = [bibleModuleOptionsConfig.getBibleComponentsDir(), bibleModuleOptionsConfig.getBizComponentsDir(), bibleModuleOptionsConfig.getBizModulesDir()]

        jsonFiles.each {
            File jsonFile = getBibleModulesJsonFile(it)
            includeModules(jsonFile, settings)
        }
    }

    def includeBizModules(Object settings, boolean ignoreBlacklist = false, File blacklistJsonFile = null) {
        includeModules(getBibleModulesJsonFile(createOptionsConfigIfNeeded().getBizModulesDir()), settings, ignoreBlacklist, blacklistJsonFile)
    }

    def includeSpecifiedBizModules(Object settings, File whitelistJsonFile = null) {
        includeSpecifiedModules(getBibleModulesJsonFile(createOptionsConfigIfNeeded().getBizModulesDir()), settings, whitelistJsonFile)
    }

    def includeBizComponents(Object settings, boolean ignoreBlacklist = false, File blacklistJsonFile = null) {
        includeModules(getBibleModulesJsonFile(createOptionsConfigIfNeeded().getBizComponentsDir()), settings, ignoreBlacklist, blacklistJsonFile)
    }

    def includeSpecifiedBizComponents(Object settings, File whitelistJsonFile = null) {
        includeSpecifiedModules(getBibleModulesJsonFile(createOptionsConfigIfNeeded().getBizComponentsDir()), settings, whitelistJsonFile)
    }

    def includeBibleComponents(Object settings, boolean ignoreBlacklist = false, File blacklistJsonFile = null) {
        includeModules(getBibleModulesJsonFile(createOptionsConfigIfNeeded().getBibleComponentsDir()), settings, ignoreBlacklist, blacklistJsonFile)
    }

    def includeSpecifiedBibleComponents(Object settings, File whitelistJsonFile = null) {
        includeSpecifiedModules(getBibleModulesJsonFile(createOptionsConfigIfNeeded().getBibleComponentsDir()), settings, whitelistJsonFile)
    }

    def includeModules(File moduleJsonFile, Object settings, boolean ignoreBlacklist = false, File blacklistJsonFile = null) {
        if (moduleJsonFile != null && moduleJsonFile.exists()) {
            def excludeModules = new ArrayList()
            if (!ignoreBlacklist) {
                excludeModules = parseBlacklistModules(blacklistJsonFile, settings)
            }

            def modulesData = parseJSON(moduleJsonFile)
            modulesData.each { k, v ->
                def subModules = v as String[]
                subModules.each {
                    def projectName = filterProjectName(it)
                    //过滤不需要的模块
                    if (!excludeModules.contains(projectName)) {
                        settings.include(it)
                        if (KEY_ROOT_MODULE.equalsIgnoreCase(k)) {
                            settings.project(it).projectDir = new File("${moduleJsonFile.getParentFile().absolutePath}/${projectName}")
                        } else {
                            settings.project(it).projectDir = new File("${moduleJsonFile.getParentFile().absolutePath}/${k}/${projectName}")
                        }
                    }
                }
            }
        }
    }

    def includeSpecifiedModules(File moduleJsonFile, Object settings, File whitelistJsonFile = null) {
        if (moduleJsonFile != null && moduleJsonFile.exists()) {
            def includeModules = parseWhitelistModules(whitelistJsonFile, settings)

            def modulesData = parseJSON(moduleJsonFile)
            modulesData.each { k, v ->
                def subModules = v as String[]
                subModules.each {
                    def projectName = filterProjectName(it)
                    if (includeModules.contains(projectName)) {
                        settings.include(it)
                        if (KEY_ROOT_MODULE.equalsIgnoreCase(k)) {
                            settings.project(it).projectDir = new File("${moduleJsonFile.getParentFile().absolutePath}/${projectName}")
                        } else {
                            settings.project(it).projectDir = new File("${moduleJsonFile.getParentFile().absolutePath}/${k}/${projectName}")
                        }
                    }
                }
            }
        }
    }

    static ArrayList<String> getModules(Project project) {
        File jsonFile = getBibleModulesJsonFile(project.rootProject.rootDir.absolutePath)
        ArrayList<String> projectList = new ArrayList<>()
        if (jsonFile != null && jsonFile.exists()) {
            def modulesData = parseJSON(jsonFile)
            modulesData.each { k, v ->
                def subModules = v as String[]
                subModules.each {
                    projectList.add(filterProjectName(it))
                }
            }
        }
        return projectList
    }

}