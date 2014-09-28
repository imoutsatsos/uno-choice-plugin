package org.biouno.unochoice.scriptler;

import hudson.Extension;

import java.util.Set;

import org.biouno.unochoice.util.JenkinsUtils;
import org.jenkinsci.plugins.scriptler.config.Script;

public class ScriptlerChoiceParameter extends AbstractScriptlerParameter {

	/**
	 * Choice type.
	 */
	private final String choiceType;
	
	/**
	 * Filter flag.
	 */
	private final Boolean filterable;
	
	protected ScriptlerChoiceParameter(String name, String description, String scriptlerScriptId,
			ScriptlerScriptParameter[] scriptParameters, String choiceType, Boolean filterable) {
		super(name, description, scriptlerScriptId, scriptParameters);
		this.choiceType = choiceType;
		this.filterable = filterable;
	}
	
	@Override
	public String getChoiceType() {
		return this.choiceType;
	}
	
	public Boolean getFilterable() {
		return filterable;
	}
	
	// ---descriptor
	@Extension
	public static final class DescriptImpl extends ParameterDescriptor {
		
		@Override
		public String getDisplayName() {
			return "Uno-Choice Dynamic Choice Parameter (Scriptler)";
		}
		
		public Set<Script> getScripts() {
			return JenkinsUtils.getAllScriptlerScripts();
		}
		
	}

}
