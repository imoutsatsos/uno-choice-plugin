package org.biouno.unochoice.issue28764;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.biouno.unochoice.model.ScriptlerScript;
import org.biouno.unochoice.model.ScriptlerScriptParameter;
import org.biouno.unochoice.util.Utils;
import org.jenkinsci.plugins.scriptler.config.Parameter;
import org.jenkinsci.plugins.scriptler.config.Script;
import org.jenkinsci.plugins.scriptler.util.ScriptHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ScriptHelper.class, Utils.class })
public class TestIssue28764 {

    private List<ScriptlerScriptParameter> params = null;

    private Script script = null;
    
    private ScriptlerScript scriptlerScript = null;

    private Map<String, String> myEnv = null;
    
    @Before
    public void setUp() {
        params = new ArrayList<ScriptlerScriptParameter>();

        script = new Script("id", "name", "comment", true, "originCatalog",
                "originScript", "originDate", true,
                (Parameter[]) params.toArray(new Parameter[0]), false);
        script.setScript("if(binding.variables.get('flag') == null) {return [1, 2, 3]} else {return [1000, 100, 1]}");

        myEnv = new HashMap<String, String>();
        myEnv.put("flag", "myflag");
        
        scriptlerScript = new ScriptlerScript("id", params);
        
        // Mock
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getSystemEnv()).thenReturn(this.myEnv);
        
        PowerMockito.mockStatic(ScriptHelper.class);
        PowerMockito.when(
                ScriptHelper.getScript(Mockito.anyString(),
                        Mockito.anyBoolean())).thenReturn(this.script);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEnvVarsExpanding() {
        List<Integer> returnValue = (List<Integer>) scriptlerScript.eval();
        assertEquals(Arrays.asList(new Integer[] { 1000, 100, 1 }), returnValue);
    }

}
