package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CopyFilesTask extends DefaultTask{
    String group = "mockplugin"
    String description = "copy of files"


    @TaskAction
    def copyFiles() {

        //Copy of the 'app/' repository to an 'appMock/' repository
        ant.copy(todir: "${project.rootDir}/appMock") {
            fileset(dir: "${project.rootDir}/app", excludes: "**/build/")
        }
        println "repository app/ copied into appMock/"

        //Copy of 'mock.jar' in the repository 'appMock/libs/'
        CopyFilesTask.class.getResource( '/mock.jar' ).withInputStream { ris ->
            new File( "${project.rootDir}/appMock/libs/mock.jar" ).withOutputStream { fos ->
                fos << ris
            }
        }
        println "'mock.jar' library added to 'appMock/libs/'"

    }
}
