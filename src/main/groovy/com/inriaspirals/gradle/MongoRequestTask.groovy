package com.inriaspirals.gradle

import org.gradle.api.tasks.TaskAction

class MongoRequestTask extends AndrofleetMethods {
    String group = "androfleet/tests"

    String DISPLAY_TYPE

    @TaskAction
    def request() {

        //copy the script queryMongoFeatures.js into /tmp_androfleet
        def tmp_path = new File("${project.rootDir}/tmp_androfleet")
        if (!tmp_path.exists()) {
            tmp_path.mkdir()
        }
        MongoRequestTask.class.getResource( "/queryMongoFeatures.js" ).withInputStream { ris ->
            new File("${project.rootDir}/tmp_androfleet/queryMongoFeatures.js").withOutputStream { fos ->
                fos << ris
            }
        }
        ant.chmod(dir: "${project.rootDir}/tmp_androfleet" , perm:'+x', includes: '**/*.py')


        def process = ['docker','exec','reportsDB','mongo','test','--eval',"var display_choice = \'${DISPLAY_TYPE}\'",'/tmp_androfleet/queryMongoFeatures.js']
        exec(process)
    }
}
