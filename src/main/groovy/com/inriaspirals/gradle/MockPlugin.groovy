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

            //get the sdkDir and then the adb path (couldn't get it automatically for some strange reason)
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


            //creation of the tasks
            project.tasks.create(name: "copyFiles", type: CopyFilesTask)

            project.tasks.create(name: "editFiles", type: EditFilesTask, dependsOn: 'copyFiles')

            project.tasks.create(name: "launchDocker", type: LaunchDockerTask) {

                NB_NODES = project.extensions.androfleet.nodes
                ANDROID_VERSION = project.extensions.androfleet.androidVersion
                ADB_PATH = adbPath

            }

            project.tasks.create(name: "cleanandrofleet", type: CleanAndrofleetTask) {

            }

            project.tasks.create(name: "master", type: MasterTask) {

            }

            project.tasks.create(name: "nodes", type: NodeTask) {

                NB_NODES = project.extensions.androfleet.nodes

            }

            project.tasks.create(name: "servicediscovery", type: ServiceDiscoveryTask) {

            }

            project.tasks.create(name: "cleanMock", type: CleanTask) {

            }

        }
    }
}



