package com.tencent.bible.tunnel.plugin

import org.gradle.api.Project

class BibleWorkspaceUtils {

    static def getBibleWorkspace(Project project) {
        BibleWorkspace bibleWorkspace = null

        // 获取扩展对象
        BibleTunnelExtension extension = project.rootProject.extensions.findByName(BibleTunnelPlugin.EXTENSION_NAME)

        if (extension != null && extension.workspace != null) {
            bibleWorkspace = extension.workspace
        }

        if (bibleWorkspace == null) {
            bibleWorkspace = new BibleWorkspace()
        }

        return bibleWorkspace
    }

    static def getBibleComponentsWorkspace(Project project) {
        return getWorkspaceString(getBibleWorkspace(project).getBibleComponentsWorkspace())

    }

    static def getBizComponentsWorkspace(Project project) {
        return getWorkspaceString(getBibleWorkspace(project).getBizComponentsWorkspace())
    }

    static def getBizModulesWorkspace(Project project) {
        return getWorkspaceString(getBibleWorkspace(project).getBizModulesWorkspace())
    }

    static String getWorkspaceString(def workspace) {
        if (workspace instanceof File) {
            return workspace.absolutePath
        }
        return workspace
    }

}