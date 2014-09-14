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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * Adapted from Jenkins Dynamic Parameter Plug-in. Due to 
 * https://github.com/Seitenbau/sb-jenkins-dynamicparameter/issues/23 
 * and incompatibility with 1.532.2.
 */
public class ChoiceParameterDefinition extends ScriptParameterDefinition {

	private static final long serialVersionUID = 4735461963713345625L;
	
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
	 * Defines whether this parameter will have a filter element or not.
	 */
	private final Boolean filterable;
	
	@Deprecated
	public ChoiceParameterDefinition(String name, String description, String uuid, 
			Boolean remote, String script, String choiceType, Boolean filterable) {
		super(name, description, uuid, remote, script, null);
		this.choiceType = StringUtils.defaultIfBlank(choiceType, PARAMETER_TYPE_SINGLE_SELECT);
		this.filterable = filterable;
	}
	
	@DataBoundConstructor
	public ChoiceParameterDefinition(String name, String description, String uuid, 
			Boolean remote, String script, String defaultScript, String choiceType, Boolean filterable) {
		super(name, description, uuid, remote, script, defaultScript);
		this.choiceType = StringUtils.defaultIfBlank(choiceType, PARAMETER_TYPE_SINGLE_SELECT);
		this.filterable = filterable;
	}
	
	// getters
	@JavaScriptMethod
	public String getChoiceType() {
		return choiceType;
	}
	
	public Boolean getFilterable() {
		return filterable;
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
		return getScriptResultAsList(Collections.<String, Object> emptyMap());
	}
	
	public Map<Object, Object> getChoicesAsMap() {
		return getScriptResultAsMap(Collections.<String, Object> emptyMap());
	}
	
	/**
	 * Get the number of visible items in the select.
	 * @return
	 */
	public int getVisibleItemCount() {
		int choicesSize = DEFAULT_MAX_VISIBLE_ITEM_COUNT;
		if (choiceType.equals(PARAMETER_TYPE_MULTI_SELECT) || choiceType.equals(PARAMETER_TYPE_SINGLE_SELECT))
			choicesSize = getChoicesAsMap().size();
		else
			choicesSize = getChoices().size();
		if (choicesSize < DEFAULT_MAX_VISIBLE_ITEM_COUNT)
			return choicesSize;
		return DEFAULT_MAX_VISIBLE_ITEM_COUNT;
	}
	
	// descriptor
	@Extension
	public static final class DescriptImpl extends ParameterDescriptor {
		
		@Override
		public String getDisplayName() {
			return "Uno-Choice Dynamic Choice Parameter";
		}
		
	}

}