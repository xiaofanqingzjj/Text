package com.test_plugins;

import org.gradle.api.Plugin
import org.gradle.api.Project

class StandAlonePlugin implements Plugin<Project> {


    void apply(Project project) {
        note()


        //create an extension object:Whyn,so others can config via Whyn
        project.extensions.create("whyn", YNExtension)


        project.task('whyn'){
            group = "test"
            description = "gradle Standalone project demo,shares everywhere"
            doLast{
                println '**************************************'
                println "$project.whyn.description"
                println '**************************************'
            }

        }
    }

    private void note(){
        println '------------------------'
        println 'apply StandAlonePlugin'
        println '------------------------'
    }
}

class YNExtension {
    String description = 'default description'
}