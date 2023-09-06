
package com.test_plugins

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle


/**
 * 给Settings对象增加一些扩展的使用方法
 */
class SettingsExtPlugin implements Plugin<Settings> {

    static  final String TAG = "SettingsExtPlugin"

    Settings settings;



    @Override
    void apply(Settings target) {
        Log.d(TAG, "apply target:${target}")
        this.settings = target

        // 给Settings增加
//        target.extensions.extraProperties["includeDir"] = this.&includeDir
        target.ext.includeDir =  this.&includeDir

        addBuildListener(target.gradle)
    }


    void addBuildListener(Gradle g) {

        g.addBuildListener(new BuildListener() {

            // 初始化阶段

            @Override
            void beforeSettings(Settings settings) {
                super.beforeSettings(settings)
                Log.d(TAG, "------------------------beforeSettings------------------------")
            }

            @Override
            void settingsEvaluated(Settings settings) {
                // setting文件执行完毕
                Log.d(TAG, "------------------------settingsEvaluated------------------------")

                Log.d(TAG, "-------- settingsEvaluated allProjects-------")
                gradle.allprojects(new Action<Project>() {
                    @Override
                    void execute(Project o) {
                        Log.d(TAG, "project:${o.name}")
                    }
                })
            }

            @Override
            void projectsLoaded(Gradle gradle) {
                Log.d(TAG, "------------------------projectsLoaded------------------------")
                Log.d(TAG, "--------allProjects-------")
                gradle.allprojects(new Action<Project>() {
                    @Override
                    void execute(Project o) {
                        Log.d(TAG, "project:${o.name}")
                    }
                })
            }





            @Override
            void projectsEvaluated(Gradle gradle) {
                Log.d(TAG, "------------------------projectsEvaluated------------------------")
            }

            @Override
            void buildFinished(BuildResult buildResult) {
                Log.d(TAG, "------------------------buildFinished------------------------")
            }
        })
    }




    /**
     * 添加某个目录下的所有子目录作为子工程
     */
    void includeDir(String dirName) {
        final File file = new File(dirName);
        if (file.exists() && file.isDirectory()) {
            final subs = file.list()
            ArrayList subProj = new ArrayList();
            subs.each {
                final subFile = new File("$dirName/$it")
                if (subFile.isDirectory()) {
                    subProj.add(":$it")
                }
            }

            settings.include( subProj as String[])
            subProj.each {
                settings.project(it).projectDir = new File("$dirName/${it.substring(1)}")
            }
        }
    }


}