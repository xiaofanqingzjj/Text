package com.tencent.bible.tunnel.plugin.versions

import org.gradle.api.Project

/**
 * 一个依赖对象
 *
 * 要么表示一个工程，要么表示一个仓库组件
 *
 *
 */
class ProjectVersionInfo {

    // 表示工程的时候有值

    // 工程名
    String projectName
    // 工程路径
    String projectPath

    // 表示仓库组件的时候有值
    String version
    String group
    String name

    def getTranslateModuleName() {
        if (group.startsWith("com.tencent.gamehelper")) {
            if (group == "com.tencent.gamehelper") {
                return "gamehelper"
            }
            return "ghp_${group.replaceAll("com.tencent.gamehelper.","").replaceAll("\\.","_")}"
        } else if (group.startsWith("com.tencent.bible")) {
            return "bible"
        } else {
            return "other"
        }
    }

    def getReleativePath(Project project) {
        return projectPath != null ? projectPath.replace(project.rootProject.projectDir.absolutePath, "") : ""
    }

    @Override
    public String toString() {
        return "ProjectVersionInfo{" +
                "projectName='" + projectName + '\'' +
                ", projectPath='" + projectPath + '\'' +
                ", version='" + version + '\'' +
                ", group='" + group + '\'' +
                ", name='" + name + '\'' +
                '}'
    }
}