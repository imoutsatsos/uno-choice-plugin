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
import org.biouno.unochoice.groovy.DynamicReferenceParameter.DescriptorImpl;
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
 * <p>This kind of parameter is only for reference. An use case is when you have several
 * job parameters, but your input values may vary depending on previous executions. You 
 * can get the previous executions by accessing from your Groovy code the jenkinsProject
 * variable.</p>
 * 
 * <p>Its options are retrieved from the evaluation of a Scriptler script.</p>
 * 
 * @author Bruno P. Kinoshita
 * @since 0.1
 */
public class ScriptlerDynamicReferenceParameter extends AbstractScriptlerCascadableParameter {

	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = 4356548854837925398L;
	
	private static final String JENKINS_PROJECT_VARIABLE_NAME = "jenkinsProject";
	private static final String JENKINS_BUILD_VARIABLE_NAME = "jenkinsBuild";
	
	/**
	 * Choice type.
	 */
	private final String choiceType;
	
	@DataBoundConstructor
	public ScriptlerDynamicReferenceParameter(String name, String description, 
			String scriptlerScriptId, ScriptlerScriptParameter[] parameters, String choiceType, 
			String referencedParameters) {
		super(name, description, scriptlerScriptId, parameters, referencedParameters);
		this.choiceType = choiceType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.biouno.unochoice.AbstractUnoChoiceParameter#getChoiceType()
	 */
	@Override
	public String getChoiceType() {
		return this.choiceType;
	}

	/**
	 * Gets each artifact, and the project as parameters to the groovy script.
	 */
	@JavaScriptMethod
	public void doUpdate(String parameters) {
		super.doUpdate(parameters);
		
		final AbstractProject<?, ?> project = ((DescriptorImpl) getDescriptor()).getProject();
		if (project != null) {
			getParameters().put(JENKINS_PROJECT_VARIABLE_NAME, project);
			AbstractBuild<?, ?> build = project.getLastBuild();
			if (build != null && build.getHasArtifacts()) {
				getParameters().put(JENKINS_BUILD_VARIABLE_NAME, build);
			}
		}
	}
	
	@JavaScriptMethod
	public String getChoicesAsStringForUI() {
		String result = getChoicesAsString(getParameters());
		return result;
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
