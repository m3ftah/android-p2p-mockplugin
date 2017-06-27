package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class BuildTask extends DefaultTask{
    String group = "mockplugin/primary"
    String description = "build the project and add the built apk to the androfleet folder"

    String ANDROFLEET_PATH

    @TaskAction
    def buildmock() {

        ant.copy(file: "${project.rootDir}/appMock/build/outputs/apk/appMock-debug.apk", tofile: "${ANDROFLEET_PATH}/appMock-debug.apk" )

    }
}
