package com.inriaspirals.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project


class MockPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {

        project.tasks.create(name: "copyFiles", type: CopyFilesTask)
        project.tasks.create(name: "editFiles", type: EditFilesTask, dependsOn: 'copyFiles')

    }
}



