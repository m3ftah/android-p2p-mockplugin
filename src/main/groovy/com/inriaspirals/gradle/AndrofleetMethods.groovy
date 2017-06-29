package com.inriaspirals.gradle

import org.gradle.api.DefaultTask


class AndrofleetMethods extends DefaultTask {

    Integer NB_NODES
    Integer ANDROID_VERSION
    String ADB_PATH
    String PACKAGE
    String DATA_EXCHANGE_PORT

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
        exec("${project.rootDir}/tmp_androfleet/cleanAndrofleet.py")
    }


    //execute master.py
    def master() {
        exec("${project.rootDir}/tmp_androfleet/master.py ${NB_NODES}")
    }


    //execute node.py
    def node() {
        exec("${project.rootDir}/tmp_androfleet/node.py ${NB_NODES} ${PACKAGE} ${DATA_EXCHANGE_PORT}")
    }


    //execute servicediscovery.py
    def servicediscovery() {
        exec("${project.rootDir}/tmp_androfleet/servicediscovery.py")
    }

}
