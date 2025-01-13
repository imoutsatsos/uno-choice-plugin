/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2020 Ioannis Moutsatsos, Bruno P. Kinoshita
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

package org.biouno.unochoice.model;

import hudson.DescriptorExtensionList;
import hudson.model.Describable;
import java.io.Serializable;
import java.util.Map;
import jenkins.model.Jenkins;

/**
 * Interface for scripts.
 *
 * @author Bruno P. Kinoshita
 * @since 0.23
 */
public interface Script extends Serializable, Describable<Script> {

    /**
     * Evaluates the script.
     *
     * @return output of the script
     */
    Object eval();

    /**
     * Evaluates the script using the given parameters binding parameters.
     *
     * @param parameters binding parameters
     * @return output of the script
     */
    Object eval(Map<String, String> parameters);

    static DescriptorExtensionList<Script, ScriptDescriptor> all() {
        final Jenkins instance = Jenkins.getInstanceOrNull();
        DescriptorExtensionList<Script, ScriptDescriptor> all = null;
        if (instance != null) {
            all = instance.getDescriptorList(Script.class);
        }
        return all;
    }

}
