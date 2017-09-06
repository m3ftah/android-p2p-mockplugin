//---------------------------------------------------------------//
//-----------------------Sort by features------------------------//
//---------------------------------------------------------------//

if (sort_type == "features") {

	var TOT_FEAT = 0;
	var TOT_F_FEAT = 0;
	var TOT_SCE = 0;
	var TOT_F_SCE = 0;
	var TOT_U_SCE = 0;
	var TOT_U_F_SCE = 0;

	//rapid count of the number of unique features
	db.features.aggregate([
		{//group by feature
			$group : {_id: {feat_id: "$id"}}
		},
		{//count the number of unique features
			$count: "nbr_tot_features"
		}
	]).forEach(function(myDoc) {TOT_FEAT = myDoc.nbr_tot_features});


	//rapid count of the number of scenario's executions and unique scenarios
	db.features.aggregate([
		{//create one document for each scenario
			$unwind : "$elements"
		},
		{//create one document for each step
			$unwind : "$elements.steps"
		},
		{//add the number of the node to the step
			$addFields : {"elements.steps.node": "$uri"}
		},
		{//group by scenario and feature
			$group : {
				_id: {feat_id: "$id", scenario_id: "$elements.id"},
				feat_uri: {"$addToSet": "$uri"}
			}
		},
		{//group by feature
			$group : {
				_id: {feat_id: "$_id.feat_id"},
				scenarios: {$push: {
					scenario_id: "$_id.scenario_id",
					nbr_nodes_concerned: {$max: {$size: "$feat_uri"}} //allows to calculate the total number of scenarios
				}}
			}
		},
		{//now add fields containing the number of scenarios and unique scenarios
			$addFields : {
				"nbr_tot_scenarios": {$sum: "$scenarios.nbr_nodes_concerned"},
				"nbr_tot_diff_scenarios": {$size: "$scenarios" }
			}
		}
	]).forEach(function(myDoc) {
			TOT_SCE += myDoc.nbr_tot_scenarios;
			TOT_U_SCE += myDoc.nbr_tot_diff_scenarios
	});


	//query to sort by features
	var sort_Features = db.features.aggregate([
		{//create one document for each scenario
			$unwind : "$elements"
		},
		{//create one document for each step
			$unwind : "$elements.steps"
		},
		{//add the number of the node to the step
			$addFields : {"elements.steps.node": "$uri"}
		},
		{//keep only relevant fields
			$project : {
				"uri": 1,
				"id": 1,
				"name": 1,
				"elements.id": 1,
				"elements.name": 1,
				"elements.steps.node": 1,
				"elements.steps.keyword": 1,
				"elements.steps.name": 1,
				"elements.steps.line": 1,
				"elements.steps.result.status": 1,
				"elements.steps.result.error_message": 1
			}
		},
		{//group by scenario and feature
			$group : {
				_id: {feat_id: "$id", scenario_id: "$elements.id"},
				feat_name: {$first: "$name"},
				feat_uri: {"$addToSet": "$uri"},
				scenario_name: {$first: "$elements.name"},
				steps: {"$addToSet": "$elements.steps"}
			}
		},
		{//group by feature
			$group : {
				_id: {feat_id: "$_id.feat_id"},
				feat_name: {$first: "$feat_name"},
				scenarios: {$push: {
					scenario_name: "$scenario_name",
					scenario_id: "$_id.scenario_id",
					nbr_nodes_concerned: {$max: {$size: "$feat_uri"}}, //allows to calculate the total number of scenarios
					steps: "$steps"
				}}
			}
		},
		{//add fields containing the number of scenarios and scenario executions to a feature
			$addFields : {
				"nbr_tot_scenarios": {$sum: "$scenarios.nbr_nodes_concerned"},
				"nbr_tot_diff_scenarios": {$size: "$scenarios" }
			}
		},
		{//create one document for each scenario
			$unwind : "$scenarios"
		},
		{//create one document for each step
			$unwind : "$scenarios.steps"
		},
		{//keep only the failed steps
			$match : {"scenarios.steps.result.status": "failed"}
		},
		{//sort the steps by node number
			$sort : {"scenarios.scenario_name": 1, "scenarios.steps.node": -1}
		},
		{//group by scenario and feature
			$group : {
				_id: {feat_id: "$_id.feat_id", scenario_id: "$scenarios.scenario_id"},
				feat_name: {$first: "$feat_name"},
				scenario_name: {$first: "$scenarios.scenario_name"},
				nbr_nodes_concerned: {$first: "$scenarios.nbr_nodes_concerned"},
				steps: {$addToSet: "$scenarios.steps"},
				nbr_tot_scenarios: {$first: "$nbr_tot_scenarios"},
				nbr_tot_diff_scenarios: {$first: "$nbr_tot_diff_scenarios"}
			}
		},
		{//group by feature
			$group : {
				_id: {feat_id: "$_id.feat_id"},
				feat_name: {$first: "$feat_name"}, 
				scenarios: {$push: {
					scenario_name: "$scenario_name",
					scenario_id: "$_id.scenario_id",
					nbr_nodes_concerned: "$nbr_nodes_concerned",
					nbr_failed_steps: {$size: "$steps"},
					steps: "$steps"
				}},
				nbr_tot_scenarios: {$first: "$nbr_tot_scenarios"},
				nbr_tot_diff_scenarios: {$first: "$nbr_tot_diff_scenarios"}	
			}
		},
		{//calculate the numbers of failed scenarios
			$addFields : {
				"nbr_tot_failed_scenarios": {$sum: "$scenarios.nbr_failed_steps"}, //number of failed scenarios, there can be multiple identical scenarios
				"nbr_tot_diff_failed_scenarios": {$size: "$scenarios" } //number of failed scenarios, scenarios being all different
			}
		}/**/
	]);

	var result1 = sort_Features.toArray();


	//Calculation of the sums
	result1.forEach( function(myDoc) {
		TOT_F_FEAT += 1; //if a feature is present in myDoc it means that it failed (and it is unique)
		TOT_F_SCE += myDoc.nbr_tot_failed_scenarios;
		TOT_U_F_SCE += myDoc.nbr_tot_diff_failed_scenarios;
	})


	//Understandable display
	if (display_choice=="pretty") {
		print("------------------------------")
		print("Totals: "+ TOT_F_FEAT + "/" + TOT_FEAT +" feature(s) failed");
		print("        "+ TOT_F_SCE + "/" + TOT_SCE +" scenario execution(s) failed");
		print("        "+ TOT_U_F_SCE + "/" + TOT_U_SCE +" scenario(s) failed");

		var n = 1;
		result1.forEach( function(myDoc) { //loop on the features
			print("------------------------------")

			print('\n'+n+'- '+myDoc.feat_name+' --> ' 
				+myDoc.nbr_tot_failed_scenarios+'/'+myDoc.nbr_tot_scenarios+" scenario execution(s) failed"
				+" & "+myDoc.nbr_tot_diff_failed_scenarios+'/'+myDoc.nbr_tot_diff_scenarios+" scenario(s) failed"
				); 

			for(i=0 ; i <myDoc.scenarios.length ; i++) { //loop on the scenarios
				
				var scenario = myDoc.scenarios[i];
				print("\n    "+(i+1)+'- Scenario: '+scenario.scenario_name+' --> '
					+'failures in '+scenario.steps.length+'/'+scenario.nbr_nodes_concerned+' node(s)'
					);

				for(j=0 ; j<scenario.steps.length ; j++) { //loop on the steps

					var step = scenario.steps[j]
					
					var error_msg = step.result.error_message.split('\n') //tokenize the error message to remove any \n and be sure to print correctly
					for(k=0 ; k<error_msg.length ; k++) {
						print("    |   "+error_msg[k]);
					}
					print("")
				}
			}
			n++;
		});
	}


	//Addition of a document containing the totals
	result1.push({
		"tot_features": TOT_FEAT,
		"tot_failed_features": TOT_F_FEAT,
		"tot_scenarios": TOT_SCE,
		"tot_failed_scenarios": TOT_F_SCE,
		"tot_diff_scenarios": TOT_U_SCE,
		"tot_diff_failed_scenarios": TOT_U_F_SCE
	})


	//Print to json format
	if (display_choice=="json") {
		result1.forEach(printjson);
	}
}


