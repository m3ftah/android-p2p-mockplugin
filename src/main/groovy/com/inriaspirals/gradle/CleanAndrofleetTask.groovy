package com.inriaspirals.gradle

import org.gradle.api.tasks.TaskAction


class CleanAndrofleetTask extends MockPluginDockerMethods{
    String group = "mockplugin/secondary"
    String description = "clean the Androfleet environment"

    @TaskAction
    def cleanandrofleet() {
        super.cleanandrofleet()
    }
}
