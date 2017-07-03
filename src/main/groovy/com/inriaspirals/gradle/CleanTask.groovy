package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CleanTask extends DefaultTask{
    String group = "androfleet/mock"
    String description = "clean the all changes done by the mockplugin"

    @TaskAction
    def cleanAll() {

        //delete changes done to the settings.gradle
        def SetGra = project.file("${project.rootDir}/settings.gradle")
        SetGra.eachLine { def line ->
            if ((line.indexOf("include")>=0) && (line.indexOf("//")>=0)) {
                ant.replaceregexp(file: SetGra, match: line, replace: line-"//")
            }
            if ((line.indexOf("include")>=0) && (line.indexOf("\':appMock\'")>=0)) {
                ant.replaceregexp(file: SetGra, match: line, replace: "")
            }
        }

        //delete all the appMock repository
        ant.delete(dir: "${project.rootDir}/appMock")
    }
}
