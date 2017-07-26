var classement_Features = db.features.aggregate([
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
	{//now add fields containing the value of scenarios and unique scenarios to a feature
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
	{//now our number of scenarios are calculated, we can keep only the failed steps
		$match : {"scenarios.steps.result.status": "failed"}
	},
	{//group by scenario and feature
		$group : {
			_id: {feat_id: "$_id.feat_id", scenario_id: "$scenarios.scenario_id"},
			feat_name: {$first: "$feat_name"},
			scenario_name: {$first: "$scenarios.scenario_name"},
			steps: {"$addToSet": "$scenarios.steps"},
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
	}
])

var result = classement_Features.toArray();

//Calculation of the sums
var TOT_SCE = 0;
var TOT_F_SCE = 0;
var TOT_U_SCE = 0;
var TOT_U_F_SCE = 0;

result.forEach( function(myDoc) {
	TOT_SCE += myDoc.nbr_tot_scenarios;
	TOT_F_SCE += myDoc.nbr_tot_failed_scenarios;
	TOT_U_SCE += myDoc.nbr_tot_diff_scenarios;
	TOT_U_F_SCE += myDoc.nbr_tot_diff_failed_scenarios;
})


//Understandable display
if (display_choice=="pretty") {
	print("------------------------------")
	print("Totals: "+ TOT_F_SCE + "/" + TOT_SCE +" scenarios failed");
	print("        "+ TOT_U_F_SCE + "/" + TOT_U_SCE +" unique scenarios failed");

	result.forEach( function(myDoc) { //loop on the features
		print("------------------------------")

		print( '\n' + myDoc.feat_name + ' --> ' 
			+ myDoc.nbr_tot_failed_scenarios + '/' + myDoc.nbr_tot_scenarios + " scenarios failed"
			+ " & " + myDoc.nbr_tot_diff_failed_scenarios + '/' + myDoc.nbr_tot_diff_scenarios + " unique scenarios failed"
			); 

		for(i=0 ; i <myDoc.scenarios.length ; i++) { //loop on the scenarios
			
			print( "\n    "  + myDoc.scenarios[i].scenario_name);

			for(j=0 ; j<myDoc.scenarios[i].steps.length ; j++) { //loop on the steps

				var step = myDoc.scenarios[i].steps[j]
				print("    |   " + step.line + " " + step.keyword + step.name +" (" + step.node + ")");
				
				var error_msg = step.result.error_message.split('\n') //tokenize the error message to remove any \n and be sure to print correctly
				for(k=0 ; k<error_msg.length ; k++) {
					print("    |   " + error_msg[k]);
				}
				print("")
			}
		}
	} );
}


//Addition of a document containing the totals
result.push({
	"tot_scenarios": TOT_SCE,
	"tot_failed_scenarios": TOT_F_SCE,
	"tot_diff_scenarios": TOT_U_SCE,
	"tot_diff_failed_scenarios": TOT_U_F_SCE
})


//Print to json format
if (display_choice=="json") {
	result.forEach(printjson);
}
