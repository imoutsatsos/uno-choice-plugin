/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Ioannis Moutsatsos, Bruno P. Kinoshita
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

package org.biouno.unochoice.issue49260;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.biouno.unochoice.CascadeChoiceParameter;
import org.biouno.unochoice.ChoiceParameter;
import org.biouno.unochoice.model.GroovyScript;
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.SecureGroovyScript;
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval;
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

import org.jvnet.hudson.test.MockFolder;
import org.powermock.api.mockito.PowerMockito;
import hudson.model.FreeStyleProject;
import hudson.model.ParametersDefinitionProperty;

import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.runner.RunWith;
import hudson.model.AbstractItem;

/**
 * Tests for different folders having same Project name. See JENKINS-49260.
 *
 * @since 2.2
 */
@Issue("JENKINS-49260")
@RunWith(PowerMockRunner.class)
@PrepareForTest({StaplerRequest.class, Stapler.class})
@PowerMockIgnore({"javax.crypto.*" })
public class TestProjectDifferenceAcrossFolders {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    // LIST script
    private final String SCRIPT_LIST = "return ['Test', jenkinsProject.getName(), jenkinsProject.getFullName()]";
    private final String FALLBACK_SCRIPT_LIST = "return ['EMPTY!']";

    private final String PARAMETER_NAME = "my-parameter-name";

    private final String PROJECT_NAME = "MyJenkinsJob";
    private final String FOLDER_NAME_A = "AAA";
    private final String FOLDER_NAME_B = "BBB";

    @Before
    public void setUp() throws Exception {
        ScriptApproval.get()
                .preapprove(SCRIPT_LIST, GroovyLanguage.get());
        ScriptApproval.get()
                .preapprove(FALLBACK_SCRIPT_LIST, GroovyLanguage.get());
    }

    @Test
    public void testProjectAreDifferent() throws IOException {
        MockFolder folderA = j.createFolder(FOLDER_NAME_A);
        MockFolder folderB = j.createFolder(FOLDER_NAME_B);

        FreeStyleProject projectA = folderA.createProject(FreeStyleProject.class, PROJECT_NAME);
        FreeStyleProject projectB = folderB.createProject(FreeStyleProject.class, PROJECT_NAME);

        GroovyScript listScript = new GroovyScript(new SecureGroovyScript(SCRIPT_LIST, Boolean.FALSE, null),
        new SecureGroovyScript(FALLBACK_SCRIPT_LIST, Boolean.FALSE, null));
        
        PowerMockito.mockStatic(Stapler.class);

        StaplerRequest requestA = PowerMockito.mock(StaplerRequest.class);
        Ancestor ancestorA = PowerMockito.mock(Ancestor.class);
        PowerMockito.when(Stapler.getCurrentRequest()).thenReturn(requestA);
        PowerMockito.when(requestA.findAncestor(AbstractItem.class)).thenReturn(ancestorA);
        PowerMockito.when(ancestorA.getObject()).thenReturn(projectA);
        ChoiceParameter listParamA = new ChoiceParameter(PARAMETER_NAME, "description...", "random-name-A", listScript,
                CascadeChoiceParameter.PARAMETER_TYPE_SINGLE_SELECT, false, 1);

        StaplerRequest requestB = PowerMockito.mock(StaplerRequest.class);
        Ancestor ancestorB = PowerMockito.mock(Ancestor.class);
        PowerMockito.when(Stapler.getCurrentRequest()).thenReturn(requestB);
        PowerMockito.when(requestB.findAncestor(AbstractItem.class)).thenReturn(ancestorB);
        PowerMockito.when(ancestorB.getObject()).thenReturn(projectB);
        ChoiceParameter listParamB = new ChoiceParameter(PARAMETER_NAME, "description...", "random-name-B", listScript,
                CascadeChoiceParameter.PARAMETER_TYPE_SINGLE_SELECT, false, 1);
        
        ParametersDefinitionProperty paramsDefA = new ParametersDefinitionProperty(listParamA);
        ParametersDefinitionProperty paramsDefB = new ParametersDefinitionProperty(listParamB);

        projectA.addProperty(paramsDefA);
        projectB.addProperty(paramsDefB);
        
        Map<Object, Object> listSelectionValueA = listParamA.getChoices();
        Map<Object, Object> listSelectionValueB = listParamB.getChoices();

        // keys and values have the same content when the parameter returns an array...
        assertTrue("Missing project name for A!", listSelectionValueA.containsKey(PROJECT_NAME));
        assertTrue("Missing project name for B!", listSelectionValueB.containsKey(PROJECT_NAME));

        // Now, check full name!
        assertTrue("Missing project full name for A!", listSelectionValueA.containsKey(FOLDER_NAME_A+"/"+PROJECT_NAME));
        assertTrue("Missing project full name for B!", listSelectionValueB.containsKey(FOLDER_NAME_B+"/"+PROJECT_NAME));
    }
}