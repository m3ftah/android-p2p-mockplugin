package com.inriaspirals.gradle

import org.gradle.api.tasks.TaskAction


class MasterTask extends MockPluginDockerMethods {
    String group = "mockplugin/secondary"
    String description = ""

    @TaskAction
    def master() {
        super.master()
    }
}
