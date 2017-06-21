package com.inriaspirals.gradle

import org.gradle.api.tasks.TaskAction


class LaunchDockerTask extends MockPluginDockerMethods {
    String group = "mockplugin/primary"
    String description = "init and launch Docker and Androfleet"


    @TaskAction
    def launch() {
        def HOME = System.getProperty("user.home")

        println 'NB_NODES= '+NB_NODES
        println 'ANDROID_VERSION= '+ANDROID_VERSION
        println 'FEATURES_PATH= '+FEATURES_PATH
        println 'ADB_PATH= '+ADB_PATH+'\n'


        println '*Launching docker'
        exec("pwd")

        println '*Cleaning...'
        cleanandrofleet()

        println '*Launching Weave'
        exec("weave launch --ipalloc-range 192.168.48.0/23")

        println '*Exposing Weave'
        exec("weave expose")

        println '*Exposing xhost'
        exec("xhost +")

        println '*Launching adb'
        exec("${ADB_PATH} devices")

        println '*Redirecting adb port to weave'
        exec("redir --cport 5037 --caddr 127.0.0.1 --lport 5037 --laddr 192.168.48.1 &")

        println '*Launching Master'
        master()

        println '*Launching Service Discovery'
        servicediscovery()

        println '*Launching Nodes'
        node()

        println '*androfleet-master log:'
        exec("docker logs -f androfleet-master")

    }
}
