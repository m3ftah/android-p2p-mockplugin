package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class LaunchCalabashTask extends AndrofleetMethods{
    String group = "androfleet/tests"
    String description = "launch the calabash scripts on the nodes created with the launchDocker task"

    String ANDROFLEET_PATH
    Integer NB_NODES

    @TaskAction
    def launchcalabash() {

        //copy the python script calabash.py into 'tmp_androfleet'
        def tmp_path = new File("${project.rootDir}/tmp_androfleet")
        if (!tmp_path.exists()) {
            tmp_path.mkdir()
        }
        LaunchCalabashTask.class.getResource( "/calabashRun.py" ).withInputStream { ris ->
            new File("${project.rootDir}/tmp_androfleet/calabashRun.py").withOutputStream { fos ->
                fos << ris
            }
        }
        ant.chmod(dir: "${project.rootDir}/tmp_androfleet" , perm:'+x', includes: '**/*.py')

        //Create the 'results' repository
        def apk_path = new File("${ANDROFLEET_PATH}/appMock-debug.apk")
        def results_path = new File("${ANDROFLEET_PATH}/results")
        if (!results_path.exists()) {
            results_path.mkdir()
        }


        if (apk_path.exists()) {
            for (int i=1;i<=NB_NODES;i++) {
                "${project.rootDir}/tmp_androfleet/calabashRun.py ${i} ${ANDROFLEET_PATH}".execute()
            }
        }
        else {
            println "\n ERROR: It looks like the 'buildMock' task was not executed before. Please execute the 'buildMock' task before this one (you only need to do it once, not everytime you want to run 'launchCalabash')"
        }

    }
}
