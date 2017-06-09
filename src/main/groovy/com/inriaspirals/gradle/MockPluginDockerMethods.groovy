package com.inriaspirals.gradle

import org.gradle.api.DefaultTask


class MockPluginDockerMethods extends DefaultTask {

    Integer NB_NODES


    //'redefine' execute() in order to print the outputs
    StringBuffer exout = new StringBuffer() //Standard output of a command
    StringBuffer exerr = new StringBuffer() //Error output of a command

    def exec(String command) {
        command.execute().consumeProcessOutput(exout, exerr)
        command.execute().waitForProcessOutput()
        if( exout.size() > 0 ) println exout
        if( exerr.size() > 0 ) println exerr
        if( exout.size() > 0 ) exout.setLength(0)
        if( exerr.size() > 0 ) exerr.setLength(0)
    }

    def cleanandrofleet() {
        exec("${project.rootDir}/appMock/src/mock_res/cleanAndrofleet.py")
    }

    def master() {
        exec("${project.rootDir}/appMock/src/mock_res/master.py")
    }

    def node() {
        exec("${project.rootDir}/appMock/src/mock_res/node.py")
    }

    def servicediscovery() {
        exec("${project.rootDir}/appMock/src/mock_res/servicediscovery.py")
    }

}
