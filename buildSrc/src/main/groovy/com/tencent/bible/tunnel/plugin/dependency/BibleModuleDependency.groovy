package com.tencent.bible.tunnel.plugin.dependency

class BibleModuleDependency {

    String projectName
    //被谁依赖
    HashSet<BibleModuleDependency> owners = new HashSet<>()
    //依赖了谁
    HashSet<BibleModuleDependency> dependencies = new HashSet<>()

}