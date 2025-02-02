package site.gutschi.humble.spring.main.ui;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeNameAttributeTagProcessor extends AbstractAttributeTagProcessor {
    private final static String ATTRIBUTE_NAME = "time";

    public TimeNameAttributeTagProcessor(String dialectPrefix) {
        super(
                TemplateMode.HTML, // This processor will apply only to HTML mode
                dialectPrefix,     // Prefix to be applied to name for matching
                null,              // No tag name: match any tag name
                false,             // No prefix to be applied to tag name
                ATTRIBUTE_NAME,    // Name of the attribute that will be matched
                true,              // Apply dialect prefix to attribute name
                10000,             // Precedence (inside dialect's own precedence)
                true);             // Remove the matched attribute afterward
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        final var configuration = context.getConfiguration();
        final var parser = StandardExpressions.getExpressionParser(configuration);
        final var expression = parser.parseExpression(context, attributeValue);
        final var instant = (Instant) expression.execute(context);
        final var dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.systemDefault());
        final var text = dateFormatter.format(instant);
        final var escapedText = HtmlEscape.escapeHtml5(text);
        structureHandler.setBody(escapedText, false);
    }
}
