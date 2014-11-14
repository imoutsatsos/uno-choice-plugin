package org.biouno.unochoice;

import static org.junit.Assert.assertEquals;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import net.sf.json.JSONObject;

import org.biouno.unochoice.model.GroovyScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.stapler.StaplerRequest;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TestAbstractUnoChoiceParameter {

	@Test
	public void testCreateValue() {
		GroovyScript script = new GroovyScript("return ['a', 'b']", "return ['EMPTY!']");
		ChoiceParameter param = new ChoiceParameter("name", "description", script, "choiceType", true);
		ParameterValue value = param.createValue("value");
		
		assertEquals("value", value.getValue().toString());
		
		JSONObject json = new JSONObject();
		json.put("name", "name");
		json.put("value", "value");
		
		StaplerRequest request = PowerMockito.mock(StaplerRequest.class);
		PowerMockito.when(request.bindJSON(StringParameterValue.class, json)).thenReturn((StringParameterValue) value);
		
		value = param.createValue(request, json);
		
		assertEquals("value", value.getValue().toString());
	}
	
	
	
}
