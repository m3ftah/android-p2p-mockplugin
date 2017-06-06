package com.inriaspirals.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project


class MockPlugin implements Plugin<Project> {

    int tmp_nb_nodes //variable to pass the nb of nodes from 'readArg' to 'launchDocker'
    int tmp_android_version

    @Override
    void apply(Project project) {

        project.tasks.create(name: "copyFiles", type: CopyFilesTask)

        project.tasks.create(name: "editFiles", type: EditFilesTask, dependsOn: 'copyFiles')

        def getArg = project.tasks.create(name: "readArg", type: ReadArgTask) << {
            tmp_nb_nodes = nodes
            tmp_android_version = androidVersion
        }

        project.tasks.create(name: "launchDocker", type: LaunchDockerTask) {
            getArg.execute()
            NB_NODES = tmp_nb_nodes
            ANDROID_VERSION = tmp_android_version
        }
    }
}



