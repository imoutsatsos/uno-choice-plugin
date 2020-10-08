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

package org.biouno.unochoice.util;

import java.io.IOException;
import java.io.Writer;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlSanitizer;
import org.owasp.html.HtmlStreamRenderer;
import org.owasp.html.PolicyFactory;

import com.google.common.base.Throwables;

import hudson.markup.BasicPolicy;
import hudson.markup.MarkupFormatter;

/**
 * A markup formatter used by the plug-in (only, not available in other parts
 * of Jenkins) that allows HTML tags such as &lt;input&gt;.
 *
 * @since 2.4
 */
public class SafeHtmlExtendedMarkupFormatter extends MarkupFormatter {

    public static final SafeHtmlExtendedMarkupFormatter INSTANCE = new SafeHtmlExtendedMarkupFormatter();

    /**
     * {@link BasicPolicy#POLICY_DEFINITION} is the policy used by {@link hudson.markup.RawHtmlMarkupFormatter}.
     * We start from that secure policy and then extend it to include required elements for this plugin.
     */
    private static final PolicyFactory POLICY = BasicPolicy.POLICY_DEFINITION.and(new HtmlPolicyBuilder()
        .allowElements("input", "textarea", "select", "option")
        .allowAttributes("id", "class", "style", "type", "name", "value", "placeholder", "disabled", "checked", "max", "maxlength", "min", "minlength", "multiple", "pattern", "readonly", "step").onElements("input")
        .allowAttributes("id", "class", "style", "maxlength", "name", "placeholder", "disabled", "readonly", "wrap", "rows", "cols").onElements("textarea")
        .allowAttributes("id", "class", "style", "disabled", "multiple", "name", "required", "size").onElements("select")
        .allowAttributes("id", "class", "style", "disabled", "label", "selected", "value").onElements("option")
        .toFactory());

    /**
     * Copied from {@link hudson.markup.RawHtmlMarkupFormatter#translate(String, Writer)}
     */
    @Override
    public void translate(String markup, Writer output) throws IOException {
        HtmlStreamRenderer renderer = HtmlStreamRenderer.create(
            output,
            // Receives notifications on a failure to write to the output.
            Throwables::propagate, // System.out suppresses IOExceptions
            // Our HTML parser is very lenient, but this receives notifications on
            // truly bizarre inputs.
            x -> {
                throw new Error(x);
            }
        );
        HtmlSanitizer.sanitize(markup, POLICY.apply(renderer));
    }
}
