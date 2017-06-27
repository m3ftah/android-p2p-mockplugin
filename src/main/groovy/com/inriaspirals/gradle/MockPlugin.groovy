package com.inriaspirals.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import static groovy.io.FileType.FILES


class MockPlugin implements Plugin<Project> {

    String adbPath

    @Override
    void apply(Project project) {

        project.extensions.create("androfleet", MockPluginExtensions)

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
            project.tasks.create(name: "copyFiles", type: CopyFilesTask) {}

            project.tasks.create(name: "initMock", type: EditFilesTask, dependsOn: 'copyFiles') {}

            project.tasks.create(name: "cleanMock", type: CleanTask) {}

            project.tasks.create(name: "buildMock", type: BuildTask, dependsOn: project.getTasksByName('build', true)) {

                ANDROFLEET_PATH = project.extensions.androfleet.androfleetPath

            }

            project.tasks.create(name: "launchDocker", type: LaunchDockerTask) {

                NB_NODES = project.extensions.androfleet.nodes
                ANDROID_VERSION = project.extensions.androfleet.androidVersion
                ADB_PATH = adbPath
                PACKAGE = package_name

            }

            project.tasks.create(name: "cleanandrofleet", type: CleanAndrofleetTask) {}

            project.tasks.create(name: "master", type: MasterTask) {}

            project.tasks.create(name: "nodes", type: NodeTask) {

                NB_NODES = project.extensions.androfleet.nodes
                PACKAGE = package_name

            }

            project.tasks.create(name: "servicediscovery", type: ServiceDiscoveryTask) {}

            project.tasks.create(name: "reportNodes", type: ReportTask) {

                ANDROFLEET_PATH = project.extensions.androfleet.androfleetPath
                NB_NODES = project.extensions.androfleet.nodes

            }

            project.tasks.create(name: "launchCalabash", type: CalabashTask) {

                ANDROFLEET_PATH = project.extensions.androfleet.androfleetPath
                NB_NODES = project.extensions.androfleet.nodes

            }

        }
    }
}



