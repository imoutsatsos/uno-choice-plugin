package org.biouno.unochoice.util;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.scriptler.config.Script;
import org.jenkinsci.plugins.scriptler.config.ScriptlerConfiguration;

public final class Utils {
	
	private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

	private Utils() {} // hidden constructor
	
	public static Set<Script> getAllScriptlerScripts() {
		final Set<Script> scripts = ScriptlerConfiguration.getConfiguration().getScripts();
	    return scripts;
	}
	
	/**
	 * Executes the script with the given parameters.
	 * 
	 * @param callback callable script
	 * @param fallback fallback script
	 * @param parameters parameters
	 * @return script eval'd
	 * @throws Exception iff the fallback script fails
	 */
	public static Object executeScript(ScriptCallback<?, ?> callback, ScriptCallback<?, ?> fallback, Map<Object, Object> parameters) throws Throwable {
		try {
			return callback.call();
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, String.format("Error executing script for dynamic parameter '%s'", callback.getName()), e);
			if (fallback != null) {
				try {
					LOGGER.log(Level.WARNING, "Fallback to default script...");
					return fallback.call();
				} catch (Throwable e1) {
					LOGGER.log(Level.SEVERE, String.format("Error executing fallback script for dynamic parameter '%s'", fallback.getName()), e);
					throw e1;
				}
			}
			LOGGER.log(Level.WARNING, String.format("No fallback script configured for '%s'", callback.getName()));
			throw e;
		}
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
