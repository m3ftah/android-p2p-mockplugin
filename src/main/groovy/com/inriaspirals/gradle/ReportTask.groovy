package com.inriaspirals.gradle

import org.gradle.api.tasks.TaskAction

class ReportTask extends MockPluginDockerMethods{
    String group = "mockplugin/primary"
    String description = "print the reports from the nodes"

    @TaskAction
    def report() {

        //print the content of each report text file in the androfleet repository if it is found
        for (int i=1;i<=NB_NODES;i++) {

            def report_path = new File("${project.rootDir}/appMock/src/mock_src/report_node_${i}.txt")

            if (report_path.exists()) {
                String fileContents = new File("${project.rootDir}/appMock/src/mock_src/report_node_${i}.txt").text
                println "*Report for the node n°${i}\n"
                if (fileContents == "") {
                    println "report is empty"
                }
                else {
                    println fileContents
                }
                println "\n----------------------------------------"
            }
            else {
                println "*Report for the node n°${i} not found"
                println "\n----------------------------------------"
            }
        }
    }
}
