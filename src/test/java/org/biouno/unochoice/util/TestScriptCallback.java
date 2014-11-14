package org.biouno.unochoice.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.biouno.unochoice.model.GroovyScript;
import org.biouno.unochoice.model.ScriptlerScriptParameter;
import org.junit.Test;

public class TestScriptCallback {

	@Test
	public void testScriptCallback() {
		List<ScriptlerScriptParameter> params = new ArrayList<ScriptlerScriptParameter>();
		params.add(new ScriptlerScriptParameter("name1", "value1"));
		params.add(new ScriptlerScriptParameter("name2", "value2"));
		GroovyScript script = new GroovyScript("return ['a', 'b']", "return ['EMPTY!']");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("flag", "true");
		ScriptCallback<Exception> sc = new ScriptCallback<Exception>(
			"callback1",
			script, 
			parameters
		);
		
		assertEquals("callback1", sc.getName());
		assertEquals(sc.getScript(), script);
	}
	
}
