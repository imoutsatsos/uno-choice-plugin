package org.biouno.unochoice.util;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.scriptler.config.Script;
import org.jenkinsci.plugins.scriptler.config.ScriptlerConfiguration;

public final class Utils {
	
	private Utils() {} // hidden constructor
	
	public static Set<Script> getAllScriptlerScripts() {
		final Set<Script> scripts = ScriptlerConfiguration.getConfiguration().getScripts();
	    return scripts;
	}
	
	// --- methods called from Jelly pages
	
	public static boolean isSelected(Object obj) {
		if (obj == null)
			return false;
		final String text = obj.toString();
		return StringUtils.isNotBlank(text) && text.endsWith(":selected");
	}
	  
	public static String escapeSelected(Object obj) {
		if (obj == null)
			return "";
		final String text = obj.toString();
		if (StringUtils.isBlank(text))
			return "";
		if (isSelected(text))
			return text.substring(0, text.indexOf(":selected"));
		return text;
	}
	
	public static String createRandomParameterName(String prefix, String suffix) {
		String paramName = "";
		if (StringUtils.isNotBlank(prefix))
			paramName = prefix + "-";
		paramName += System.nanoTime();
		if (StringUtils.isNotBlank(suffix))
			paramName = paramName + "-" + suffix;
		return paramName;
	}
	
}
