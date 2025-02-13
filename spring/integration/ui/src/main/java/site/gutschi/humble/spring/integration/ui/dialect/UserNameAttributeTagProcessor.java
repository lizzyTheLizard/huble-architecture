package site.gutschi.humble.spring.integration.ui.dialect;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;
import site.gutschi.humble.spring.users.model.User;

public class UserNameAttributeTagProcessor extends AbstractAttributeTagProcessor {
    private final static String ATTRIBUTE_NAME = "username";

    public UserNameAttributeTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, null, false, ATTRIBUTE_NAME, true, 10000, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        final var configuration = context.getConfiguration();
        final var parser = StandardExpressions.getExpressionParser(configuration);
        final var expression = parser.parseExpression(context, attributeValue);
        final var user = (User) expression.execute(context);
        if (user == null) {
            structureHandler.setBody("", false);
            return;
        }
        final var text = user.getName() + " (" + user.getEmail() + ")";
        final var escapedText = HtmlEscape.escapeHtml5(text);
        structureHandler.setBody(escapedText, false);
    }
}
