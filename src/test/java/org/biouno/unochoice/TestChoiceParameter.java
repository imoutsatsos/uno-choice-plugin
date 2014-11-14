package org.biouno.unochoice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.biouno.unochoice.model.GroovyScript;
import org.junit.Test;

public class TestChoiceParameter {

	@Test
	public void testConstructor() {
		GroovyScript script = new GroovyScript("return ['a', 'b']", "return ['EMPTY!']");
		ChoiceParameter param = new ChoiceParameter(
			"param000", "description", 
			script, CascadeChoiceParameter.ELEMENT_TYPE_FORMATTED_HIDDEN_HTML, true);
		
		assertEquals("param000", param.getName());
		assertEquals("description", param.getDescription());
		assertEquals(script, param.getScript());
		assertEquals("ET_FORMATTED_HIDDEN_HTML", param.getChoiceType());
		assertTrue(param.getFilterable());
	}
}
