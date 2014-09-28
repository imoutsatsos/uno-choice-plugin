package org.biouno.unochoice;

import java.util.List;
import java.util.Map;

/**
 * A parameter that monitors other referenced parameters. When any of these parameters change, 
 * this parameter gets updated. It has a map with the current parameters that can be retrieved
 * by the UI to render the most updated view, based on the state of the referenced parameters.
 * 
 * @author Bruno P. Kinoshita
 * @since 0.20
 */
public interface CascadableParameter<T> extends ScriptableParameter<T> {

	/**
	 * Gets the current parameters, be it before or after other referenced parameters 
	 * triggered an update.
	 * 
	 * @return the current parameters
	 */
	public Map<Object, Object> getParameters();
	
	/**
	 * Gets the list of referenced parameters. If any of these parameters change in the
	 * UI we will update our current parameters.
	 * 
	 * @return the referencedParameters
	 */
	public String getReferencedParameters();
	
	/**
	 * Evaluates a script and returns its result as a Map. List values are automatically handled and converted to
	 * Maps too.
	 * 
	 * @param count the current value in the UI element
	 * @param parameters parameters
	 * @return script result as Map
	 */
	public List<Object> getChoices(int count);
	
	/**
	 * Exposed to the UI. Is triggered everytime any of the referenced parameters gets updated.
	 * @param parameters Comma separated list of parameters
	 */
	public void doUpdate(String parameters);
	
}
