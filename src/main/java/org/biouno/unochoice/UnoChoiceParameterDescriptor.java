package org.biouno.unochoice;

import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.ParameterDefinition.ParameterDescriptor;

import java.util.LinkedList;
import java.util.List;

import org.biouno.unochoice.model.AbstractScript;

public class UnoChoiceParameterDescriptor extends ParameterDescriptor {

	public List<Descriptor<? extends AbstractScript>> getApplicableResultSeekers(AbstractProject<?, ?> p) {
        List<Descriptor<? extends AbstractScript>> list = new LinkedList<Descriptor<? extends AbstractScript>>();
        for (Descriptor<? extends AbstractScript> rs : AbstractScript.all()) {
            list.add(rs);
        }
        return list;
    }
	
}
