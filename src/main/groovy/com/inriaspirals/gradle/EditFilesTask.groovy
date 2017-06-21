package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class EditFilesTask extends DefaultTask{
    String group = "mockplugin/primary"
    String description = "edit the src files to make them use our mock library"

    @TaskAction
    def editFiles() {

        //Editing of the .java files
        def javaFiles = project.fileTree(dir: "${project.rootDir}/appMock/src", include: '**/*.java')
        javaFiles.each { def javaFile ->

            //replace the package imports
            ant.replace(file: javaFile, token: 'android.net.wifi.p2p', value: 'mock.net.wifi.p2p')

            //replace the creation of the Wifi manager
            javaFile.eachLine { def line ->
                if ((line.indexOf("WifiP2pManager") >= 0)&&(line.indexOf("getSystemService") >= 0)&&(line.indexOf("Context.WIFI_P2P_SERVICE") >=0 )) {
                    def list = line.tokenize('=')

                    ant.replace(file: javaFile, token: line, value: list[0]+'= new WifiP2pManager();')
                }
            }

        }

        //Editing of settings.gradle
        def SetGra = project.file("${project.rootDir}/settings.gradle")
        SetGra.eachLine { def line ->
            if (line.indexOf("include")>=0) {
                if (line.indexOf(",':appMock'") < 0) {
                    ant.replaceregexp(file: SetGra, match: line, replace: line + ",':appMock'")
                }
            }
        }

        //Editing of the appMock's build.gradle
        def BuiGra = project.file("${project.rootDir}/appMock/build.gradle")
        BuiGra.append('\n\ndependencies {\n\tcompile files(\'libs/mock.jar\')\n}')

    }
}