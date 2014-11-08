package org.biouno.unochoice.scriptler;

import hudson.Extension;

import java.util.Set;

import org.biouno.unochoice.groovy.AbstractCascadableParameter;
import org.biouno.unochoice.util.Utils;
import org.jenkinsci.plugins.scriptler.config.Script;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * <p>A choice parameter, that gets updated when another parameter changes. The simplest 
 * use case for this, would be having a list of states, and when the user selected a
 * state it would trigger an update of the city fields.</p>
 * 
 * <p>The state parameter would be a choice parameter, and the city parameter would be a
 * cascade choice parameter, that referenced the former.</p>
 * 
 * <p>Its options are retrieved from the evaluation of a Scriptler script.</p>
 * 
 * @author Bruno P. Kinoshita
 * @since 0.20
 */
public class ScriptlerCascadeChoiceParameter extends AbstractCascadableParameter {

	/* 
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1442341084915900761L;

	/**
	 * Choice type.
	 */
	private final String choiceType;
	
	/**
	 * Filter flag.
	 */
	private final Boolean filterable;
	
	/**
	 * Constructor called from Jelly with parameters.
	 * 
	 * @param name name
	 * @param description description
	 * @param scriptlerScriptId Scriptler script ID
	 * @param scriptParameters Scriptler script parameters
	 * @param choiceType choice type
	 * @param referencedParameters referenced parameters
	 * @param filterable filter flag
	 */
	@DataBoundConstructor
	public ScriptlerCascadeChoiceParameter(String name, String description, String scriptlerScriptId,
			ScriptlerScriptParameter[] scriptParameters, String choiceType, String referencedParameters,
			Boolean filterable) {
		super(name, description, scriptlerScriptId, /* fallback */null, referencedParameters);
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
	 * Gets the filter flag.
	 * 
	 * @return filter flag
	 */
	public Boolean getFilterable() {
		return filterable;
	}
	
	// ---descriptor
	
	@Extension
	public static final class DescriptImpl extends ParameterDescriptor {
		
		@Override
		public String getDisplayName() {
			return "Uno-Choice Cascade Dynamic Choice Parameter (Scriptler)";
		}
		
		public Set<Script> getScripts() {
			return Utils.getAllScriptlerScripts();
		}
		
	}
}
