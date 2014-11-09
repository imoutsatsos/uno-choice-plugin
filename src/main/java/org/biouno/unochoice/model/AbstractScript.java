package org.biouno.unochoice.model;

import hudson.DescriptorExtensionList;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

public abstract class AbstractScript implements Script, Describable<AbstractScript> {

	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = 4027103576278802323L;

	@SuppressWarnings("unchecked")
	public Descriptor<AbstractScript> getDescriptor() {
		return Jenkins.getInstance().getDescriptor(getClass());
	}

	public static <T, P> DescriptorExtensionList<AbstractScript, ScriptDescriptor> all() {
		return Jenkins.getInstance().<AbstractScript, ScriptDescriptor> getDescriptorList(AbstractScript.class);
	}
	
}
