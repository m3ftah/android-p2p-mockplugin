package com.inriaspirals.gradle

import org.gradle.api.tasks.TaskAction


class ServiceDiscoveryTask extends MockPluginDockerMethods {
    String group = "mockplugin/secondary"
    String description = ""

    @TaskAction
    def servicediscovery() {
        super.servicediscovery()
    }
}
