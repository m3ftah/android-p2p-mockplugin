package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class BuildTask extends DefaultTask{
    String group = "androfleet/mock"
    String description = "build the project and add the built apk to the androfleet folder"

    String ANDROFLEET_PATH

    @TaskAction
    def buildmock() {

        def mocked_apk_path = new File("${project.rootDir}/appMock/build/outputs/apk/appMock-debug.apk")
        if (mocked_apk_path.exists()) {
            ant.copy(file: "${project.rootDir}/appMock/build/outputs/apk/appMock-debug.apk", tofile: "${ANDROFLEET_PATH}/appMock-debug.apk" )
        }
        else {
            println "\n ERROR: It looks like the 'initMock' task was not executed before. Please execute the 'initMock' task before this one (you only need to do it once, not everytime you want to run 'buildMock')"
        }
    }
}
