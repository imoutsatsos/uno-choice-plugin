/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Ioannis K. Moutsatsos
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.biouno.unochoice;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.Util;
import hudson.remoting.Callable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import jenkins.model.Jenkins;

import org.codehaus.groovy.control.CompilerConfiguration;

/**
 * A callable (Jenkins remoting API) object that executes the script locally (when executed in the master)
 * or remotely. 
 */
public class ScriptCallback implements Callable<Object, Throwable> {
	
	private static final long serialVersionUID = 4524316203276099968L;
	
	private static final Logger LOGGER = Logger.getLogger(ScriptCallback.class.getName());
	
	private final String script;
	private Map<String, Object> parameters;

	public ScriptCallback(String script, Map<String, Object> parameters) {
		this.script = script;
		this.parameters = parameters;
	}
	
	public Object call() throws Throwable {
		// we can add class paths here too if needed
		ClassLoader cl = null;
		try {
			cl = Jenkins.getInstance().getPluginManager().uberClassLoader;
		} catch (Throwable t) {
			LOGGER.finest(t.getMessage());
		}
		if (cl == null) {
			cl = Thread.currentThread().getContextClassLoader();
		}
		
		final Binding context = new Binding();
		
		// @SuppressWarnings("unchecked")
		Map<String, String> envVars = System.getenv();
		for (Entry<String, Object> parameter : parameters.entrySet()) {
			Object value = parameter.getValue();
			if (value != null && value instanceof String) {
				value = Util.replaceMacro((String) value, envVars);
				context.setVariable(parameter.getKey(), value);
			}
		}
		
		GroovyShell shell = new GroovyShell(cl, context, CompilerConfiguration.DEFAULT);
		Object eval = shell.evaluate(script);
		return eval;
	}
	
}
