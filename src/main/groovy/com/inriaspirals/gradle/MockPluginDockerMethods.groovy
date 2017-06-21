package com.inriaspirals.gradle

import org.gradle.api.DefaultTask


class MockPluginDockerMethods extends DefaultTask {

    Integer NB_NODES
    Integer ANDROID_VERSION
    String FEATURES_PATH
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


    //initiate report files and execute node.py
    def node() {
        println PACKAGE

        for (int i=1;i<=NB_NODES;i++) {
            def report_path = new File("${project.rootDir}/appMock/src/mock_src/report_node_${i}.txt")
            if (!report_path.exists()) {
                new File("${project.rootDir}/appMock/src/mock_src/report_node_${i}.txt").createNewFile()
            }
        }

        exec("${project.rootDir}/appMock/src/mock_src/node.py ${NB_NODES}")
    }


    //execute servicediscovery.py
    def servicediscovery() {
        exec("${project.rootDir}/appMock/src/mock_src/servicediscovery.py")
    }

}
