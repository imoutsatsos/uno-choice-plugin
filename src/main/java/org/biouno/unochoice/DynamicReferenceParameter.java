package org.biouno.unochoice;

import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.util.FormValidation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jenkins.model.ArtifactManager;
import jenkins.util.VirtualFile;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
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
 * @since 0.1
 */
public class DynamicReferenceParameter extends ScriptParameterDefinition {

	private static final long serialVersionUID = -8114576083660466882L;
	
	/*
	 * Valid parameter values.
	 */
	public static final String ELEMENT_TYPE_TEXT_BOX = "ET_TEXT_BOX"; // default choice type
	public static final String ELEMENT_TYPE_ORDERED_LIST = "ET_ORDERED_LIST";
	public static final String ELEMENT_TYPE_UNORDERED_LIST = "ET_UNORDERED_LIST";
	public static final String ELEMENT_TYPE_FORMATTED_HTML = "ET_FORMATTED_HTML";
	public static final String ELEMENT_TYPE_FORMATTED_HIDDEN_HTML = "ET_FORMATTED_HIDDEN_HTML";
	public static final String ELEMENT_TYPE_IMAGE_GALLERY = "ET_IMAGE_GALLERY";
	
	private final String elementType;
	private final String includes;
	private final String referencedParameters;
	
	private Map<String, Object> parameters = new HashMap<String, Object>();

	@DataBoundConstructor
	public DynamicReferenceParameter(String name, String description, String uuid, Boolean remote, String script, String defaultScript, String elementType, String referencedParameters, String includes) {
		super(name, description, uuid, remote, script, defaultScript);
		this.elementType = elementType;
		this.referencedParameters = referencedParameters;
		this.includes = includes;
	}
	
	@Deprecated
	public DynamicReferenceParameter(String name, String description, String uuid, Boolean remote, String script, String defaultScript, String elementType, String referencedParameters, String includes, Boolean hidden) {
		super(name, description, uuid, remote, script, defaultScript);
		this.elementType = elementType;
		this.referencedParameters = referencedParameters;
		this.includes = includes;
	}
	
	/**
	 * Type of element displayed.
	 */
	@JavaScriptMethod
	public String getElementType() {
		return elementType;
	}
	
	/**
	 * Pattern to include elements.
	 */
	public String getIncludes() {
		return includes;
	}
	
	/**
	 * Comma separated referenced parameters.
	 */
	public String getReferencedParameters() {
		return referencedParameters;
	}
	
	/*
	 * (non-Javadoc)
	 * @see hudson.model.ParameterDefinition#getName()
	 */
	@Override
	@JavaScriptMethod
	public String getName() {
		return super.getName();
	}
	
	public String[] getReferencedParamatersAsArray() {
		String[] arr = getReferencedParameters().split(",");
		String[] r = new String[arr.length];
		for (int i = 0; i< arr.length ; ++i) {
			r[i] = arr[i].trim();
		}
		return r;
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	@Override
	public ParameterValue getDefaultParameterValue() {
		return new StringParameterValue(getName(), "");
	}
	
	@Override
	public ParameterValue createValue(String value) {
		ParameterValue parameterValue = null;
		try {
			parameterValue = super.createValue(value);
		} catch (RuntimeException e) {
			parameterValue = new StringParameterValue(getName(), "");
		}
		return parameterValue;
	}
	
	@Override
	public ParameterValue createValue(StaplerRequest request, JSONObject json) {
		ParameterValue parameterValue = null;
		try {
			parameterValue = super.createValue(request, json);
		} catch (RuntimeException e) {
			parameterValue = new StringParameterValue(getName(), "");
		}
		return parameterValue;
	}
	
	/**
	 * Gets each artifact, and the project as parameters to the groovy script.
	 */
	@JavaScriptMethod
	public void doUpdate(String stringParameters) {
		parameters.clear();
		
		AbstractProject<?, ?> project = ((DescriptorImpl) getDescriptor()).getProject();
		if (project != null) {
			parameters.put("jenkinsProject", project);
			AbstractBuild<?, ?> build = project.getLastBuild();
			if (build != null && build.getHasArtifacts()) {
				parameters.put("jenkinsBuild", build);
				if (StringUtils.isNotBlank(getIncludes())) {
					List<String> parameterArtifacts = new ArrayList<String>();
					ArtifactManager artifactsManager = build.getArtifactManager();
					final VirtualFile artifactsDir = artifactsManager.root();
					try {
						final String[] artifacts = artifactsDir.list(getIncludes());
						for (String artifact : artifacts) {
							parameterArtifacts.add(artifact);
						}
						parameters.put("artifacts", parameterArtifacts);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		String[] params = stringParameters.split("__LESEP__");
		for (String param : params) {
			String[] nameValue = param.split("=");
			String name = nameValue[0];
			String value = nameValue[1];
			getParameters().put(name, value);
		}
	}
	
	@JavaScriptMethod
	public List<Object> getScriptResultAsList() {
		return super.getScriptResultAsList(getParameters());
	}
	
	@JavaScriptMethod
	public String getScriptResultAsString() {
		return super.getScriptResultAsString(getParameters());
	}
	
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
			return "Uno-Choice Dynamic Reference Parameter";
		}
		
		public FormValidation doCheckRequired(@QueryParameter String value) {
			value = StringUtils.strip(value);
			if (value == null || value.equals("")) {
                return FormValidation.error("This field is required.");
            }
            return FormValidation.ok();
		}
		
	}

}
