package com.inriaspirals.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import static groovy.io.FileType.FILES


class AndrofleetPlugin implements Plugin<Project> {

    String adbPath

    @Override
    void apply(Project project) {

        project.extensions.create("androfleet", AndrofleetExtensions)

        project.afterEvaluate {

            //get the sdkDir and then the adb path
            def rootDir = project.rootDir
            def localProperties = new File(rootDir, "local.properties") //suppose that the file exists
            if (localProperties.exists()) {
                Properties properties = new Properties()
                localProperties.withInputStream { instr ->
                    properties.load(instr)
                }
                def sdkDir = properties.getProperty('sdk.dir')
                new File(sdkDir).eachFileRecurse(FILES) {
                    if (it.name == 'adb') {
                        adbPath = it
                    }
                }
            }
            if(adbPath == null) {println "adb path not found"}

            //get the package name of the app
            def package_name = new XmlSlurper().parse("${project.rootDir}/app/src/main/AndroidManifest.xml").@package
            if (package_name == null) {println "package name of the app not found"}


            //creation of the tasks

            project.tasks.create(name: "initMock", type: InitFilesTask) {}

            project.tasks.create(name: "cleanMock", type: CleanTask) {}

            project.tasks.create(name: "buildMock", type: BuildTask, dependsOn: project.getTasksByName('assembleDebug', true)) {

                ANDROFLEET_PATH = project.extensions.androfleet.androfleetPath

            }

            project.tasks.create(name: "launchEmulators", type: LaunchAndrofleetTask) {

                NB_NODES = project.extensions.androfleet.nodes
                ANDROID_VERSION = project.extensions.androfleet.androidVersion
                ADB_PATH = adbPath
                PACKAGE = package_name
                DATA_EXCHANGE_PORT = project.extensions.androfleet.dataExchangePort

            }

            project.tasks.create(name: "cleanAndrofleet", type: CleanAndrofleetTask) {}

            project.tasks.create(name: "launchTests", type: LaunchCalabashTask) {

                ANDROFLEET_PATH = project.extensions.androfleet.androfleetPath
                NB_NODES = project.extensions.androfleet.nodes

            }

            project.tasks.create(name: "printTestReport", type: ReportTask) {

                ANDROFLEET_PATH = project.extensions.androfleet.androfleetPath
                NB_NODES = project.extensions.androfleet.nodes

            }

            project.tasks.create(name: "connectMongo", type: MongoDBTask) {
                ANDROFLEET_PATH = project.extensions.androfleet.androfleetPath
                NB_NODES = project.extensions.androfleet.nodes
            }
        }
    }
}



