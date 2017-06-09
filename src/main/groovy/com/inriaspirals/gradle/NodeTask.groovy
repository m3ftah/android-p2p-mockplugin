package com.inriaspirals.gradle

import org.gradle.api.tasks.TaskAction


class NodeTask extends MockPluginDockerMethods {
    String group = "mockplugin/secondary"
    String description = "create nodes for docker"

    @TaskAction
    def node() {
        super.node()
    }
}
