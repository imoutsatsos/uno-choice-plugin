package org.biouno.unochoice.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@PrepareForTest({ScriptHelper.class})
public class TestScriptlerScript {

	private List<ScriptlerScriptParameter> params = null;
	
	private Script script = null;
	
	@Before
	public void setUp() {
		params = new ArrayList<ScriptlerScriptParameter>();
		params.add(new ScriptlerScriptParameter("name1", "value1"));
		params.add(new ScriptlerScriptParameter("name2", "value2"));
		
		script = new Script("id", "name", "comment", true, "originCatalog", "originScript", "originDate", 
				true, (Parameter[]) params.toArray(new Parameter[0]), false);
		script.setScript("if(binding.variables.get('flag') == null) {return [1, 2, 3]} else {return [1000, 100, 1]}");
		
		PowerMockito.mockStatic(ScriptHelper.class);
		PowerMockito.when(ScriptHelper.getScript(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(this.script);
	}
	
	@Test
	public void testConstructors() {
		ScriptlerScript script = new ScriptlerScript("id", params);
		
		assertEquals("id", script.getScriptlerScriptId());
		assertEquals(2, script.getParameters().size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEvals() {
		ScriptlerScript script = new ScriptlerScript("id", params);
		
		List<Integer> returnValue = (List<Integer>) script.eval();
		assertEquals(Arrays.asList(new Integer[]{1,2,3}), returnValue);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("flag", "true");
		
		returnValue = (List<Integer>) script.eval(parameters);
		assertEquals(Arrays.asList(new Integer[]{1000, 100, 1}), returnValue);
	}
	
}
