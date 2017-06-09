package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CopyFilesTask extends DefaultTask{
    String group = "mockplugin/secondary"
    String description = "copy the content of app/ into appMock/"


    @TaskAction
    def copyFiles() {

        //Copy of the 'app/' repository to an 'appMock/' repository
        println "copy of app/ to appMock/"
        ant.copy(todir: "${project.rootDir}/appMock") {
            fileset(dir: "${project.rootDir}/app", excludes: "**/build/")
        }

        //Copy of 'mock.jar' in the repository 'appMock/libs/'
        CopyFilesTask.class.getResource( '/mock.jar' ).withInputStream { ris ->
            println "copy of mock.jar to appMock/libs/"
            new File( "${project.rootDir}/appMock/libs/mock.jar" ).withOutputStream { fos ->
                fos << ris
            }
        }

        //Copy the python scripts into 'appMock/src/mock_res
        def pythonFiles = ["cleanAndrofleet.py","master.py","node.py","servicediscovery.py"]
        new File("${project.rootDir}/appMock/src/mock_res").mkdir()
        pythonFiles.each {
            println "copy of ${it} to appMock/src/mock_res/"
            CopyFilesTask.class.getResource( "/${it}" ).withInputStream { ris ->
                new File("${project.rootDir}/appMock/src/mock_res/${it}").withOutputStream { fos ->
                    fos << ris
                }
            }
        }
    }
}
