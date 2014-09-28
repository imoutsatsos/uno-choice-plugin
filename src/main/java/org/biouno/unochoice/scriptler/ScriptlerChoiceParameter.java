package org.biouno.unochoice.scriptler;

import hudson.Extension;

import java.util.Set;

import org.biouno.unochoice.util.Utils;
import org.jenkinsci.plugins.scriptler.config.Script;

/**
 * A parameter that renderss its options as a choice (select) HTML component, using a
 * Scriptler parameter that returns a list or a map.
 *
 * @author Bruno P. Kinoshita
 */
public class ScriptlerChoiceParameter extends AbstractScriptlerParameter {

	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = 6416418319327891747L;

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
	
	/**
	 * @return the filter flag
	 */
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
			return Utils.getAllScriptlerScripts();
		}
		
	}

}
