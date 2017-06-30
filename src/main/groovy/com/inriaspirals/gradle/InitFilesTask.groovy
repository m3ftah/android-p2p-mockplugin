package com.inriaspirals.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class InitFilesTask extends DefaultTask{
    String group = "androfleet/mock"
    String description = "edit the src files to make them use our mock library"

    @TaskAction
    def editFiles() {

        //Copy of the 'app/' repository to an 'appMock/' repository
        ant.copy(todir: "${project.rootDir}/appMock") {
            fileset(dir: "${project.rootDir}/app", excludes: "**/build/")
        }

        //Copy of 'widi.aar' in the repository 'appMock/libs/'
        def libs_path = new File("${project.rootDir}/appMock/libs")
        if (!libs_path.exists()) {
            new File("${project.rootDir}/appMock/libs").mkdir()
        }
        InitFilesTask.class.getResource( '/widi.aar' ).withInputStream { ris ->
            new File( "${project.rootDir}/appMock/libs/widi.aar" ).withOutputStream { fos ->
                fos << ris
            }
        }

        //Editing of the .java files
        def javaFiles = project.fileTree(dir: "${project.rootDir}/appMock/src", include: '**/*.java')
        javaFiles.each { def javaFile ->

            //replace the packages imports
            ant.replace(file: javaFile, token: 'android.net.wifi.p2p', value: 'mock.net.wifi.p2p')

            ant.replace(file: javaFile, token: 'android.net.NetworkInfo', value: 'mock.net.NetworkInfo')

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
        BuiGra.append('\n\ndependencies {\n' +
                '   //compile project(path: \':widi\')\n' +
                '   compile \'com.google.code.gson:gson:2.7\'\n' +
                '   compile \'org.greenrobot:eventbus:3.0.0\'\n' +
                '   compile(name:\'widi\', ext:\'aar\')\n' +
                '}\n' +
                'repositories{\n' +
                '   flatDir{\n' +
                '       dirs \'libs\'\n' +
                '   }\n' +
                '}')

    }
}