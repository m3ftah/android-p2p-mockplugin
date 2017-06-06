package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ReadArgTask extends DefaultTask {
    String group = "mockplugin"
    String description = "read androfleet arguments from the build.gradle"

    int nodes = -1
    int androidVersion = -1

    @TaskAction
    def readArg() {
        def minSdkVer = false
        def tarSdkVer = false
        def nod = false
        def andVer = false

        def BuiGra = project.file("${project.rootDir}/app/build.gradle")
        BuiGra.eachLine { def line ->

            def mots = line.tokenize(' ;{}')
            mots.each { def mot ->

                //nodes
                if(nod==true) {nodes = mot.isInteger() ? mot.toInteger() : null}
                nod = mot.equalsIgnoreCase('nodes')

                //androidVersion
                if(andVer==true) {androidVersion = mot.isInteger() ? mot.toInteger() : null}
                andVer = mot.equalsIgnoreCase('androidVersion')

            }
        }

        println 'nodes= '+nodes
        println 'androidVersion= '+androidVersion
    }
}
