package com.inriaspirals.gradle

import org.gradle.api.tasks.TaskAction

class MongoDBTask extends AndrofleetMethods {
    String group = "androfleet/tests"

    String ANDROFLEET_PATH

    @TaskAction
    def connect() {

        println "remove reportsDB"
        exec(['docker','rm','-f','reportsDB'])

        def tmp_path = new File("${project.rootDir}/tmp_androfleet")
        if (!tmp_path.exists()) {
            tmp_path.mkdir()
        }

        println "create the DB in the container 'reportsDB'"
        def command = ['docker','run',
                       '--name','reportsDB',
                       '-v',"${ANDROFLEET_PATH}/results/:/results",
                       '-v',"${project.rootDir}/tmp_androfleet/:/tmp_androfleet",
                       '-p','27017:27017',
                       '-d','mongo']
        exec(command)

        println "import the json files"
        for (int i = 0; i < NB_NODES; i++) {

            def report_json_path = new File("${ANDROFLEET_PATH}/results/node${i}.json")

            if (report_json_path.exists()) {
                println report_json_path
                command = ['docker','exec','reportsDB','mongoimport',
                           '--port','27017',
                           '--db','test',
                           '--collection','features',
                           '--jsonArray',
                           '--file',"/results/node${i}.json"]
                exec(command)
            }
            else {
                println "node${i}.json doesn't exist"
            }
        }
    }
}
