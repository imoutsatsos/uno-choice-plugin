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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * TODO
 * 
 * @author Bruno P. Kinoshita
 * @since 0.1
 */
public class CascadeChoiceParameter extends AbstractScriptableParameter {

	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = 4524790278642708107L;

	/**
	 * Map with parameters in the UI.
	 */
	private final Map<Object, Object> parameters = new LinkedHashMap<Object, Object>();
	
	/**
	 * Choice type.
	 */
	private final String choiceType;
	
	/**
	 * Referenced parameters.
	 */
	private final String referencedParameters;
	
	/**
	 * Filter flag.
	 */
	private final Boolean filterable;

	/**
	 * Creates a new parameter.
	 * 
	 * @param name name
	 * @param description description
	 * @param script script
	 * @param fallbackScript fallback script
	 * @param choiceType choice type
	 * @param referencedParameters referenced parameters
	 * @param filterable filter flag
	 */
	protected CascadeChoiceParameter(String name, String description, String script, String fallbackScript, 
			String choiceType, String referencedParameters, Boolean filterable) {
		super(name, description, script, fallbackScript);
		this.choiceType = choiceType;
		this.referencedParameters = referencedParameters;
		this.filterable = filterable;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.biouno.unochoice.AbstractUnoChoiceParameter#getChoiceType()
	 */
	@Override
	public String getChoiceType() {
		return choiceType;
	}
	
	/**
	 * @return the referencedParameters
	 */
	public String getReferencedParameters() {
		return referencedParameters;
	}

	/**
	 * @return the filterable
	 */
	public Boolean getFilterable() {
		return filterable;
	}
	
	// --- methods called from the UI
	
	/**
	 * Get script choices.
	 * 
	 * TODO: explain why this method was created.
	 * 
	 * @return List
	 */
	@JavaScriptMethod
	public List<Object> getChoices(int count) {
		Map<Object, Object> mapResult = getChoices(parameters);
		return Arrays.asList(count, mapResult.values(), mapResult.keySet());
	}
	
	/**
	 * Exposed to the UI. Is triggered everytime any of the referenced parameters gets updated.
	 * @param parameters Comma separated list of parameters
	 */
	@JavaScriptMethod
	public void doUpdate(String postedParameters) {
		parameters.clear();
		final String[] params = postedParameters.split(SEPARATOR);
		for (String param : params) {
			final String[] nameValue = param.split("=");
			if (nameValue.length == 2) {
				final String name = nameValue[0];
				final String value = nameValue[1];
				parameters.put(name, value);
			}
		}
	}
	
	// --- descriptor
	
	@Extension
	public static final class DescriptImpl extends ParameterDescriptor {
		
		@Override
		public String getDisplayName() {
			return "Uno-Choice Cascade Dynamic Choice Parameter";
		}
		
	}

}
