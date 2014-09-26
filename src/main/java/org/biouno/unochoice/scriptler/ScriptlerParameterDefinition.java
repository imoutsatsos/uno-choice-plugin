/*
 * Copyright 2012 Seitenbau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.biouno.unochoice.scriptler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.biouno.unochoice.BaseParameterDefinition;
import org.biouno.unochoice.util.ScriptCallback;
import org.jenkinsci.plugins.scriptler.config.Parameter;
import org.jenkinsci.plugins.scriptler.config.Script;
import org.jenkinsci.plugins.scriptler.util.ScriptHelper;
import org.kohsuke.stapler.DataBoundConstructor;

/** Base class for all dynamic parameters using Scriptler scripts. */
public abstract class ScriptlerParameterDefinition extends BaseParameterDefinition {
	
	private static final long serialVersionUID = -3251343753070860079L;

	/** Scriptler script id. */
	private final String scriptlerScriptId;

	/** Script parameters. */
	private final ScriptParameter[] scriptParameters;

	protected ScriptlerParameterDefinition(String name, String description, String uuid, String scriptlerScriptId,
			ScriptParameter[] scriptParameters, Boolean remote) {
		super(name, description, uuid, remote);
		this.scriptlerScriptId = scriptlerScriptId;
		this.scriptParameters = scriptParameters;
	}

	/**
	 * Get Scriptler script id.
	 * 
	 * @return scriptler script id
	 */
	public final String getScriptlerScriptId() {
		return scriptlerScriptId;
	}

	/**
	 * Get script parameters.
	 * 
	 * @return array with script parameters
	 */
	public final ScriptParameter[] getScriptParameters() {
		return scriptParameters;
	}
	
	@Override
	protected ScriptCallback evaluateDefaultScript(
			Map<String, Object> parameters) {
		throw new NotImplementedException();
	}
	
	@Override
	protected ScriptCallback evaluateScript(Map<String, Object> parameters) {
		String scriptId = getScriptlerScriptId();
		Script script = ScriptHelper.getScript(scriptId, true);
		if (script == null) {
			throw new RuntimeException(String.format("No script with Scriplter ID '%s' exists", scriptId));
		}

		// Read all parameters from job configuration
		Map<String, Object> parametersMap = getParametersAsMap();

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

	/**
	 * Convert the list of parameters to a map.
	 * 
	 * @return a {@link Map} with script parameters
	 */
	protected Map<String, Object> getParametersAsMap() {
		ScriptParameter[] parameters = getScriptParameters();
		Map<String, Object> map = new HashMap<String, Object>(parameters.length);
		for (Parameter parameter : parameters) {
			map.put(parameter.getName(), parameter.getValue());
		}
		return map;
	}

	/**
	 * Script parameter which has a data bound constructor.
	 */
	public static final class ScriptParameter extends Parameter {
		private static final long serialVersionUID = 6199964387976523255L;

		@DataBoundConstructor
		public ScriptParameter(String name, String value) {
			super(name, value);
		}
	}
}
