package org.biouno.unochoice;

import static org.junit.Assert.assertArrayEquals;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.biouno.unochoice.model.GroovyScript;
import org.junit.Test;
import org.jvnet.hudson.test.Bug;

@Bug(26)
public class TestParametersOrder {

	@Test
	public void testParametersOrder() {
		Map<Object, Object> parameters = new LinkedHashMap<Object, Object>();
		parameters.put("D", "D");
		parameters.put("C", "C");
		parameters.put("B", "B");
		parameters.put("A", "A");
		
		ChoiceParameter parameter = new ChoiceParameter(
				"script001", "", new GroovyScript("return ['D', 'C', 'B', 'A']", null),
				ChoiceParameter.PARAMETER_TYPE_MULTI_SELECT, true);
		
		Map<Object, Object> result = parameter.getChoices(Collections.<Object, Object>emptyMap());
		assertArrayEquals(parameters.keySet().toArray(), result.keySet().toArray());
	}
	
}
