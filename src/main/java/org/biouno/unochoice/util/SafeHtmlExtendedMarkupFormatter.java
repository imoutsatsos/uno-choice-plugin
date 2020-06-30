package org.biouno.unochoice.util;

import com.google.common.base.Throwables;
import hudson.markup.BasicPolicy;
import hudson.markup.MarkupFormatter;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlSanitizer;
import org.owasp.html.HtmlStreamRenderer;
import org.owasp.html.PolicyFactory;

import java.io.IOException;
import java.io.Writer;

public class SafeHtmlExtendedMarkupFormatter extends MarkupFormatter {

    public static SafeHtmlExtendedMarkupFormatter INSTANCE = new SafeHtmlExtendedMarkupFormatter();

    /**
     * {@link BasicPolicy#POLICY_DEFINITION} is the policy used by {@link hudson.markup.RawHtmlMarkupFormatter}.
     * We start from that secure policy and then extend it to include required elements for this plugin.
     */
    private static final PolicyFactory POLICY = BasicPolicy.POLICY_DEFINITION.and(new HtmlPolicyBuilder()
        .allowElements("input", "textarea")
        .allowAttributes("id", "type", "name", "value", "placeholder", "disabled", "checked", "max", "maxlength", "min", "minlength", "multiple", "pattern", "readonly", "step").onElements("input")
        .allowAttributes("id", "maxlength", "name", "placeholder", "disabled", "readonly", "wrap", "rows", "cols").onElements("textarea")
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
