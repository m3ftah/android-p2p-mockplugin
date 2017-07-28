package com.inriaspirals.gradle

import org.gradle.api.tasks.TaskAction

class MongoRequestTask extends AndrofleetMethods {
    String group = "androfleet/tests"

    String DISPLAY_TYPE
    String SORT_TYPE

    @TaskAction
    def request() {


        //check if the reportsDB container is running
        StringBuffer exout = new StringBuffer()
        StringBuffer exerr = new StringBuffer()
        ['docker','inspect','-f','\'{{.State.Running}}\'','reportsDB'].execute().waitForProcessOutput(exout,exerr)


        if( exout.toString().indexOf('true') == 1) {//avoid any special character

            //copy the script queryMongoFeatures.js into /tmp_androfleet, repository created at the creation of the DB
            MongoRequestTask.class.getResource( "/queryMongoFeatures.js" ).withInputStream { ris ->
                new File("${project.rootDir}/tmp_androfleet/queryMongoFeatures.js").withOutputStream { fos ->
                    fos << ris
                }
            }
            ant.chmod(dir: "${project.rootDir}/tmp_androfleet" , perm:'+x', includes: '**/*.js')


            def process = ['docker','exec','reportsDB','mongo','test','--eval',"var sort_type = \'${SORT_TYPE}\' , display_choice = \'${DISPLAY_TYPE}\'",'/tmp_androfleet/queryMongoFeatures.js']
            exec(process)
        }
        else {
            println '\nThe mongo DataBase is not running, please run the \'launchMongo\' task'
        }
    }
}
