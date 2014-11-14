package org.biouno.unochoice.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.jenkinsci.plugins.scriptler.config.Script;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class})
public class TestUtils {
	
	@Test
	public void testGetAllScriptlerScripts() {
		Set<Script> fakeScripts = new HashSet<Script>();
		fakeScripts.add(new Script("id", "name", "comment", true, "originCatalog", 
				"originScript", "originDate", true, null, true));
		PowerMockito.mockStatic(Utils.class);
		PowerMockito.when(Utils.getAllScriptlerScripts()).thenReturn(fakeScripts);
		Set<Script> scripts = Utils.getAllScriptlerScripts();
		assertTrue(scripts.size() == 1);
	}

	@Test
	public void testIsSelected() {
		assertTrue(Utils.isSelected("a:selected"));
		assertFalse(Utils.isSelected(null));
		assertFalse(Utils.isSelected(""));
		assertFalse(Utils.isSelected("a"));
	}
	
	@Test
	public void testEscapeSelected() {
		String escaped = Utils.escapeSelected(null);
		assertEquals("", escaped);
		
		escaped = Utils.escapeSelected("");
		assertEquals("", escaped);
		
		escaped = Utils.escapeSelected("a:selected");
		assertEquals("a", escaped);
		
		escaped = Utils.escapeSelected("a");
		assertEquals("a", escaped);
	}
	
	@Test
	public void testRandomParameterName() {
		String paramName = Utils.createRandomParameterName("test", "param");
		assertNotNull(paramName);
		assertTrue(paramName.startsWith("test"));
		assertTrue(paramName.endsWith("param"));
		
		paramName = Utils.createRandomParameterName(null, "param");
		assertNotNull(paramName);
		assertFalse(paramName.startsWith("test"));
		assertTrue(paramName.endsWith("param"));
		
		paramName = Utils.createRandomParameterName("test", null);
		assertNotNull(paramName);
		assertTrue(paramName.startsWith("test"));
		assertFalse(paramName.endsWith("param"));
	}
	
}
