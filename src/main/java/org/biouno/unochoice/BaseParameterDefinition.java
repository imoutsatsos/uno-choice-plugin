/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Ioannis K. Moutsatsos
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.biouno.unochoice;

import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.remoting.Callable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

/**
 * This is the base class to all types of parameters. This class extends
 * {@link SimpleParameterDefinition} from Jenkins API. This class is also
 * present in the Jenkins Dynamic Parameter Plug-in.
 * @see SimpleParameterDefinition
 */
public abstract class BaseParameterDefinition extends SimpleParameterDefinition {

	private static final long serialVersionUID = 3054189661123450222L;
	
	protected static final Logger LOGGER = Logger.getLogger(BaseParameterDefinition.class.getName());
	
	/**
	 * Parameter UUID.
	 */
	private final UUID uuid;
	/**
	 * Whether this parameter is dynamically loaded in the master or in the slaves.
	 */
	private final Boolean remote;
	
	/**
	 * Constructor.
	 * @param name parameter name
	 * @param description parameter description
	 * @param uuid uuid
	 * @param remote whether it is resolved in the master or on slaves
	 */
	protected BaseParameterDefinition(String name, String description, String uuid, Boolean remote) {
		super(name, description);
		if (StringUtils.isBlank(uuid))
			this.uuid = UUID.randomUUID();
		else
			this.uuid = UUID.fromString(uuid);
		this.remote = remote;
	}
	
	// getters
	public UUID getUuid() {
		return uuid;
	}
	public Boolean getRemote() {
		return remote;
	}

	// overrided methods from Jenkins API

	/*
	 * (non-Javadoc)
	 * @see hudson.model.SimpleParameterDefinition#createValue(java.lang.String)
	 */
	@Override
	public ParameterValue createValue(String value) {
		StringParameterValue parameterValue = createStringParameterValue(getName(), value);
		return parameterValue;
	}

	/*
	 * (non-Javadoc)
	 * @see hudson.model.ParameterDefinition#createValue(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)
	 */
	@Override
	public ParameterValue createValue(StaplerRequest request, JSONObject json) {
		final JSONObject parameterJsonModel = new JSONObject(false);
	    final Object value = json.get("value");
	    final String valueAsText;
	    if (JSONUtils.isArray(value))
	      valueAsText = ((JSONArray)value).join(",", true);
	    else
	      valueAsText = String.valueOf(value);
	    parameterJsonModel.put("name",  json.get("name"));
	    parameterJsonModel.put("value", valueAsText);

	    StringParameterValue parameterValue = request.bindJSON(StringParameterValue.class, parameterJsonModel);
	    parameterValue.setDescription(getDescription());
	    return parameterValue;
	}
	
	// utility methods
	
	protected abstract ScriptCallback evaluateScript(Map<String, String> parameters);
	
	private StringParameterValue createStringParameterValue(String name, String value) {
		String description = getDescription();
		StringParameterValue parameterValue = new StringParameterValue(name, value, description);
		return parameterValue;
	}
	
	/**
	 * Executes a script and gets its result as a java.util.List.
	 * @param parameters script parameters
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	public final List<Object> getScriptResultAsList(Map<String, String> parameters) {
		Object value = executeScript(parameters);
		if (value instanceof List) {
			return (List<Object>) value;
		}
		String name = getName();
		LOGGER.warning(String.format("Script parameter with name '%s' is not an instance of java.util.List. The parameter value is %s", name, value));
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * Executes a script and gets its results as String.
	 * @param parameters script parameters
	 * @return String
	 */
	public final String getScriptResultAsString(Map<String, String> parameters) {
		Object value = executeScript(parameters);
		return ObjectUtils.toString(value, null);
	}

	/**
	 * Executes the script with the given parameters.
	 * @param parameters parameters
	 * @return script eval'd
	 */
	private Object executeScript(Map<String, String> parameters) {
		Callable<Object, Throwable> callback = evaluateScript(parameters);
		Object r = null;
		try {
			r = callback.call();
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, String.format("Error executing script for dynamic paramter '%s'", getName()), e);
		}
		return r;
	}
	
}
