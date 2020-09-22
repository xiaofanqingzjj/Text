package com.tencent.bible.tunnel.plugin.dependency

import com.tencent.bible.tunnel.plugin.BibleTunnelPlugin
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import org.gradle.api.GradleException
import org.gradle.api.Project

class ExportModulePublishOrders {

    public static String KEY_DEPENDENCY_MAP = "bible_dependency_map"

    void export(Project project) {
        // 这个projectNames最终只是用来判断工程是不是我们内部支持批量发布的工程
        ArrayList<String> projectNames = new ArrayList<>()
//        ArrayList<String> projectNames = BibleModules.getModules(project)
//        if (projectNames == null || projectNames.size() == 0) {
        project.rootProject.getSubprojects().each {
            // 过滤掉那些调试的工程，比如test.xxx
            if (it.file("gradle.properties").exists() && it.properties.containsKey("ARTIFACT_ID")) {
                projectNames.add(it.name)
            }
        }
//        }


        if (project.rootProject.hasProperty(ExportModulePublishOrders.KEY_DEPENDENCY_MAP)) {
            HashMap<String, BibleModuleDependency> dependencyHashMap = project.rootProject.ext.bible_dependency_map

            // 过滤掉不支持批量发布的工程
            filterUnSupportBatchPublishProject(project,dependencyHashMap, projectNames)
            // 过滤掉其他仓库的依赖，比如有可能依赖了BibleComponents里的组件，如果不是自己工程或者不支持批量发布的则忽略掉
            filterDependencies(dependencyHashMap, projectNames)

            HashMap<String, Integer> priorityMap = new HashMap<>()
            dependencyHashMap.each { k, v ->
                computeDependencyPriority(priorityMap, v, 1)
            }

            def orderedMap = priorityMap.sort { a, b -> b.value <=> a.value }

            def json = new JsonBuilder()
            json {
                priority orderedMap
                orders orderedMap.keySet()
            }

            def outputFolder = new File(project.rootProject.buildDir, "bible")
            FileUtils.forceMkdir(outputFolder)

            def buildOrderFile = new File(outputFolder, "bible_publish_order.json")
            buildOrderFile.withWriter('UTF-8') { writer ->
                writer.write(JsonOutput.prettyPrint(json.toString()))
            }

            //System.out.println("final priority is : $priorityMap")
        }
    }

    boolean isAllOwnerUnSupportBatchPublish(String targetProjectName, BibleModuleDependency owner, ArrayList<String> notSupportBatchPublishProject) {
        if (notSupportBatchPublishProject.contains(owner.projectName)) {
            if (owner.owners != null || owner.owners.size() > 0) {
                for (BibleModuleDependency dependency : owner.owners) {
                    if (!isAllOwnerUnSupportBatchPublish(targetProjectName, dependency, notSupportBatchPublishProject)) {
                        throw new GradleException("Project $targetProjectName is not support batch publish ,but it is dependent on project ${dependency.projectName} that supports batch publish.")
                    }
                }
                return true
            } else {
                return true
            }
        }
        return false
    }

    def filterUnSupportBatchPublishProject(Project project, HashMap<String, BibleModuleDependency> dependencyHashMap, ArrayList<String> projectNames) {
        ArrayList<String> notSupportBatchPublishProject = new ArrayList<>()

        // 找到不支持批量发布的工程
        projectNames.each {
            Project prj = project.findProject(it)
            if (prj != null) {
                def bbeExtension = prj.extensions.findByName(BibleTunnelPlugin.EXTENSION_NAME)
                if (bbeExtension != null && !bbeExtension.isSupportBatchPublish) {
                    notSupportBatchPublishProject.add(it)
                }
            }
        }

        // 确保所有不支持批量发布的工程没有被支持批量发布的工程依赖，否则抛出异常
        notSupportBatchPublishProject.each {
            BibleModuleDependency bibleModuleDependency = dependencyHashMap.get(it)
            if (bibleModuleDependency != null) {
                if (isAllOwnerUnSupportBatchPublish(it, bibleModuleDependency, notSupportBatchPublishProject)) {
                    projectNames.remove(it)
                }
            }
        }
    }

    def filterDependencies(HashMap<String, BibleModuleDependency> dependencyHashMap, ArrayList<String> projectNames) {
        ArrayList<String> toDeleteDependencyForMap = new ArrayList<>()
        dependencyHashMap.each { k, v ->
            if (projectNames.contains(v.projectName)) {
                ArrayList<String> toDeleteDependency = new ArrayList<>()
                v.dependencies.each { BibleModuleDependency dependency ->
                    if (!projectNames.contains(dependency.projectName)) {
                        toDeleteDependency.add(dependency)
                    }
                }
                toDeleteDependency.each {
                    v.dependencies.remove(it)
                }
            } else {
                toDeleteDependencyForMap.add(k)
            }
        }
        toDeleteDependencyForMap.each {
            dependencyHashMap.remove(it)
        }
    }

    /**
     * 递归找出自己依赖的组件，并给每个组件根据树的层级赋权重，层级越深的说明发布的顺序越靠前
     */
    def computeDependencyPriority(HashMap<String, Integer> priorityMap, BibleModuleDependency dependency, int level) {
        int priority = priorityMap.get(dependency.projectName, -1)
        // 如果之前map有的话则取出来比较哪个大，优先用更大的值
        priorityMap.put(dependency.projectName, Math.max(priority, level))
        if (dependency.dependencies.size() > 0) {
            level++
            dependency.dependencies.each {
                computeDependencyPriority(priorityMap, it, level)
            }
        }
    }


}