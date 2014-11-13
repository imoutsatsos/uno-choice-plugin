package org.biouno.unochoice.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jenkinsci.plugins.scriptler.util.ScriptHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ScriptHelper.class})
public class TestGroovyScript {

	@Before
	public void setUp() {
	}
	
	@Test
	public void testConstructors() {
		GroovyScript script = new GroovyScript("return ['a', 'b']", "return ['EMPTY!']");
		
		assertEquals("return ['a', 'b']", script.getScript());
		assertEquals("return ['EMPTY!']", script.getFallbackScript());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEvals() {
		GroovyScript script = new GroovyScript("return [flag, 'b']", "return ['EMPTY!']");
		
		List<String> returnValue = (List<String>) script.eval();
		assertEquals(Arrays.asList(new String[]{"EMPTY!"}), returnValue);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("flag", "true");
		
		returnValue = (List<String>) script.eval(parameters);
		assertEquals(Arrays.asList(new String[]{"true", "b"}), returnValue);
	}
	
	@Test
	public void testEquals() {
		GroovyScript script1 = new GroovyScript("return [flag, 'b']", "return ['EMPTY!']");
		GroovyScript script2 = new GroovyScript("return [flag, 'b']", "return ['EMPTY!']");
		assertEquals(script1, script2);
		
		GroovyScript script3 = new GroovyScript("return [flag, 'c', 'd']", "return ['EMPTY!']");
		assertNotEquals(script1, script3);
	}
	
}
