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

            def report_json_path = new File("${ANDROFLEET_PATH}/results/node${i}.json")
            def report_html_path = new File("${ANDROFLEET_PATH}/results/node${i-1}.html")

            println report_json_path
            println report_html_path

            if (report_json_path.exists()) {
                String fileContents = report_json_path.text
                println "*Report for the node n°${i}\n"
                if (fileContents == "") {
                    println "report is empty\n"
                }
                else {
                    println fileContents + '\n'
                }
            }
            else {
                println "*Report for the node n°${i} not found"
            }

            if (report_html_path.exists()) {
                println "Path to the html report (copy it in your web browser):\nfile://"+report_html_path
            }
            else {
                println "Html report for the node n°${i} not found"
            }
            println "\n----------------------------------------"
        }
    }
}
