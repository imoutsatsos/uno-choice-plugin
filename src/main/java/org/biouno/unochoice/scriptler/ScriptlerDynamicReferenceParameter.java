package org.biouno.unochoice.scriptler;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ParameterDefinition;
import hudson.util.FormValidation;

import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.biouno.unochoice.AbstractCascadableParameter;
import org.biouno.unochoice.util.Utils;
import org.jenkinsci.plugins.scriptler.config.Script;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * <p>Provides a <b>dynamic reference parameter</b> for users. This is a not so elegant
 * solution, since we are using a ParameterDefinition extension point, but it 
 * actually <b>doesn't provide any parameter value</b>.</p>
 * 
 * <p>This kind of parameter is only for reference. The dynamic in the name is due 
 * to its ability to <b>use past artifacts to display information for users before 
 * they submit a job</b>. An use case is when you have several job parameters, but 
 * your input values may vary depending on previous executions.</p>
 * 
 * <p>The artifacts of previous builds can be used to produce input text boxes, 
 * HTML (un) ordered lists, formatted HTML or simple image galleries.</p>
 * 
 * <p>
 * This parameter uses Scriptler. 
 * 
 * @since 0.1
 */
public class ScriptlerDynamicReferenceParameter extends AbstractCascadableParameter {

	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = 69655390867060982L;

	private final String elementType;
	
	@DataBoundConstructor
	public ScriptlerDynamicReferenceParameter(String name, String description, 
			String scriptlerScriptId, ScriptlerScriptParameter[] parameters, String elementType, String referencedParameters) {
		super(name, description, scriptlerScriptId, /* fallback */null, referencedParameters);
		this.elementType = elementType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.biouno.unochoice.AbstractUnoChoiceParameter#getChoiceType()
	 */
	@Override
	public String getChoiceType() {
		return this.elementType;
	}

	/**
	 * Gets each artifact, and the project as parameters to the groovy script.
	 */
	@JavaScriptMethod
	public void doUpdate(String parameters) {
		super.doUpdate(parameters);
		
		final AbstractProject<?, ?> project = ((DescriptorImpl) getDescriptor()).getProject();
		if (project != null) {
			getParameters().put("jenkinsProject", project);
			AbstractBuild<?, ?> build = project.getLastBuild();
			if (build != null && build.getHasArtifacts()) {
				getParameters().put("jenkinsBuild", build);
			}
		}
	}
	
	// --- descriptor
	
	@Extension
	public static final class DescriptorImpl extends ParameterDescriptor {
		
		private AbstractProject<?, ?> project;

		/*
		 * Used to store a reference to the Jenkins project related to this parameter.
		 * A bit hacky, probably using another extension point would be a good idea.
		 */
		@Override
		public ParameterDefinition newInstance(StaplerRequest req,
				JSONObject formData)
				throws hudson.model.Descriptor.FormException {
			List<Ancestor> ancestors = req.getAncestors();
			AbstractProject<?, ?> project = null;
			for (Ancestor ancestor : ancestors) {
				Object object = ancestor.getObject();
				if (object instanceof AbstractProject<?, ?>) {
					project = (AbstractProject<?, ?>) object;
					break;
				}
			}
			this.project = project;
			return super.newInstance(req, formData);
		}
		
		public AbstractProject<?, ?> getProject() {
			return project;
		}
		
		@Override
		public String getDisplayName() {
			return "Uno-Choice Dynamic Reference Parameter (Scriptler)";
		}
		
		public FormValidation doCheckRequired(@QueryParameter String value) {
			value = StringUtils.strip(value);
			if (value == null || value.equals("")) {
                return FormValidation.error("This field is required.");
            }
            return FormValidation.ok();
		}
		
		public Set<Script> getScripts() {
			return Utils.getAllScriptlerScripts();
		}
		
	}

}
