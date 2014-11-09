package org.biouno.unochoice.model;

import java.util.Map;

import org.jenkinsci.plugins.scriptler.config.Script;
import org.jenkinsci.plugins.scriptler.util.ScriptHelper;

public class ScriptlerScript extends AbstractScript {

	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = -4833367326600112145L;

	private final String scriptlerId;
	
	private final Map<String, String> parameters;

	public ScriptlerScript(String scriptlerId, Map<String, String> parameters) {
		super();
		this.scriptlerId = scriptlerId;
		this.parameters = parameters;
	}

	/**
	 * @return the scriptlerId
	 */
	public String getScriptlerId() {
		return scriptlerId;
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
		return this.toGroovyScript().eval(this.getParameters());
	}
	
	// --- utility methods for conversion
	
	public GroovyScript toGroovyScript() {
		final Script scriptler   = ScriptHelper.getScript(getScriptlerId(), true);
		return new GroovyScript(scriptler.script, null);
	}
	
}
