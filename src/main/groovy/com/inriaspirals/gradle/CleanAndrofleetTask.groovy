package com.inriaspirals.gradle

import org.gradle.api.tasks.TaskAction


class CleanAndrofleetTask extends AndrofleetMethods{
    String group = "androfleet/tests"
    String description = "clean the Androfleet environment"

    @TaskAction
    def cleanandrofleet() {

        def tmp_path = new File("${project.rootDir}/tmp_androfleet")
        if (!tmp_path.exists()) {
            tmp_path.mkdir()
        }

        CleanAndrofleetTask.class.getResource( "/cleanAndrofleet.py" ).withInputStream { ris ->
            new File("${project.rootDir}/tmp_androfleet/cleanAndrofleet.py").withOutputStream { fos ->
                fos << ris
            }
        }

        ant.chmod(dir: "${project.rootDir}/tmp_androfleet" , perm:'+x', includes: '**/*.py')

        super.cleanandrofleet()

        ant.delete(dir: "${project.rootDir}/tmp_androfleet")

        println "The mongo DataBase will be deleted ..."
        exec(['docker','rm','-f','reportsDB'])
    }
}
