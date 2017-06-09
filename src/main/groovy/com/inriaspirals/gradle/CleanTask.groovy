package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CleanTask extends DefaultTask{
    String group = "mockplugin/primary"
    String description = "clean the all changes done by the mockplugin"

    @TaskAction
    def cleanAll() {

        def SetGra = project.file("${project.rootDir}/settings.gradle")
        SetGra.eachLine { def line ->
            if (line.indexOf(",':appMock'") > 0) {
                    ant.replaceregexp(file: SetGra, match: line, replace: line - ",':appMock'", byline: "true")
            }
        }

        ant.delete(dir: "${project.rootDir}/appMock")
    }
}