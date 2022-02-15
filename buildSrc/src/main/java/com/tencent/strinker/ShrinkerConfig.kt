package com.tencent.strinker

/**
 *
 * 配置文件
 *
 */
open  class ShrinkerConfig {

    /**
     * 是否开启inlineR
     */
    var enableShrink = true

    /**
     *
     */
    var shrinkBuildType: MutableList<String> = mutableListOf()


    override fun toString(): String {
        return "ShrinkerConfig(enableShrink=$enableShrink, shrinkBuildType=$shrinkBuildType)"
    }


}