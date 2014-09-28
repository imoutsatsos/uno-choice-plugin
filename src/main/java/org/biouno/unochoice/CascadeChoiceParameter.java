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

/**
 * <p>A choice parameter, that gets updated when another parameter changes. The simples example
 * of a use case for this, would be to have a list of states. When the user selected a
 * state it would trigger an update of the city fields.</p>
 * 
 * <p>The state parameter would be a choice parameter, and the city parameter would be a
 * cascade choice parameter, that referenced the former.</p>
 * 
 * @author Bruno P. Kinoshita
 * @since 0.1
 */
public class CascadeChoiceParameter extends AbstractCascadableParameter {

	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = 4524790278642708107L;

	/**
	 * Choice type.
	 */
	private final String choiceType;
	
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
		super(name, description, script, fallbackScript, referencedParameters);
		this.choiceType = choiceType;
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
	 * @return the filterable
	 */
	public Boolean getFilterable() {
		return filterable;
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
