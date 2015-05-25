package org.topbraid.shacl.constraints;

import java.util.List;

import org.topbraid.shacl.model.SHACLShape;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Encapsulates a single constraint that can be executed, possibly together with pre-bound
 * variables stemming from template calls.
 * 
 * @author Holger Knublauch
 */
public abstract class ConstraintExecutable {
	
	
	/**
	 * Gets the severity level (e.g. sh:Warning).
	 * @return the level class, never null
	 */
	public abstract Resource getSeverity();
	
	
	/**
	 * Gets the specified sh:messages, to be used for constructed results.
	 * @return the messages (may be empty)
	 */
	public abstract List<Literal> getMessages();
	
	
	/**
	 * Gets the specified sh:predicate (if any), to be used for constructed results.
	 * @return the predicate or null
	 */
	public abstract Resource getPredicate();
	
	
	/**
	 * Gets the specified sh:scopeShapes, to be used as pre-conditions.
	 * @return the scope shapes
	 */
	public abstract List<SHACLShape> getScopeShapes();
}