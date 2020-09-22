package com.tencent.bible.tunnel.plugin

import com.tencent.bible.tunnel.plugin.dependency.BibleModuleDependency
import com.tencent.bible.tunnel.plugin.dependency.ExportModulePublishOrders
import com.tencent.bible.tunnel.plugin.versions.ExportModuleInfoTask
import com.tencent.bible.tunnel.plugin.versions.ProjectVersionInfo
import org.gradle.api.Plugin
import org.gradle.api.Project


class BibleTunnelPlugin implements Plugin<Project> {

    public static final String EXTENSION_NAME = "bbeTunnel"
    public static final String PLUGIN_NAME = "bbe-tunnel"

    private Project project


    private LocalProjectNameFinder projectNameFinder = new LocalProjectNameFinder()

    def isDebugModule() {
        //直接找rootProject的设置，不再支持单工程的配置，否则会出现不同工程不一样，这样会导致有的依赖lib工程，有的依赖远程，最后编译不过（代码重复）
        if (project.rootProject.extensions.findByName(EXTENSION_NAME) != null && project.rootProject.extensions.bbeTunnel.hasSetDebug) {
            return project.rootProject.extensions.bbeTunnel.isDebugModule
        }
        return false
    }

    def isUseDevApi() {
        if (project.rootProject.extensions.findByName(EXTENSION_NAME) != null) {
            //如果设置了useDevApi，则以useDevApi为准，否则以isDebugModule为准
            if (project.rootProject.extensions.bbeTunnel.hasSetUseDevApi) {
                return project.rootProject.extensions.bbeTunnel.isUseDevApi
            }
            return project.rootProject.extensions.bbeTunnel.isDebugModule
        }
        return false
    }

    def isNeedDebug() {
        return isDebugModule()
    }

//    def bbeTunnel(Object dependencyNotation, String localProjectName) {
//        if (isNeedDebug()) {
//            return localProjectName
//        }
//        return dependencyNotation
//    }


    /**
     * 依赖某一个组件
     *
     * @param dependencyNotation 依赖的对象
     * @param localProjectName
     * @param closure
     * @return
     */
    def bbeCompile(Object dependencyNotation, String localProjectName = "", Closure closure = {}) {
        println("bbeCompile object:$dependencyNotation, localProjectName:$localProjectName")
        bbeTunnel(this.project.dependencies.&compile, dependencyNotation, localProjectName, closure)
    }

    def bbeApi(Object dependencyNotation, String localProjectName = "", Closure closure = {}) {
        bbeTunnel(this.project.dependencies.&api, dependencyNotation, localProjectName, closure)
    }

    def bbeImplementation(Object dependencyNotation, String localProjectName = "", Closure closure = {
    }) {
        bbeTunnel(this.project.dependencies.&implementation, dependencyNotation, localProjectName, closure)
    }

    def bbeCompileOnly(Object dependencyNotation, String localProjectName = "", Closure closure = {
    }) {
        bbeTunnel(this.project.dependencies.&compileOnly, dependencyNotation, localProjectName, closure)
    }

    def bbeProvide(Object dependencyNotation, String localProjectName = "", Closure closure = {}) {
        bbeTunnel(this.project.dependencies.&provide, dependencyNotation, localProjectName, closure)
    }

    def amcApi(Object dependencyNotation) {
        String localProjectName = null
        ProjectVersionInfo projectVersionInfo = projectNameFinder.getLocalProjectInfo(this.project, dependencyNotation)
        if (projectVersionInfo != null) {
            localProjectName = projectVersionInfo.projectName
        }
        if (localProjectName != null && !localProjectName.isEmpty() && projectVersionInfo != null && isUseDevApi()) {
            if (!localProjectName.startsWith(":")) {
                localProjectName = ":${localProjectName}"
            }

            Project apiHostProject = project.project(localProjectName)
            String amcApiDirName = 'amc-api'
            File amcApiDir = apiHostProject.file(amcApiDirName)

            if (amcApiDir.exists() && amcApiDir.listFiles().findAll { it.name.endsWith(".jar") }.size() > 0) {
                this.project.dependencies.compileOnly(apiHostProject.fileTree(dir: 'amc-api', include: ['*.jar']))
            } else {
                this.project.dependencies.compileOnly dependencyNotation
            }
        } else {
            this.project.dependencies.compileOnly dependencyNotation
        }
    }


