package com.inriaspirals.gradle

import org.gradle.api.tasks.TaskAction


class LaunchDockerTask extends MockPluginDockerMethods {
    String group = "mockplugin/primary"
    String description = "init and launch Docker and Androfleet"

    Integer NB_NODES
    Integer ANDROID_VERSION
    String ADB_PATH

    @TaskAction
    def launch() {
        def HOME = System.getProperty("user.home")

        println 'NB_NODES= '+NB_NODES
        println 'ANDROID_VERSION= '+ANDROID_VERSION
        println 'ADB_PATH= '+ADB_PATH+'\n'


        println '*Launching docker'
        exec("pwd")

        /*println '*Cleaning...'
        exec("${HOME}/Documents/androfleet/docker/cleanAndrofleet.py") //this is an absolute path, need to find generic method

        println '*Launching Weave'
        exec("weave launch")

        println '*Exposing Weave'
        exec("weave expose")

        println '*Launching adb'
        exec("${ADB_PATH} devices")

        println '*Redirecting adb port to weave'
        exec("redir --cport 5037 --caddr 127.0.0.1 --lport 5037 --laddr 192.168.49.1 &")

        println '*Launching Master'
        exec("${HOME}/Documents/androfleet/docker/master.py ${NB_NODES}")

        println '*Launching Service Discovery'
        exec("${HOME}/Documents/androfleet/docker/servicediscovery.py")

        println '*Launching Nodes'
        exec("${HOME}/Documents/androfleet/docker/node.py ${NB_NODES}")

        println '*androfleet-master log:'
        exec("docker logs -f androfleet-master")*/

    }
}
