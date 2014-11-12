package org.biouno.unochoice.model;

import hudson.Extension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jenkinsci.plugins.scriptler.config.Script;
import org.jenkinsci.plugins.scriptler.util.ScriptHelper;
import org.kohsuke.stapler.DataBoundConstructor;

public class ScriptlerScript extends AbstractScript {

	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = -4833367326600112145L;

	private final String scriptlerScriptId;
	
	private final Map<String, String> parameters;

	@DataBoundConstructor
	public ScriptlerScript(String scriptlerScriptId, List<ScriptlerScriptParameter> parameters) {
		super();
		this.scriptlerScriptId = scriptlerScriptId;
		this.parameters = new HashMap<String, String>();
		if (parameters != null) {
			for (ScriptlerScriptParameter parameter : parameters) {
				this.parameters.put(parameter.getName(), parameter.getValue());
			}
		}
	}
	
	/**
	 * @return the scriptlerScriptId
	 */
	public String getScriptlerScriptId() {
		return scriptlerScriptId;
	}

	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	public Object eval() {
		return eval(null);
	}

	public Object eval(Map<String, String> parameters) {
		if (parameters != null && parameters.size() > 0)
			getParameters().putAll(parameters);
		return this.toGroovyScript().eval(this.getParameters());
	}
	
	// --- utility methods for conversion
	
	public GroovyScript toGroovyScript() {
		final Script scriptler   = ScriptHelper.getScript(getScriptlerScriptId(), true);
		return new GroovyScript(scriptler.script, null);
	}
	
	// --- descriptor
	
	@Extension
	public static class DescriptorImpl extends ScriptDescriptor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return "Scriptler Script"; 
		}
		
	}
	
}