//---------------------------------------------------------------//
//-------------------------Sort by nodes-------------------------//
//---------------------------------------------------------------//


if (sort_type == "nodes") {

	var TOT_NODE = 0;
	var TOT_F_NODE = 0;
	var TOT_SCE = 0;
	var TOT_F_SCE = 0;
	var TOT_U_SCE = 0;
	var TOT_U_F_SCE = 0;

	//rapid count of the number of nodes
	db.features.aggregate([
		{//group by feature
			$group : {_id: {feat_id: "$uri"}}
		},
		{//count the number of unique features
			$count: "nbr_tot_nodes"
		}
	]).forEach(function(myDoc) {TOT_NODE = myDoc.nbr_tot_nodes});


	//rapid count of the number of scenarios
	db.features.aggregate([
		{//add the number of scenarios related to a feature (they are necessarily unique for one node)
			$addFields: {"nbr_tot_scenarios": {$size: "$elements"}}
		}
	]).forEach(function(myDoc) {
			TOT_SCE += myDoc.nbr_tot_scenarios;
	});


	//rapid count of the number of scenario executions
	db.features.aggregate([
		{//create one document for each scenario
			$unwind : "$elements"
		},
		{//group by scenario
			$group : {
				_id: {scenario_id: "$elements.id"},
			}
		},
		{//count the number of unique scenarios
			$count: "nbr_tot_diff_scenarios"
		}
	]).forEach(function(myDoc) {
			TOT_U_SCE = myDoc.nbr_tot_diff_scenarios
	});


	//rapid count of the number of failed scenario executions
	db.features.aggregate([
		{//create one document for each scenario
			$unwind : "$elements"
		},
		{//create one document for each step
			$unwind : "$elements.steps"
		},
		{//keep only the failed steps
			$match : {"elements.steps.result.status": "failed"}
		},
		{//group by scenario
			$group : {
				_id: {scenario_id: "$elements.id"},
			}
		},
		{//count the number of unique scenarios
			$count: "nbr_tot_diff_failed_scenarios"
		}/**/
	]).forEach(function(myDoc) {
			TOT_U_F_SCE = myDoc.nbr_tot_diff_failed_scenarios
	});


	//query to sort by nodes
	var sort_Nodes = db.features.aggregate([
		{//add the number of scenarios related to a feature (they are necessarily unique for one node)
			$addFields: {"nbr_tot_scenarios": {$size: "$elements"}}
		},
		{//create one document for each scenario
			$unwind: "$elements"
		},
		{//create one document for each step
			$unwind : "$elements.steps"
		},
		{//keep only relevant fields
			$project : {
				"uri": 1,
				"id": 1,
				"name": 1,
				"nbr_tot_scenarios": 1,
				"elements.id": 1,
				"elements.name": 1,
				"elements.steps.keyword": 1,
				"elements.steps.name": 1,
				"elements.steps.line": 1,
				"elements.steps.result.status": 1,
				"elements.steps.result.error_message": 1
			}
		},
		{//group by node, feature and scenario
			$group : {
				_id: {node_id: "$uri", feat_id: "$id", scenario_id: "$elements.id"},
				feat_name: {$first: "$name"},
				nbr_tot_scenarios: {$first: "$nbr_tot_scenarios"},
				scenario_name: {$first: "$elements.name"},
				steps: {"$addToSet": "$elements.steps"}
			}
		},
		{//group by node and feature
			$group : {
				_id: {node_id: "$_id.node_id", feat_id: "$_id.feat_id"},
				feat_name: {$first: "$feat_name"},
				nbr_tot_scenarios: {$first: "$nbr_tot_scenarios"},
				scenarios: {$push: {
					scenario_name: "$scenario_name",
					scenario_id: "$_id.scenario_id",
					steps: "$steps"
				}}
			}
		},
		{//group by node
			$group : {
				_id: {node_id: "$_id.node_id"},
				features: {$push: {
					feat_id: "$_id.feat_id",
					feat_name: "$feat_name",
					nbr_tot_scenarios: "$nbr_tot_scenarios",
					scenarios: "$scenarios"
				}}
			}
		},
		{//add the number of features for each node
			$addFields : {"nbr_tot_features": {$size: "$features"}}
		},
		{//create one document for each feature
			$unwind : "$features"
		},
		{//create one document for each scenario
			$unwind : "$features.scenarios"
		},
		{//create one document for each step
			$unwind : "$features.scenarios.steps"
		},
		{//keep only the failed steps
			$match : {"features.scenarios.steps.result.status": "failed"}
		},
		{//group by node, feature and scenario
			$group : {
				_id: {node_id: "$_id.node_id", feat_id: "$features.feat_id", scenario_id: "$features.scenarios.scenario_name"},
				nbr_tot_features: {$first: "$nbr_tot_features"},
				feat_name: {$first: "$features.feat_name"},
				scenario_name: {$first: "$features.scenarios.scenario_name"},
				nbr_tot_scenarios: {$first: "$features.nbr_tot_scenarios"},
				keyword: {$first: "$features.scenarios.steps.keyword"},
				step: {$first: "$features.scenarios.steps.name"},
				line: {$first: "$features.scenarios.steps.line"},
				status: {$first: "$features.scenarios.steps.result.status"},
				error_msg: {$first: "$features.scenarios.steps.result.error_message"}
			}
		},
		{//sort in order of appareance in the node
			$sort : {"line": 1} 
		},
		{//group by node and feature
			$group : {
				_id: {node_id: "$_id.node_id", feat_id: "$_id.feat_id"},
				nbr_tot_features: {$first: "$nbr_tot_features"},
				feat_name: {$first: "$feat_name"},
				nbr_tot_scenarios: {$first: "$nbr_tot_scenarios"},
				scenarios: {$push: {
					scenario_name: "$scenario_name",
					scenario_id: "$_id.scenario_id",
					keyword: "$keyword",
					step: "$step",
					line: "$line",
					status: "$status",
					error_msg: "$error_msg"
				}}
			}
		},
		{//group by node
			$group : {
				_id: {node_id: "$_id.node_id"},
				features: {$push: {
					feat_id: "$_id.feat_id",
					feat_name: "$feat_name",
					nbr_tot_scenarios: "$nbr_tot_scenarios",
					nbr_tot_failed_scenarios: {$size: "$scenarios"}, //now each feature contains only the failed scenarios
					scenarios: "$scenarios"
				}},
				nbr_tot_features: {$first: "$nbr_tot_features"},
			}
		},
		{//add the number of failed features for a node
			$addFields : {"nbr_tot_failed_features": {$size: "$features"}}
		},
		{//sort by ascending node number 
			$sort : { "_id.node_id": 1}
		}
	])

	var result2 = sort_Nodes.toArray();


	//Calculation of the sums
	result2.forEach( function(myDoc) {
		TOT_F_NODE += 1;
		for(i=0 ; i <myDoc.features.length ; i++) {
			TOT_F_SCE += myDoc.features[i].nbr_tot_failed_scenarios;
		}
	})


	//Understandable display
	if (display_choice=="pretty") {
		print("------------------------------")
		print("Totals: "+TOT_F_NODE+"/"+TOT_NODE+" node(s) with failures");
		print("        "+TOT_F_SCE+"/"+TOT_SCE+" scenario execution(s) failed");
		print("        "+ TOT_U_F_SCE + "/" + TOT_U_SCE +" scenario(s) failed");
		
		result2.forEach( function(myDoc) { //loop on the nodes
			print("------------------------------")

			print('\nNode: ' + myDoc._id.node_id+' --> '
				+myDoc.nbr_tot_failed_features+"/"+myDoc.nbr_tot_features+' feature(s) failed')

			for(i=0 ; i <myDoc.features.length ; i++) {//loop on the features
				
				var feature = myDoc.features[i];
				print('\n    '+(i+1)+'- Feature: '+feature.feat_name+' --> '
					+feature.nbr_tot_failed_scenarios+'/'+feature.nbr_tot_scenarios+' scenario(s) failed'
					);

				for(j=0 ; j<feature.scenarios.length ; j++) {

					var scenario = feature.scenarios[j];
					print("    |   "+(j+1)+'- Scenario: '+scenario.scenario_name);

					var error_msg = scenario.error_msg.split('\n') //tokenize the error message to remove any \n and be sure to print correctly
					for(k=0 ; k<error_msg.length ; k++) {
						print("    |   .   "+error_msg[k]);
					}
					print("    |")
				}
			}
		});
	}


	//Addition of a document containing the totals
	result2.push({
		"tot_nodes": TOT_NODE,
		"tot_failed_nodes": TOT_F_NODE,
		"tot_scenarios": TOT_SCE,
		"tot_failed_scenarios": TOT_F_SCE
	})


	//Print to json format
	if (display_choice=="json") {
		result2.forEach(printjson);
	}
}

