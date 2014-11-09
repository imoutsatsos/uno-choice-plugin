package org.biouno.unochoice.model;

import org.jenkinsci.plugins.scriptler.config.Parameter;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Script parameter which has a data bound constructor.
 * 
 * @author dynamic-parameter-plugin
 * @since 0.20
 */
public class ScriptlerScriptParameter extends Parameter {
	
	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = 39604092433909771L;

	@DataBoundConstructor
	public ScriptlerScriptParameter(String name, String value) {
		super(name, value);
	}
}
