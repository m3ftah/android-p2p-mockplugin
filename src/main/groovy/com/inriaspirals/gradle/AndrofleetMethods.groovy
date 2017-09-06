package com.inriaspirals.gradle

import org.gradle.api.DefaultTask


class AndrofleetMethods extends DefaultTask {

    Integer NB_NODES
    Integer ANDROID_VERSION
    String ADB_PATH
    String PACKAGE
    String DATA_EXCHANGE_PORT
    String ANDROFLEET_PATH

    //'redefine' execute() in order to print the outputs
    StringBuffer execout = new StringBuffer() //Standard output of a command
    StringBuffer execerr = new StringBuffer() //Error output of a command
    def exec(List<String> command) {
        command.execute().waitForProcessOutput(execout, execerr)
        if( execout.size() > 0 ) println execout
        if( execerr.size() > 0 ) println execerr
        if( execout.size() > 0 ) execout.setLength(0)
        if( execerr.size() > 0 ) execerr.setLength(0)
    }
}
