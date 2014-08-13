package org.biouno.unochoice;

import static org.junit.Assert.assertArrayEquals;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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
		
		BaseParameterDefinition def = new ChoiceParameterDefinition(
				"script001", "", "", null, "return ['D', 'C', 'B', 'A']", "",
				ChoiceParameterDefinition.PARAMETER_TYPE_MULTI_SELECT, false);
		
		Map<Object, Object> result = def.getScriptResultAsMap(Collections.<String, Object>emptyMap());
		assertArrayEquals(parameters.keySet().toArray(), result.keySet().toArray());
	}
	
}
