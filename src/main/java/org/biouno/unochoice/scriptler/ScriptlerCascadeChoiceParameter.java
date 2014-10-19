package org.biouno.unochoice.scriptler;

import hudson.Extension;

import java.util.Set;

import org.biouno.unochoice.AbstractCascadableParameter;
import org.biouno.unochoice.util.Utils;
import org.jenkinsci.plugins.scriptler.config.Script;

/**
 * <p>A cascade choice parameter. It receives a list of parameters to watch. In case any 
 * of these parameters change, its doUpdate(params:String) gets called, with a list 
 * of parameters and values separated by comma. For instance: "a=1,b=2,c=3".</p>
 * 
 * <p>When any of the referenced parameters change, this parameter uses Scriptler to
 * generate a new list of parameters.</p> 
 * 
 * @author Bruno P. Kinoshita
 */
public class ScriptlerCascadeChoiceParameter extends AbstractCascadableParameter {

	/* 
	 * Serial UID.
	 */
	private static final long serialVersionUID = 3375455239145048367L;

	/**
	 * Choice type.
	 */
	private final String choiceType;
	
	/**
	 * Filter flag.
	 */
	private final Boolean filterable;
	
	protected ScriptlerCascadeChoiceParameter(String name, String description, String scriptlerScriptId,
			ScriptlerScriptParameter[] scriptParameters, String choiceType, Boolean filterable, 
			String referencedParameters) {
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
