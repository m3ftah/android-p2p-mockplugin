package com.inriaspirals.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class MockPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create("androfleet", MockPluginExtensions)

        project.afterEvaluate {

            project.tasks.create(name: "copyFiles", type: CopyFilesTask)

            project.tasks.create(name: "editFiles", type: EditFilesTask, dependsOn: 'copyFiles')

            project.tasks.create(name: "launchDocker", type: LaunchDockerTask) {

                NB_NODES = project.extensions.androfleet.nodes
                ANDROID_VERSION = project.extensions.androfleet.androidVersion

            }
        }
    }
}



