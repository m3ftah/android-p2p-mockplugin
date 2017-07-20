package com.inriaspirals.gradle

import groovy.json.JsonSlurper
import org.gradle.api.tasks.TaskAction


class ReportTask extends AndrofleetMethods{
    String group = "androfleet/tests"
    String description = "print the calabash reports for each node"

    String ANDROFLEET_PATH

    @TaskAction
    def report() {

        def Errors = []
        def error = new ArrayList(5)
            //0 node: number of the node concerned
            //1 feature: name of the feature concerned
            //2 scenario: name of the scenario which failed
            //3 line: the line number of the failed step
            //4 message: error message returned

        def Features = []
            //0 name: the feature's name
            //1 nodes: number of nodes concerned by the feature
            //2 scenarios: number of scenarios for the feature
            //3 fails: number of failed scenarios for the feature

        def Scenarios = []
            //0 name: the scenario's name

        def Nodes = []
            //0 number: number of the node
            //1 scenarios: number of scenarios for the node
            //2 fails: number of failed scenarios for the node

        def tot_nrb_sce = 0 //total number of scenarios
        def tot_nbr_sce_failed = 0 //total number of scenarios failed

        for (int i=0;i<NB_NODES;i++) {

            def report_json_path = new File("${ANDROFLEET_PATH}/results/node${i}.json")

            if (report_json_path.exists()) {

                def slurper = new JsonSlurper()
                def json = slurper.parseText(report_json_path.text)

                error[0] = i
                Nodes.add([i,0,0])

                for (int k=0;k<json.size();k++) { //for each Feature
                    error[1] = json[k].name


                    def new_feat = 1 //if = 1 it is the first time the feature is encountered
                    //find if the feature is already in Features[], add it to Features[] if not
                    for(int m=0;m<Features.size();m++) {
                        if (Features[m].contains(json[k].name)) {
                            new_feat= 0
                            Features[m][1]++ //suppose that a feature can only appear once in a json file
                        }
                    }
                    if (new_feat == 1) {
                        Features.add([json[k].name,1,0,0])
                    }


                    json[k].elements.each { //for each scenario
                        tot_nrb_sce++
                        def sce_ok = 1 //if = 0, the scenario has failed
                        error[2] = it.name
                        Nodes[i][1]++

                        //increase the number of scenarios concerning the current feature in Features[]
                        for(int m=0;m<Features.size();m++) {
                            if (Features[m].contains(json[k].name)) {Features[m][2]++}
                        }


                        def new_sce = 1 //if = 1 it is the first time the scenario is encountered
                        //find if the scenario is already in Scenarios[], add it to Scenarios[] if not
                        for(int m=0;m<Scenarios.size();m++) {
                            if (Scenarios[m].contains(it.name)) {
                                new_sce= 0
                            }
                        }
                        if (new_sce == 1) {
                            Scenarios.add(it.name)
                        }


                        it.steps.each { //for each step
                            error[3]=it.line

                            if (it.result.status == "skipped") {
                            }

                            //only if the step failed
                            if (it.result.status == "failed") {
                                sce_ok = 0
                                error[4] = it.result.error_message
                                Nodes[i][2]++

                                Errors.add(error)
                                error = error.clone()
                            }
                        }

                        if (sce_ok == 0) {
                            tot_nbr_sce_failed++ //increase the total of failed scenarios
                            //increase the number of failed scenarios concerning the current feature in Features[]
                            for(int m=0;m<Features.size();m++) {
                                if (Features[m].contains(json[k].name)) {Features[m][3]++}
                            }
                        }
                    }
                }
            }
            else {
                println "*Report for the node n°${i} not found"
            }
        }

        println "---------------------------------------------\n" +
                "${tot_nrb_sce} scenarios in total, " +
                "${tot_nbr_sce_failed} failed (${(tot_nbr_sce_failed*100F/tot_nrb_sce).round()}%), " +
                "${tot_nrb_sce-tot_nbr_sce_failed} passed (${((tot_nrb_sce-tot_nbr_sce_failed)*100F/tot_nrb_sce).round()}%)\n" +
                "---------------------------------------------"

        print "${Features.size()} features, "
        int nbr_feat_failed = 0
        for(int k=0;k<Features.size();k++) {
            if (Features[k][3] > 0) {
                nbr_feat_failed++
            }
        }
        println "${nbr_feat_failed} failed (${(nbr_feat_failed*100F/Features.size()).round()}%), " +
                "${Features.size()-nbr_feat_failed} passed (${((Features.size()-nbr_feat_failed)*100F/Features.size()).round()}%):"

        for(int k=0;k<Features.size();k++) {
            if (Features[k][3] > 0) {
                println "  ${Features[k][0]}, " +
                        "${Features[k][1]} node(s) concerned, " +
                        "${Features[k][2]} scenarios, " +
                        "${Features[k][3]} failed (${(Features[k][3]*100F/Features[k][2]).round()}%), " +
                        "${Features[k][2]-Features[k][3]} passed (${((Features[k][2]-Features[k][3])*100F/Features[k][2]).round()}%)"
            }
        }
        println "---------------------------------------------"

        print "${Nodes.size()} nodes, "
        def nbr_node_failed = 0
        for (int k=0;k<Nodes.size();k++) {
            if (Nodes[k][2] > 0) {
                nbr_node_failed++
            }
        }
        println "${nbr_node_failed} nodes with failures (${(nbr_node_failed*100F/Nodes.size()).round()}%):"

        for (int k=0;k<Nodes.size();k++) {
            if (Nodes[k][2] > 0) {
                println "  node n°${Nodes[k][0]}, " +
                        "${Nodes[k][1]} scenarios, " +
                        "${Nodes[k][2]} failed (${(Nodes[k][2]*100F/Nodes[k][1]).round()}%), " +
                        "${Nodes[k][1]-Nodes[k][2]} passed (${((Nodes[k][1]-Nodes[k][2])*100F/Nodes[k][1]).round()}%)"
            }
        }
        println "---------------------------------------------\n"


        for(int i=0; i<Features.size(); i++) { //loop on the features
            def feat = Features[i][0]
            for (int j=0; j<Scenarios.size(); j++) { //loop on the scenarios
                def sce = Scenarios[j]

                if (Errors.size()>0) {

                    int nbr_iterations = 0

                    Errors.each { //find how many errors concern the current feature and the current scenario
                        if ((it.contains(feat)) && (it.contains(sce))) {
                            nbr_iterations++
                        }
                    }

                    for(int l=0;l<nbr_iterations;l++) {
                        int min_line = Integer.MAX_VALUE
                        int index_min = 0

                        for(int k=0;k<Errors.size();k++) { //find the index of the error with the minimum line number
                            if ((Errors[k][1]==feat) && (Errors[k][2]==sce) && (Errors[k][3] < min_line)) {
                                min_line = Errors[k][3]
                                index_min = k
                            }
                        }

                        //print the error
                        println "${Errors[index_min][1]} - node ${Errors[index_min][0]}\n" +
                                "   ${Errors[index_min][2]}"
                        def error_msg = (Errors[index_min][4]).tokenize('\n')
                        println "   ${error_msg[1]}\n" +
                                "   ${error_msg[0]}\n"

                        //remove the printed error
                        if (Errors.size()>0) {Errors.remove(index_min)} //prevent operations on null object
                    }
                }
            }
        }



        for (int i=0;i<NB_NODES;i++) {

            def report_html_path = new File("${ANDROFLEET_PATH}/results/node${i}.html")

            if (report_html_path.exists()) {
                println "Path to the html report (copy it in your web browser):\nfile://"+report_html_path
            }
            else {
                println "Html report for the node n°${i} not found"
            }
            println "\n---------------------------------------------"
        }
    }
}