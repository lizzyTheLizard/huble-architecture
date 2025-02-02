package site.gutschi.humble.spring.main.ui;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;
import site.gutschi.humble.spring.users.model.User;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class UserH6ElementTagProcessor extends AbstractElementTagProcessor {
    private static final String TAG_NAME = "userh6";

    public UserH6ElementTagProcessor(String dialectPrefix) {
        super(
                TemplateMode.HTML, // This processor will apply only to HTML mode
                dialectPrefix,     // Prefix to be applied to name for matching
                null,              // No tag name: match any tag name
                false,             // No prefix to be applied to tag name
                TAG_NAME,          // Name of the attribute that will be matched
                true,              // Apply dialect prefix to attribute name
                10000);            // Precedence (inside dialect's own precedence)
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        final var configuration = context.getConfiguration();
        final var parser = StandardExpressions.getExpressionParser(configuration);
        final var user = (User) parser
                .parseExpression(context, tag.getAttributeValue("user"))
                .execute(context);
        final var instant = (Instant) parser
                .parseExpression(context, tag.getAttributeValue("instant"))
                .execute(context);

        final var dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.systemDefault());
        final var text = user.getName() + " (" + user.getEmail() + ")" + dateFormatter.format(instant);

        //TODO: This does not replace text with h6 tag...
        final var modelFactory = context.getModelFactory();
        final var model = modelFactory.createModel();
        model.add(modelFactory.createOpenElementTag("h6"));
        model.add(modelFactory.createText(HtmlEscape.escapeHtml5(text)));
        model.add(modelFactory.createCloseElementTag("h6"));
        structureHandler.replaceWith(model, false);
    }

}
