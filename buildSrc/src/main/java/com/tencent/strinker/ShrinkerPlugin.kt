package com.tencent.strinker

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.tencent.strinker.util.logger
import org.gradle.api.Plugin
import org.gradle.api.Project


/**
 * 一个Gradle插件
 */
class ShrinkerPlugin : Plugin<Project> {

    companion object {
    }

    override fun apply(project: Project) {

        // 只能应用在app工程上
        if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
            throw UnsupportedOperationException("Plugin 'shrinker' can only apply with 'com.android.application'")
        }
        logger.lifecycle("-------------- apply ShrinkerPlugin ----------------")

        // 获取Android Extension对象
        val android = project.extensions.getByType(AppExtension::class.java)

        // 添加一个extension对象，这个对象可以在脚本里直接被配置
        // 这里直接返回了extensions添加的对象，这个对象可以在脚本中直接配置
        val config = project.extensions.create("shrinkerConfig", ShrinkerConfig::class.java)

        println("config:$config")

        // 注册转换器
        android.registerTransform(InlineRTransform(config))
    }


}