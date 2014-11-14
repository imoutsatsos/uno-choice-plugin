package org.biouno.unochoice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.biouno.unochoice.model.GroovyScript;
import org.junit.Test;

public class TestCascadeChoiceParameter {

	@Test
	public void testConstructor() {
		GroovyScript script = new GroovyScript("return ['a', 'b']", "return ['EMPTY!']");
		CascadeChoiceParameter param = new CascadeChoiceParameter(
			"param000", "description", 
			script, CascadeChoiceParameter.ELEMENT_TYPE_FORMATTED_HIDDEN_HTML, 
			"param001, param002", true);
		
		assertEquals("param000", param.getName());
		assertEquals("description", param.getDescription());
		assertEquals(script, param.getScript());
		assertEquals("ET_FORMATTED_HIDDEN_HTML", param.getChoiceType());
		assertEquals("param001, param002", param.getReferencedParameters());
		assertTrue(param.getFilterable());
	}
	
	@Test
	public void testParameters() {
		GroovyScript script = new GroovyScript("return ['a', 'b']", "return ['EMPTY!']");
		CascadeChoiceParameter param = new CascadeChoiceParameter(
			"param000", "description", 
			script, CascadeChoiceParameter.ELEMENT_TYPE_FORMATTED_HIDDEN_HTML, 
			"param001, param002", true);
		assertTrue(param.getParameters().isEmpty());
		
		param.doUpdate("param001=A__LESEP__param002=B__LESEP__param003=");
		
		Map<String, String> expected = new LinkedHashMap<String, String>();
		expected.put("param001", "A");
		expected.put("param002", "B");
		expected.put("param003", "");
		assertEquals(expected, param.getParameters());
		
		param.doUpdate("");
		expected.clear();
		assertEquals(expected, param.getParameters());
		
		assertEquals(Arrays.asList("param001", "param002"), Arrays.asList(param.getReferencedParametersAsArray()));
	}
	
}
