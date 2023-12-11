/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
 */
package org.topbraid.shacl.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileUtils;
import org.topbraid.shacl.rules.RuleUtil;

/**
 * Stand-alone utility to perform inferences based on SHACL rules from a given file.
 *
 * Example arguments:
 * 
 * 		-datafile my.ttl
 * 
 * @author Holger Knublauch
 */
public class Infer extends AbstractTool {
	
	public static void main(String[] args) throws IOException {
		// Temporarily redirect system.err to avoid SLF4J warning
		PrintStream oldPS = System.err;
		System.setErr(new PrintStream(new ByteArrayOutputStream()));
		Infer infer = new Infer();
		System.setErr(oldPS);
		infer.run(args);
	}


	private void run(String[] args) throws IOException {
		Model dataModel = getDataModel(args);
		Model shapesModel = getShapesModel(args);
		if(shapesModel == null) {
			shapesModel = dataModel;
		}
        int maxIterations = getMaxIterations(args);

        // execute the rules over and over until there are no new results
        // or until a maximum number of iterations has been reached.
        // Keep track of all results from each iteration in a separate model.
        // Break if no new results are found, or if the size of the allResults
        // model does not increase after a run.

        // stores which iteration we are on
        int iteration = 0;
        // stores all results from each iteration
        Model results = null;
        do {
            // execute the rules
            Model newResults = RuleUtil.executeRules(dataModel, shapesModel, null, null);
            // if this is the first iteration, set the results model, otherwise add to it
            if (results == null) {
                results = newResults;
            } else {
                results.add(newResults);
            }
            // if no new results were found, break
            if (newResults.size() == 0) {
                break;
            }
            // if the size of the allResults model did not increase, break
            // (this means that the new results were already in the allResults model)
            if (results.size() == newResults.size()) {
                break;
            }
            // otherwise, continue by adding the new results to the data model, incrementing the iteration,
            // and continuing the loop to see if more inferences are performed
            dataModel.add(newResults);
            iteration++;
        } while (iteration < maxIterations);

        // print results
        results.write(System.out, FileUtils.langTurtle);
    }
}
