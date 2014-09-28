/*
 * The MIT License (MIT)
 * 
 * Copyright (c) <2014> <Ioannis Moutsatsos, Bruno P. Kinoshita>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.biouno.unochoice;

import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.ObjectUtils;
import org.biouno.unochoice.util.ScriptCallback;

/**
 * Base class for parameters with scripts.
 * 
 * @author Bruno P. Kinoshita
 * @since 0.20
 */
public abstract class AbstractScriptableParameter extends AbstractUnoChoiceParameter implements ScriptableParameter<Map<Object, Object>> {

	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = -1718340722437175976L;
	
	protected static final String SEPARATOR = "__LESEP__"; // used to split values that come from the UI via Ajax POST's
	
	private final String script;
	private final String fallbackScript;

	/**
	 * Inherited constructor.
	 * 
	 * {@inheritDoc}
	 * 
	 * @param name name
	 * @param description description
	 * @param script script used to generate the list of parameter values
	 * @param fallbackScript script used in case the original script fails
	 */
	protected AbstractScriptableParameter(String name, String description, String script, String fallbackScript) {
		super(name, description);
		this.script = script;
		this.fallbackScript = fallbackScript;
	}
	
	/**
	 * Gets the script.
	 * 
	 * @return the script
	 */
	public String getScript() {
		return script;
	}
	
	/**
	 * Gets the fallback script.
	 * 
	 * @return the fallback script
	 */
	public String getFallbackScript() {
		return fallbackScript;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.biouno.unochoice.ScriptableParameter#getChoices(java.util.Map)
	 */
	@SuppressWarnings("unchecked") // due to Web + Java and scripts integration
	public Map<Object, Object> getChoices(Map<Object, Object> parameters) {
		Object value;
		try {
			value = this.executeScript(parameters);
		} catch (Exception e) {
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
	
	/**
	 * Executes the script with the given parameters.
	 * 
	 * @param parameters parameters
	 * @return script eval'd
	 * @throws Exception iff the fallback script fails
	 */
	private Object executeScript(Map<Object, Object> parameters) throws Exception {
		ScriptCallback callback = new ScriptCallback(script, parameters);
		try {
			return callback.call();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, String.format("Error executing script for dynamic parameter '%s'", getName()), e);
			try {
				LOGGER.log(Level.WARNING, "Fallback to default script...");
				callback = new ScriptCallback(fallbackScript, parameters);
				return callback.call();
			} catch (Exception e1) {
				LOGGER.log(Level.SEVERE, String.format("Error executing fallback script for dynamic parameter '%s'", getName()), e);
				throw e1;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see hudson.model.ParameterDefinition#getDefaultParameterValue()
	 */
	@Override
	public ParameterValue getDefaultParameterValue() {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.entering(AbstractUnoChoiceParameter.class.getName(), "getDefaultParameterValue");
		}
		Object firstElement = "";
		final Map<Object, Object> choices = getChoices(Collections.<Object, Object> emptyMap());
		if (choices != null && choices.size() > 0) {
			firstElement = choices.get(0);
		}
		final String name = getName();
		final String value = ObjectUtils.toString(firstElement, ""); // Jenkins doesn't like null parameter values
		final StringParameterValue stringParameterValue = new StringParameterValue(name, value);
		return stringParameterValue;
	}
	
	// --- type types
	
	/**
	 * Get the number of visible items in the select.
	 * 
	 * @return the number of choices or, if it is higher than the default, then it returns the default maximum value
	 */
	public int getVisibleItemCount() {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.entering(AbstractUnoChoiceParameter.class.getName(), "getVisibleItemCount");
		}
		final int choicesSize = getChoices(Collections.<Object, Object> emptyMap()).size();
		if (choicesSize < DEFAULT_MAX_VISIBLE_ITEM_COUNT)
			return choicesSize;
		return DEFAULT_MAX_VISIBLE_ITEM_COUNT;
	}

}
