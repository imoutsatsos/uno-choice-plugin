package org.biouno.unochoice.issue62215;

import org.biouno.unochoice.model.GroovyScript;
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.SecureGroovyScript;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

@Issue("62215")
public class TestMarkupFormatterAllowsRequiredElementsInScriptOutput {

    @Rule
    public JenkinsRule j = new JenkinsRule();


    @Test
    public void testInputIsNotRemovedFromGroovySandboxOutput() {
        String markup = "<input type=\"text\" name=\"value\" value=\"value\" />";
        GroovyScript script = new GroovyScript(new SecureGroovyScript("return '" + markup + "'", true, null), null);
        String result = (String) script.eval();

        assertEquals(markup, result);
    }

    @Test
    public void testTextareaIsNotRemovedFromGroovySandboxOutput() {
        String markup = "<textarea name=\"value\" placeholder=\"test\"></textarea>";
        GroovyScript script = new GroovyScript(new SecureGroovyScript("return '" + markup + "'", true, null), null);
        String result = (String) script.eval();

        assertEquals(markup, result);
    }

    @Test
    public void testSelectIsNotRemovedFromGroovySandboxOutput() {
        String markup = "<select id=\"cars\"><option value=\"volvo\">Volvo</option><option value=\"saab\">Saab</option><option value=\"opel\">Opel</option><option value=\"audi\">Audi</option></select>";
        GroovyScript script = new GroovyScript(new SecureGroovyScript("return '" + markup + "'", true, null), null);
        String result = (String) script.eval();

        assertEquals(markup, result);
    }
}
