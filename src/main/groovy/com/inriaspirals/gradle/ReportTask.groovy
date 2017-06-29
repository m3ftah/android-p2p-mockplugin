package com.inriaspirals.gradle

import org.gradle.api.tasks.TaskAction


class ReportTask extends AndrofleetMethods{
    String group = "androfleet/tests"
    String description = "print the calabash reports for each node"

    String ANDROFLEET_PATH

    @TaskAction
    def report() {

        //print the content of each report text file in the androfleet repository if it is found
        for (int i=1;i<=NB_NODES;i++) {

            def report_path = new File("${ANDROFLEET_PATH}/results/node${i}.json")

            if (report_path.exists()) {
                String fileContents = report_path.text
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
