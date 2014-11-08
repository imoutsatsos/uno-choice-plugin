package org.biouno.unochoice.scriptler;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.biouno.unochoice.AbstractUnoChoiceParameter;
import org.biouno.unochoice.util.ScriptCallback;
import org.biouno.unochoice.util.Utils;
import org.jenkinsci.plugins.scriptler.config.Parameter;
import org.jenkinsci.plugins.scriptler.config.Script;
import org.jenkinsci.plugins.scriptler.util.ScriptHelper;

public abstract class AbstractScriptlerParameter extends AbstractUnoChoiceParameter implements ScriptlerParameter<Map<Object, Object>> {

	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = -8185384780174742586L;
	
	protected static final String SEPARATOR = "__LESEP__"; // used to split values that come from the UI via Ajax POST's
	
	/**
	 * Scriptler script ID.
	 */
	private final String scriptlerScriptId;
	/**
	 * Scriptler script parameters.
	 */
	private final ScriptlerScriptParameter[] scriptParameters;
	
	protected AbstractScriptlerParameter(String name, String description, final String scriptlerScriptId, final ScriptlerScriptParameter[] scriptParameters) {
		super(name, description);
		this.scriptlerScriptId = scriptlerScriptId;
		this.scriptParameters = scriptParameters;
	}
	
	/**
	 * @return the scriptler script ID
	 */
	public String getScriptlerScriptId() {
		return scriptlerScriptId;
	}
	
	/**
	 * @return the scriptler script parameters
	 */
	public ScriptlerScriptParameter[] getScriptParameters() {
		return scriptParameters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.biouno.unochoice.ScriptableParameter#getChoices(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getChoices(Map<Object, Object> parameters) {
		final String scriptId = getScriptlerScriptId();
		final Script script   = ScriptHelper.getScript(scriptId, true);
		if (script == null)
			throw new RuntimeException(String.format("No script with Scriplter ID '%s' exists", scriptId));

		// Read all parameters from job configuration
		final ScriptlerScriptParameter[] scriptParameters = getScriptParameters();
		final Map<Object, Object> parametersMap = new LinkedHashMap<Object, Object>(scriptParameters.length);
		for (Parameter parameter : scriptParameters) {
			parametersMap.put(parameter.getName(), parameter.getValue());
		}

		// Set default values, in case the value has not been set in job
		// configuration
		for (Parameter parameter : script.getParameters()) {
			if (!parametersMap.containsKey(parameter.getName())) {
				parametersMap.put(parameter.getName(), parameter.getValue());
			}
		}
		// add all parameter specific parameters to the script call
		parametersMap.putAll(parameters);

		// create the script call
		ScriptCallback<Object, Exception> callback = new ScriptCallback<Object, Exception>(getName(), script.script, parametersMap);
		Object value;
		try {
			value = Utils.executeScript(callback, null, parameters);
		} catch (Throwable e) {
			return Collections.EMPTY_MAP;
		}
		if (value instanceof Map) {
			return (Map<Object, Object>) value;
		}
		if (value instanceof List) {
			// here we take a list and return it as a map
			final Map<Object, Object> map = new LinkedHashMap<Object, Object>();
			for (Object o : (List<Object>) value) {
				map.put(o, o);
			}
			return map;
		}
		LOGGER.warning(String.format("Script parameter with name '%s' is not an instance of java.util.Map. The parameter value is %s", getName(), value));
		return Collections.EMPTY_MAP;
	}
	
	public String getChoicesAsString() {
		return getChoicesAsString(getParameters());
	}
	
	public String getChoicesAsString(Map<Object, Object> parameters) {
		final Object value = eval(parameters);
		if (value != null) 
			return value.toString();
		return "";
	}
	
	private Object eval(Map<Object, Object> parameters) {
		final Object value;
		try {
			ScriptCallback<Object, Exception> callback = new ScriptCallback<Object, Exception>(getName(), script, parameters);
			ScriptCallback<Object, Exception> fallback = null;
			if (fallbackScript != null)
				fallback = new ScriptCallback<Object, Exception>(getName(), fallbackScript, parameters);
			value = Utils.executeScript(callback, fallback, parameters);
			return value;
		} catch (Throwable e) {
			return Collections.EMPTY_MAP;
		}
	}
	
}
