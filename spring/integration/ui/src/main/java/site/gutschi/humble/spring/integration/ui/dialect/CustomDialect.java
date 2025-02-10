package site.gutschi.humble.spring.integration.ui.dialect;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.Set;

public class CustomDialect extends AbstractProcessorDialect {
    private static final String DIALECT_NAME = "Custom Dialect";
    private static final String DIALECT_PREFIX = "gs";

    public CustomDialect() {
        super(DIALECT_NAME, DIALECT_PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        return Set.of(
                new NewLineAttributeTagProcessor(DIALECT_PREFIX),
                new UserNameAttributeTagProcessor(DIALECT_PREFIX),
                new TimeNameAttributeTagProcessor(DIALECT_PREFIX));
    }
}
