package org.biouno.unochoice.model;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.Extension;
import hudson.Util;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import jenkins.model.Jenkins;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

public class GroovyScript extends AbstractScript {
	
	/*
	 * Serial UID.
	 */
	private static final long serialVersionUID = -4886250205110550815L;

	private static final Logger LOGGER = Logger.getLogger(GroovyScript.class.getName());

	private final String script;
	
	@Nullable
	private final String fallbackScript;
	
	public GroovyScript(String script, String fallbackScript) {
		this.script = script;
		this.fallbackScript = fallbackScript;
	}

	/**
	 * @return the script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * @return the fallbackScript
	 */
	public String getFallbackScript() {
		return fallbackScript;
	}

	public Object eval() {
		return eval(Collections.<String, String>emptyMap());
	}

	public Object eval(Map<String, String> parameters) throws RuntimeException {
		ClassLoader cl = null;
		try {
			cl = Jenkins.getInstance().getPluginManager().uberClassLoader;
		} catch (Exception e) {
			LOGGER.finest(e.getMessage());
		}
		if (cl == null) {
			cl = Thread.currentThread().getContextClassLoader();
		}
		
		final Binding context = new Binding();
		
		// @SuppressWarnings("unchecked")
		final Map<String, String> envVars = System.getenv();
		for (Entry<String, String> parameter : parameters.entrySet()) {
			Object value = parameter.getValue();
			if (value != null && value instanceof String) {
				value = Util.replaceMacro((String) value, envVars);
				context.setVariable(parameter.getKey().toString(), value);
			}
		}
		
		final GroovyShell shell = new GroovyShell(cl, context, CompilerConfiguration.DEFAULT);
		try {
			return shell.evaluate(script);
		} catch (CompilationFailedException cfe1) {
			if (this.fallbackScript != null) {
				try {
					LOGGER.log(Level.WARNING, "Fallback to default script...", cfe1);
					return shell.evaluate(fallbackScript);
				} catch (CompilationFailedException cfe2) {
					LOGGER.log(Level.SEVERE, "Error executing fallback script", cfe2);
					throw cfe2;
				}
			} else {
				LOGGER.log(Level.WARNING, "No fallback script configured for '%s'");
				throw cfe1;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GroovyScript [script=" + script + ", fallbackScript="
				+ fallbackScript + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fallbackScript == null) ? 0 : fallbackScript.hashCode());
		result = prime * result + ((script == null) ? 0 : script.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroovyScript other = (GroovyScript) obj;
		if (fallbackScript == null) {
			if (other.fallbackScript != null)
				return false;
		} else if (!fallbackScript.equals(other.fallbackScript))
			return false;
		if (script == null) {
			if (other.script != null)
				return false;
		} else if (!script.equals(other.script))
			return false;
		return true;
	}
	
	@Extension
	public static class DescriptorImpl extends ScriptDescriptor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return "Groovy Script"; 
		}
	}

}
