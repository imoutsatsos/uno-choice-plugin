package org.biouno.unochoice.scriptler;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.biouno.unochoice.CascadableParameter;
import org.kohsuke.stapler.bind.JavaScriptMethod;

public abstract class AbstractCascadableScriptlerParameter extends AbstractScriptlerParameter implements CascadableParameter<Map<Object, Object>> {

	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = 5604568507990190279L;
	
	/**
	 * Other parameters referenced by this parameter. Any update on them will trigger the {#doUpdate) method.
	 */
	private final String referencedParameters;
	/**
	 * Map with parameters in the UI.
	 */
	protected final Map<Object, Object> parameters = new LinkedHashMap<Object, Object>();

	protected AbstractCascadableScriptlerParameter(String name, String description, String scriptlerScriptId,
			ScriptlerScriptParameter[] scriptParameters, String referencedParameters) {
		super(name, description, scriptlerScriptId, scriptParameters);
		this.referencedParameters = referencedParameters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.biouno.unochoice.CascadableParameter#getReferencedParameters()
	 */
	public String getReferencedParameters() {
		return referencedParameters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.biouno.unochoice.CascadableParameter#getParameters()
	 */
	public Map<Object, Object> getParameters() {
		return parameters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.biouno.unochoice.CascadableParameter#getChoices(int)
	 */
	@JavaScriptMethod
	public List<Object> getChoices(int count) {
		Map<Object, Object> mapResult = getChoices(getParameters());
		return Arrays.asList(count, mapResult.values(), mapResult.keySet());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.biouno.unochoice.CascadableParameter#doUpdate(java.lang.String)
	 */
	public void doUpdate(String parameters) {
		getParameters().clear();
		final String[] params = parameters.split(SEPARATOR);
		for (String param : params) {
			final String[] nameValue = param.split("=");
			if (nameValue.length == 2) {
				final String name = nameValue[0];
				final String value = nameValue[1];
				getParameters().put(name, value);
			}
		}
	}
	
}
