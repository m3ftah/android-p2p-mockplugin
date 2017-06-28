package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CalabashTask extends DefaultTask{
    String group = "mockplugin/primary"
    String description = "launch the calabash scripts on the nodes created with the launchDocker task"

    String ANDROFLEET_PATH
    Integer NB_NODES

    @TaskAction
    def launchcalabash() {

        def apk_path = new File("${ANDROFLEET_PATH}/appMock-debug.apk")
        def results_path = new File("${ANDROFLEET_PATH}/results")
        if (!results_path.exists()) {
            results_path.mkdir()
        }


        if (apk_path.exists()) {
            for (int i=1;i<=NB_NODES;i++) {
                "calabash-android run ${ANDROFLEET_PATH}/appMock-debug.apk ADB_DEVICE_ARG=192.168.49.${i} -f json -o ${ANDROFLEET_PATH}/results/node${i}.json -f html -o ${ANDROFLEET_PATH}/result/node${i}.html &".execute()
            }
        }
        else {
            println "\n ERROR: It looks like the 'buildMock' task was not executed before. Please execute the 'buildMock' task before this one (you only need to do it once, not everytime you want to run 'launchCalabash')"
        }
    }
}
