package com.tencent.bible.tunnel.plugin.versions

import com.tencent.bible.tunnel.plugin.BibleTunnelPlugin
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import org.gradle.api.Project


class ExportModuleInfoTask {


    public static final String BIBLE_MODULE_INFO_FILE_NAME = "bible_modules_info.json"
    public static final String DEFUALT_BIBLE_OUTPUT_VERSION_FILE_NAME = "bible_modules_version.json"

    private static final String AMCX_PLUGIN = "com.tencent.bible.amcx"


    static def getBibleModuleInfoFile(Project project) {
        return project.rootProject.file(BIBLE_MODULE_INFO_FILE_NAME)
    }

    // 获取用于记录版本号的json文件名称，用户可配置，默认为bible_modules_version.json
    static def getOutputVersionFile(Project project) {
        String fileName = DEFUALT_BIBLE_OUTPUT_VERSION_FILE_NAME
        if (project.rootProject.extensions.findByName(BibleTunnelPlugin.EXTENSION_NAME) != null) {
            String outputVersionFileName = project.rootProject.extensions.bbeTunnel.outputVersionFileName
            if (outputVersionFileName != null && !outputVersionFileName.isEmpty()) {
                if (outputVersionFileName.indexOf(".") == -1) {
                    outputVersionFileName = "${outputVersionFileName}.json"
                }
                fileName = outputVersionFileName
            }
        } else {
            println("cannot find extensions bbeTunnel")
        }

        return project.rootProject.file(fileName)
    }

    void addProjectInfo(HashMap<String, ArrayList<ProjectVersionInfo>> versionMap, ProjectVersionInfo projectInfo) {
        if (projectInfo != null) {
            String moduleName = projectInfo.getTranslateModuleName()
            ArrayList<ProjectVersionInfo> versionInfos = versionMap.get(moduleName)
            if (versionInfos == null) {
                versionInfos = new ArrayList<>()
                versionMap.put(moduleName, versionInfos)
            }
            versionInfos.add(projectInfo)
        }
    }

    void export(Project project) {
        Set<Project> projects = project.rootProject.getAllprojects()
        if (projects != null) {
            HashMap<String, ArrayList<ProjectVersionInfo>> versionMap = new HashMap<>()
            for (Project p : projects) {
                if (p != project.rootProject && p.file("gradle.properties").exists()) {
                    addProjectInfo(versionMap, parseVersionInfo(p))
                    addProjectInfo(versionMap, parseApiProjectInfo(p))
                }
            }
            writeFile(project, versionMap)
        }
    }

    ProjectVersionInfo parseVersionInfo(Project project) {
        ProjectVersionInfo projectInfo = new ProjectVersionInfo()
        projectInfo.projectName = project.getName()
        projectInfo.projectPath = project.projectDir.absolutePath
        projectInfo.version = project.properties["VERSION_NAME"]
        projectInfo.name = project.properties["ARTIFACT_ID"]
        projectInfo.group = project.properties["GROUP_ID"]
        return projectInfo
    }

    ProjectVersionInfo parseApiProjectInfo(Project project) {
        if (project.getPluginManager().hasPlugin(AMCX_PLUGIN)) {
            ProjectVersionInfo projectInfo = new ProjectVersionInfo()
            projectInfo.name = project.extensions.amcx.apiArtifactId
            if (projectInfo.name == null || projectInfo.name.isEmpty()) {
                projectInfo.name = project.properties["ARTIFACT_ID"] + "-api"
            }
            // api的工程名称也设置为宿主工程
            projectInfo.projectName = project.getName()
            projectInfo.projectPath = project.projectDir.absolutePath

            projectInfo.group = project.extensions.amcx.apiGroupId
            if (projectInfo.group == null || projectInfo.group.isEmpty()) {
                projectInfo.group = project.GROUP_ID
            }
            if (project.hasProperty("API_VERSION_NAME")) {
                projectInfo.version = project.API_VERSION_NAME
            }
            if (projectInfo.version == null || projectInfo.version.isEmpty()) {
                if (project.hasProperty("VERSION_NAME")) {
                    if (project.hasProperty("SNAPSHOT_API")) {
                        if (Boolean.parseBoolean(project.SNAPSHOT_API)) {
                            projectInfo.version = project.VERSION_NAME + "-SNAPSHOT"
                        } else {
                            projectInfo.version = project.VERSION_NAME
                        }
                    } else {
                        projectInfo.version = project.VERSION_NAME
                    }
                }
            }
            return projectInfo

        }
        return null
    }


    void writeFile(Project project, HashMap<String, ArrayList<ProjectVersionInfo>> versionMap) {
        //输出 bible_modules_info.json
        def json = new JsonBuilder()
        json {
            mapping {
                versionMap.each { k, v ->
                    v.findAll { ProjectVersionInfo projectInfo ->
                        projectInfo.projectName != null && !projectInfo.projectName.isEmpty()
                    }.each { ProjectVersionInfo projectInfo ->
                        "${projectInfo.group}:${projectInfo.name}" {
                            name "${projectInfo.projectName}"
                            path "${projectInfo.getReleativePath(project)}"
                        }
                    }
                }
            }
        }
        def modulesInfoFile = getBibleModuleInfoFile(project)

        modulesInfoFile.withWriter('UTF-8') { writer ->
            writer.write(JsonOutput.prettyPrint(json.toString()))
        }

        // 输出version.json
        def versionJson = new JsonBuilder()
        versionJson {
            versionMap.each { k, v ->
                "$k" {
                    v.each { ProjectVersionInfo projectInfo ->
                        "${projectInfo.name.replaceAll("-", "_")}" "${projectInfo.group}:${projectInfo.name}:${projectInfo.version}"
                    }
                }
            }
        }
        def versionFile = getOutputVersionFile(project)
        versionFile.withWriter('UTF-8') { writer ->
            writer.write(JsonOutput.prettyPrint(versionJson.toString()))
        }

//        System.out.println(JsonOutput.prettyPrint(json.toString()))
    }

//    private static Properties createProperties(Project project) {
//        Properties properties = new Properties()
//        project.file("gradle.properties").withInputStream {
//            properties.load(it)
//        }
//        return properties
//    }


}