    /**
     *
     * @param dependenceMethod 依赖方式
     * @param dependencyNotation 依赖的对象
     * @param localProjectName
     * @param closure
     * @return
     */
    def bbeTunnel(dependenceMethod, Object dependencyNotation, String localProjectName = "", Closure closure = { }) {

        ProjectVersionInfo projectVersionInfo

        if (localProjectName == null || localProjectName.isEmpty()) {

            // 根据依赖名找是否有本地工程，如果有本地工程，则用本地工程
            // 根据依赖名返回真实的依赖对象
            projectVersionInfo = projectNameFinder.getLocalProjectInfo(this.project, dependencyNotation)

            //
            if (projectVersionInfo != null) {
                localProjectName = projectVersionInfo.projectName
            }

        } else {
            projectVersionInfo = projectNameFinder.getLocalProjectInfoByName(this.project, localProjectName)
        }


        addDependency(localProjectName)

        // 使用使用本地工程依赖
        if (localProjectName != null && !localProjectName.isEmpty() && projectVersionInfo != null && isNeedDebug()) {


            if (!localProjectName.startsWith(":")) {
                localProjectName = ":${localProjectName}"
            }

            if (project.findProject(localProjectName) != null) {

                //这里是将两个closure进行合并
                Closure finalClosure = {
                    project.extensions.bbeTunnel.excludeSettings.each {
                        Map map = it as Map
                        String groupName = map['group']
                        String moduleName = map['module']
                        if (groupName) {
                            if (moduleName) {
                                exclude group: groupName, module: moduleName
                            } else {
                                exclude group: groupName
                            }
                        }
                    }
                } << closure

                //这里之所以通过dependencies来调用对象是因为如果直接调用方法（如dependenceMethod(project(localProjectName), finalClosure)）
                //会找不到这个方法，因为compile、implementation之类的方法本来就是不存在的，所以外面传进来的就是一个不存在的方法
                //但是通过dependencies去调用则会走到DependencyHandler的methodMissing()，最后被转到调用add()方法，从而添加依赖。
                //gradle原先也是通过这种方法来实现添加依赖的

//            this.project.dependencies."${dependenceMethod.method}"(project.project(projectVersionInfo.projectPath), finalClosure)
                //println("${project.name} ${dependenceMethod.method} project(${localProjectName})")
                this.project.dependencies."${dependenceMethod.method}"(project.project(localProjectName), finalClosure)
            } else {
                //println("${project.name} ${dependenceMethod.method} maven ${dependencyNotation} ")
                this.project.dependencies."${dependenceMethod.method}"(dependencyNotation, closure)
            }
        } else {
            //println("${project.name} ${dependenceMethod.method} maven ${dependencyNotation} ")

            // 用原依赖
            this.project.dependencies."${dependenceMethod.method}"(dependencyNotation, closure)
        }
    }

    def findModuleDependency(HashMap<String, BibleModuleDependency> dependencyHashMap, String localProjectName) {
        BibleModuleDependency moduleDependency = dependencyHashMap.get(localProjectName)
        if (moduleDependency == null) {
            moduleDependency = new BibleModuleDependency()
            moduleDependency.projectName = localProjectName
            dependencyHashMap.put(localProjectName, moduleDependency)
        }
        return moduleDependency
    }

    def initDependencyMapIfNeeded() {
        if (!this.project.rootProject.hasProperty(ExportModulePublishOrders.KEY_DEPENDENCY_MAP)) {
            this.project.rootProject.ext.bible_dependency_map = new HashMap<>()
        }
    }

    def addDependency(String localProjectName) {
        if (localProjectName == null || localProjectName.isEmpty()) {
            return
        }

        initDependencyMapIfNeeded()

        HashMap<String, BibleModuleDependency> dependencyHashMap = this.project.rootProject.ext.bible_dependency_map

        //当前模块的BibleModuleDependency
        BibleModuleDependency moduleDependency = findModuleDependency(dependencyHashMap, this.project.name)

        //被依赖的模块的BibleModuleDependency
        BibleModuleDependency relierDependency = findModuleDependency(dependencyHashMap, localProjectName)

        moduleDependency.dependencies.add(relierDependency)
        relierDependency.owners.add(moduleDependency)
    }

    def addCurrentProjectToDependencyMap() {
        initDependencyMapIfNeeded()
        HashMap<String, BibleModuleDependency> dependencyHashMap = this.project.rootProject.ext.bible_dependency_map
        findModuleDependency(dependencyHashMap, this.project.name)
    }


    @Override
    void apply(Project project) {

        println("apply BibleTunnelPlugin")

        this.project = project
        project.ext.bbeTunnel = this.&bbeTunnel
        project.ext.bbeCompile = this.&bbeCompile
        project.ext.bbeApi = this.&bbeApi
        project.ext.bbeImplementation = this.&bbeImplementation
        project.ext.bbeCompileOnly = this.&bbeCompileOnly
        project.ext.bbeProvide = this.&bbeProvide
        project.ext.amcApi = this.&amcApi

        project.extensions.add(EXTENSION_NAME, BibleTunnelExtension)

        if (project == project.rootProject) {
            project.tasks.create("exportModulesInfo").doLast {
                new ExportModuleInfoTask().export(project)
            }
            project.tasks.create("exportModulesInfoForPublish").doLast {
                new ExportModuleInfoTask().export(project)
            }
            project.tasks.create("exportPublishOrder").doLast {
                new ExportModulePublishOrders().export(project)
            }
        }
        // 只要apply了bbe-tunnel plugin都往map里添加一下，这样publishOrder才不会漏掉部分没有被任何工程依赖的情况
        addCurrentProjectToDependencyMap()
    }
}