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

import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * A cascade choice parameter. It receives a list of parameters to watch. In case any 
 * of these parameters change, its doUpdate(params:String) gets called, with a list 
 * of parameters and values separated by comma. For instance: "a=1,b=2,c=3".
 */
public class CascadeChoiceParameterDefinition extends ScriptParameterDefinition {

	private static final long serialVersionUID = 7486462074111049326L;

	private static final Logger LOGGER = Logger.getLogger(CascadeChoiceParameterDefinition.class.getName());
	
	/*
	 * Valid parameter values.
	 */
	public static final String PARAMETER_TYPE_SINGLE_SELECT = "PT_SINGLE_SELECT"; // default choice type
	public static final String PARAMETER_TYPE_MULTI_SELECT = "PT_MULTI_SELECT";
	public static final String PARAMETER_TYPE_CHECK_BOX = "PT_CHECKBOX";
	public static final String PARAMETER_TYPE_RADIO = "PT_RADIO";
  	public static final int DEFAULT_MAX_VISIBLE_ITEM_COUNT = 10;
	
	/**
	 * Parameter choice type.
	 */
	private final String choiceType;
	
	/**
	 * Other parameters referenced by this parameter. Any update on them will trigger the {#doUpdate) method.
	 */
	private final String referencedParameters;
	
	/**
	 * Defines whether this parameter will have a filter element or not.
	 */
	private final Boolean filterable;
	
	private Map<String, String> parameters = new HashMap<String, String>();
	
	@Deprecated
	public CascadeChoiceParameterDefinition(String name, String description, String uuid, 
			Boolean remote, String script, String choiceType, String referencedParameters) {
		this(name, description, uuid, remote, script, choiceType, referencedParameters, /* filterable */ false);
	}
	
	@DataBoundConstructor
	public CascadeChoiceParameterDefinition(String name, String description, String uuid, 
			Boolean remote, String script, String choiceType, String referencedParameters,
			Boolean filterable) {
		super(name, description, uuid, remote, script);
		this.choiceType = StringUtils.defaultIfBlank(choiceType, PARAMETER_TYPE_SINGLE_SELECT);
		this.referencedParameters = referencedParameters;
		this.filterable = filterable;
	}
	
	// getters
	
	public String getChoiceType() {
		return choiceType;
	}
	
	public String getReferencedParameters() {
		return referencedParameters;
	}
	
	public Boolean getFilterable() {
		return filterable;
	}
	
	/*
	 * (non-Javadoc)
	 * @see hudson.model.ParameterDefinition#getName()
	 */
	@Override
	@JavaScriptMethod
	public String getName() {
		return super.getName();
	}
	
	public String[] getReferencedParamatersAsArray() {
		String[] arr = getReferencedParameters().split(",");
		String[] r = new String[arr.length];
		for (int i = 0; i< arr.length ; ++i) {
			r[i] = arr[i].trim();
		}
		return r;
	}
	
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	// overrided methods in Jenkins API
	
	/*
	 * (non-Javadoc)
	 * @see hudson.model.ParameterDefinition#getDefaultParameterValue()
	 */
	@Override
	public ParameterValue getDefaultParameterValue() {
		Object firstElement = null;
		List<Object> choices = getChoices();
		if (choices != null && choices.size() > 0) {
			firstElement = choices.get(0);
		}
		String name = getName();
		StringParameterValue stringParameterValue = 
				new StringParameterValue(name, ObjectUtils.toString(firstElement, null));
		return stringParameterValue;
	}
	
	// class methods
	
	/**
	 * Get script choices.
	 * @return List
	 */
	public List<Object> getChoices() {
		try{
			return getScriptResultAsList(getParameters());
		} catch (Throwable t) {
			LOGGER.log(Level.WARNING, "Failed to evaluate script for choices.", t);
			return Collections.emptyList();
		}
	}
	
	/**
	 * Get script choices.
	 * @return List
	 */
	@JavaScriptMethod
	public List<Object> getChoices(int count) {
		List<Object> result = getScriptResultAsList(getParameters());
		return Arrays.asList(count, result);
	}
	
	/**
	 * Get the number of visible items in the select.
	 * @return
	 */
	public int getVisibleItemCount() {
		final int choicesSize = getChoices().size();
		if (choicesSize < DEFAULT_MAX_VISIBLE_ITEM_COUNT)
			return choicesSize;
		return DEFAULT_MAX_VISIBLE_ITEM_COUNT;
	}
	
	/**
	 * Exposed to the UI. Is triggered everytime any of the referenced parameters gets updated.
	 * @param parameters Comma separated list of parameters
	 */
	@JavaScriptMethod
	public void doUpdate(String parameters) {
		getParameters().clear();
		String[] params = parameters.split("__LESEP__");
		for (String param : params) {
			String[] nameValue = param.split("=");
			if (nameValue.length == 2) {
				String name = nameValue[0];
				String value = nameValue[1];
				getParameters().put(name, value);
			}
		}
	}
	
	// descriptor
	@Extension
	public static final class DescriptImpl extends ParameterDescriptor {
		
		@Override
		public String getDisplayName() {
			return "Uno-Choice Cascade Dynamic Choice Parameter";
		}
		
	}

}