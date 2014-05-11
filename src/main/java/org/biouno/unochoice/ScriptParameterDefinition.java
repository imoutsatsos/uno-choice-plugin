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

import java.util.Map;


/**
 * Base class for dynamic script parameters.
 */
public class ScriptParameterDefinition extends BaseParameterDefinition {

	private static final long serialVersionUID = 2616675552276392152L;
	
	private final String script;
	
	protected ScriptParameterDefinition(String name, String description, String uuid, Boolean remote, String script) {
		super(name, description, uuid, remote);
		this.script = script;
	}
	
	public String getScript() {
		return script;
	}

	@Override
	protected ScriptCallback evaluateScript(Map<String, Object> parameters) {
		final String script = getScript();
		ScriptCallback callback = new ScriptCallback(script, parameters);
		return callback;
	}
	
}
