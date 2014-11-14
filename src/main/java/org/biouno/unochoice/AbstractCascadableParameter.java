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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.biouno.unochoice.model.Script;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * Base class for cascadable parameters, providing basic and utility methods.
 *
 * @author Bruno P. Kinoshita
 * @since 0.20
 */
public abstract class AbstractCascadableParameter extends AbstractScriptableParameter implements CascadableParameter<Map<Object, Object>> {

	/*
	 * Serial UID. 
	 */
	private static final long serialVersionUID = 6992538803651219246L;
	/**
	 * Map with parameters in the UI.
	 */
	protected final Map<Object, Object> parameters = new LinkedHashMap<Object, Object>();
	
	/**
	 * Referenced parameters.
	 */
	private final String referencedParameters;
	
	protected AbstractCascadableParameter(String name, String description,
			Script script, String referencedParameters) {
		super(name, description, script);
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
	 * @see org.biouno.unochoice.AbstractScriptableParameter#getParameters()
	 */
	public Map<Object, Object> getParameters() {
		return parameters;
	}
	
	// --- methods called from the UI
	
	/*
	 * (non-Javadoc)
	 * @see org.biouno.unochoice.CascadableParameter#doUpdate(java.lang.String)
	 */
	@JavaScriptMethod
	public void doUpdate(String parameters) {
		getParameters().clear();
		final String[] params = parameters.split(SEPARATOR);
		for (String param : params) {
			final String[] nameValue = param.split("=");
			if (nameValue.length == 1) {
				final String name = nameValue[0].trim();
				if (name.length() > 0)
					getParameters().put(name, "");
			} else if (nameValue.length == 2) {
				final String name = nameValue[0];
				final String value = nameValue[1];
				getParameters().put(name, value);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.biouno.unochoice.CascadableParameter#getChoicesForUI()
	 */
	@JavaScriptMethod
	public List<Object> getChoicesForUI() {
		Map<Object, Object> mapResult = getChoices(getParameters());
		return Arrays.<Object>asList(mapResult.values(), mapResult.keySet());
	}
	
	public String[] getReferencedParametersAsArray() {
		String referencedParameters = this.getReferencedParameters();
		if (StringUtils.isNotBlank(referencedParameters)) {
			String[] array = referencedParameters.split(",");
			List<String> list = new ArrayList<String>();
			for (String value : array) {
				value = value.trim();
				if (StringUtils.isNotBlank(value)) {
					list.add(value);
				}
			}
			return list.toArray(new String[0]);
		}
		return new String[]{};
	}
	
}
