package com.tencent.bible.tunnel.plugin

import com.tencent.bible.tunnel.plugin.versions.ProjectVersionInfo
import groovy.json.JsonSlurper
import org.gradle.api.Project

class LocalProjectNameFinder {

    //里面存的是bible_modules_info.json里mapping字段的json对象

    // 保存了
    ArrayList<Map> mappings = new ArrayList()

    // 保存所有工程名到路径的映射
    HashMap<String, String> projectPathMappings = new HashMap()

    volatile boolean hasParsed

    def parseAllModulesInfo(Project project) {
        if (!hasParsed) {
            hasParsed = true

            // 三个组件工程路径
            ArrayList<String> workspaces = [BibleWorkspaceUtils.getBibleComponentsWorkspace(project),
                              BibleWorkspaceUtils.getBizComponentsWorkspace(project),
                              BibleWorkspaceUtils.getBizModulesWorkspace(project)]

            if (!workspaces.contains(project.rootProject.rootDir)) {
                workspaces << project.rootProject.rootDir
            }

            workspaces.each {
                String path
                if (it instanceof File) {
                    path = it.absolutePath
                } else {
                    path = it
                }

                if (path != null && !path.isEmpty()) {

                    // 在配置的
                    File jsonFile = new File(path, "bible_modules_info.json")

                    if (jsonFile.exists()) {
                        def data = parseJSON(jsonFile)
                        if (data != null) {
                            data.mapping.each { k, v ->
                                v.path = "$path/${v.path}"
                                projectPathMappings.put(v.name, v.path)
                            }
                            mappings.add(data.mapping)
                        }
                    } else {
                        println("not exist jsonFile ${jsonFile.absolutePath}")
                    }
                }
            }
        }
    }


    def getLocalProjectInfoByName(Project project, String localProjectName) {
        parseAllModulesInfo(project)
        if (localProjectName != null && projectPathMappings.containsKey(localProjectName)) {
            ProjectVersionInfo projectInfo = new ProjectVersionInfo()
            projectInfo.projectName = localProjectName
            projectInfo.projectPath = projectPathMappings.get(localProjectName)
            return projectInfo
        }

        return null
    }

    /**
     *
     * @param project
     * @param dependencyNotation 依赖名
     * @return 返回一个ProjectVersionInfo对象，表示一个依赖，要么是工程，要么是第三方组件
     */
    ProjectVersionInfo getLocalProjectInfo(Project project, Object dependencyNotation) {

        parseAllModulesInfo(project)

        ProjectVersionInfo projectInfo = null

        String notation
        if (dependencyNotation instanceof String) {
            notation = (String) dependencyNotation
        } else {
            notation = dependencyNotation.toString()
        }

        // 去掉依赖的版本号
        notation = notation.substring(0, notation.lastIndexOf(":"))

        //
        mappings.each {
            if (it.containsKey(notation)) {
                projectInfo = new ProjectVersionInfo()
                projectInfo.projectName = it."${notation}".name
                projectInfo.projectPath = it."${notation}".path
            }
        }
        return projectInfo

    }


    private static def parseJSON(File jsonFile) {
        try {
            def jsonSlurper = new JsonSlurper()
            def reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), "UTF-8"))
            def data = jsonSlurper.parse(reader)
            return data
        } catch (Exception e) {
            throw new RuntimeException("Parse jsonFile ${jsonFile.absolutePath} failed.", e)
        }
    }

}