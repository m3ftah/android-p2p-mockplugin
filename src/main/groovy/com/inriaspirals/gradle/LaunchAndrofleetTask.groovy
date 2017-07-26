package com.inriaspirals.gradle

import groovy.io.FileType
import org.gradle.api.tasks.TaskAction


class LaunchAndrofleetTask extends AndrofleetMethods {
    String group = "androfleet/tests"
    String description = "init and launch Androfleet"


    @TaskAction
    def launch() {

        //Copy the python scripts into 'tmp'
        def tmp_path = new File("${project.rootDir}/tmp_androfleet")
        if (!tmp_path.exists()) {
            tmp_path.mkdir()
        }

        def pythonFiles = ["cleanAndrofleet.py","experiment.py","master.py","node.py","servicediscovery.py"]
        pythonFiles.each {
            println "copy of ${it} to tmp_androfleet/"
            LaunchAndrofleetTask.class.getResource( "/${it}" ).withInputStream { ris ->
                new File("${project.rootDir}/tmp_androfleet/${it}").withOutputStream { fos ->
                    fos << ris
                }
            }
        }

        //Unzip docker.zip inside 'appMock/src/mock_res
        LaunchAndrofleetTask.class.getResource( '/docker.zip' ).withInputStream { ris ->
            new File( "${project.rootDir}/tmp_androfleet/docker.zip" ).withOutputStream { fos ->
                fos << ris
            }
        }
        ant.unzip(src: "${project.rootDir}/tmp_androfleet/docker.zip", dest:"${project.rootDir}/tmp_androfleet", overwrite:"false" )
        ant.delete(file: "${project.rootDir}/tmp_androfleet/docker.zip")

        //Give execution permissions to all the files in appMock/src/mock_src/
        def list = []
        def dir = new File("${project.rootDir}/tmp_androfleet")
        dir.eachFileRecurse (FileType.FILES) { file ->
            list << file
        }
        list.each {
            ant.chmod(file: it , perm:'+x')
        }

        //Launch experiment.py
        super.experiment()

    }
}
