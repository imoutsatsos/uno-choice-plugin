/*
 * The MIT License (MIT)
 *
 * Copyright (c) <2014-2015> <Ioannis Moutsatsos, Bruno P. Kinoshita>
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

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.scriptler.config.Script;
import org.jenkinsci.plugins.scriptler.config.ScriptlerConfiguration;

/**
 * Utility methods.
 *
 * @author Bruno P. Kinoshita
 * @since 0.23
 */
public class Utils {

    /**
     * Returns all scriptler scripts available.
     *
     * @return all scriptler scripts available in Jenkins
     */
    public static Set<Script> getAllScriptlerScripts() {
        final Set<Script> scripts = ScriptlerConfiguration.getConfiguration().getScripts();
        return scripts;
    }

    // --- methods called from Jelly pages

    /**
     * Checks whether a parameter value contains the :selected suffix, returning
     * {@code true} if it does, {@code false} otherwise.
     *
     * @param obj parameter value
     * @return {@code true} if the parameter name contains the :selected suffix {@code false} otherwise.
     */
    public static boolean isSelected(Object obj) {
        if (obj == null)
            return false;
        final String text = obj.toString();
        return StringUtils.isNotBlank(text) && text.endsWith(":selected");
    }

    /**
     * Escapes the parameter value, removing the :selected suffix.
     *
     * @param obj parameter value
     * @return escaped parameter value
     */
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

    /**
     * Creates a random parameter name.
     *
     * @param prefix parameter prefix
     * @param suffix parameter suffix
     * @return random parameter name
     */
    public static String createRandomParameterName(String prefix, String suffix) {
        String paramName = "";
        if (StringUtils.isNotBlank(prefix))
            paramName = prefix + "-";
        paramName += System.nanoTime();
        if (StringUtils.isNotBlank(suffix))
            paramName = paramName + "-" + suffix;
        return paramName;
    }

    /**
     * Helped method to return the system environment variables. The main advantage
     * over calling the System.getenv method directly, is that we can mock this call
     * (System is final).
     *
     * @return System environment variables as map
     */
    public static Map<String, String> getSystemEnv() {
        return System.getenv();
    }
}
