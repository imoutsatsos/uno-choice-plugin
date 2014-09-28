package org.biouno.unochoice.scriptler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.biouno.unochoice.AbstractUnoChoiceParameter;
import org.biouno.unochoice.util.ScriptCallback;
import org.jenkinsci.plugins.scriptler.config.Parameter;
import org.jenkinsci.plugins.scriptler.config.Script;
import org.jenkinsci.plugins.scriptler.util.ScriptHelper;

public abstract class AbstractScriptlerParameter extends AbstractUnoChoiceParameter implements ScriptlerParameter {

	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = -8185384780174742586L;
	
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
	public ScriptCallback getChoices(Map<Object, Object> parameters) {
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
		ScriptCallback call = new ScriptCallback(script.script, parametersMap);
		return call;
	}
	
}
