
package com.my.plugins

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings


/**
 * 给Settings对象增加一些扩展的使用方法
 */
class SettingsExtPlugin implements Plugin<Settings> {

    Settings settings;



    @Override
    void apply(Settings target) {
        this.settings = target

        // 给Settings增加
//        target.extensions.extraProperties["includeDir"] = this.&includeDir
        target.ext.includeDir =  this.&includeDir
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


    void foo() {
        int a = 1
        def b = 2

        def c = [1, 2, 3, ];
        c.forEach( {
            println("$it")
        })

        foo2({
            print("sss:$it");
        })

        foo3 {

        }

        config {

        }
    }

    void foo2(def ss) {
        ss.call(");")
    }

    void foo3(Closure c) {
        c.call(1, 3, 4)
    }

    void config(Closure c) {
        c.call(this)
    }
}