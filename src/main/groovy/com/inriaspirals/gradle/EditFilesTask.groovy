package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class EditFilesTask extends DefaultTask{
    String group = "mockplugin/primary"
    String description = "edit the src files to make them use our mock library"

    @TaskAction
    def editFiles() {

        //Editing of the .java files
        def javaFiles = project.fileTree(dir: "${project.rootDir}/appMock/src", include: '**/*.java')
        javaFiles.each { def javaFile ->
            ant.replace(file: javaFile, token: 'android.net.wifi.p2p', value: 'mock.net.wifi.p2p')
        }
        println "Import 'android.net.wifi.p2p' changed to 'mock.net.wifi.p2p' in all java files of appMock/"

        //Editing of settings.gradle
        def SetGra = project.file("${project.rootDir}/settings.gradle")
        SetGra.eachLine { def line ->
            if (line.startsWith("include")) {
                if (line.indexOf(",':appMock'") < 0) {
                    ant.replaceregexp(file: SetGra, match: line, replace: line + ",':appMock'", byline: "true")
                }
            }
        }
        println "':appMock' added to settings.gradle"
    }
}


