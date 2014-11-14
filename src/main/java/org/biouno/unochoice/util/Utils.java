/*
 * The MIT License (MIT)
 * 
 * Copyright (c) <2014> <Ioannis Moutsatsos, Bruno P. Kinoshita>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.biouno.unochoice.util;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.scriptler.config.Script;
import org.jenkinsci.plugins.scriptler.config.ScriptlerConfiguration;

public class Utils {
	
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
