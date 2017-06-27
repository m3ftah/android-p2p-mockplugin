package com.inriaspirals.gradle

import org.gradle.api.DefaultTask


class MockPluginDockerMethods extends DefaultTask {

    Integer NB_NODES
    Integer ANDROID_VERSION
    String ADB_PATH
    String PACKAGE

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


    //execute cleanAndrofleet.py
    def cleanandrofleet() {
        exec("${project.rootDir}/appMock/src/mock_src/cleanAndrofleet.py")
    }


    //execute master.py
    def master() {
        exec("${project.rootDir}/appMock/src/mock_src/master.py ${NB_NODES}")
    }


    //execute node.py
    def node() {
        exec("${project.rootDir}/appMock/src/mock_src/node.py ${NB_NODES} ${PACKAGE}")
    }


    //execute servicediscovery.py
    def servicediscovery() {
        exec("${project.rootDir}/appMock/src/mock_src/servicediscovery.py")
    }

}
