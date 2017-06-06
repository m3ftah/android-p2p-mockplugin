package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class LaunchDockerTask extends DefaultTask {
    String group = "mockplugin"
    String description = "init and launch Docker and Androfleet"

    int NB_NODES
    int ANDROID_VERSION

    StringBuffer exout = new StringBuffer() //Standard output of a command
    StringBuffer exerr = new StringBuffer() //Error output of a command
    def exec(String command) {  //'redefine' execute() in order to print the outputs
        command.execute().consumeProcessOutput(exout, exerr)
        command.execute().waitForProcessOutput()
        if( exout.size() > 0 ) println exout
        if( exerr.size() > 0 ) println exerr
        if( exout.size() > 0 ) exout.setLength(0)
        if( exerr.size() > 0 ) exerr.setLength(0)
    }

    @TaskAction
    def launch() {
        def HOME = System.getProperty("user.home")

        println 'NB_NODES= '+NB_NODES
        println 'ANDROID_VERSION= '+ANDROID_VERSION

        println '*Launching docker'
        exec("pwd")

        println '*Cleaning...'
        exec("${HOME}/Documents/androfleet/docker/cleanAndrofleet.py") //this is an absolute path, need to find generic method

        println '*Launching Weave'
        exec("weave launch")

        println '*Exposing Weave'
        exec("weave expose")

        println '*Launching adb'
        exec("${HOME}/Android/Sdk/platform-tools/adb devices")

        println '*Redirecting adb port to weave'
        exec("redir --cport 5037 --caddr 127.0.0.1 --lport 5037 --laddr 10.32.0.2 &")

        println '*Launching Master'
        exec("${HOME}/Documents/androfleet/docker/master.py ${NB_NODES}")

        sleep(2000)
        println '*Launching Service Discovery'
        exec("${HOME}/Documents/androfleet/docker/servicediscovery.py")

        println '*Launching Nodes'
        exec("${HOME}/Documents/androfleet/docker/node.py ${NB_NODES}")

        println '*androfleet-master log:'
        exec("docker logs -f androfleet-master")
    }
}
