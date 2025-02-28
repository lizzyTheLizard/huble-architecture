package site.gutschi.humble.spring.integration.thymeleaf.error;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

class LoggingExceptionHandlerExceptionResolver extends ExceptionHandlerExceptionResolver {
    private Log warnLoggerCopy;

    @Override
    public void setWarnLogCategory(@NonNull String loggerName) {
        this.warnLoggerCopy = (StringUtils.hasLength(loggerName) ? LogFactory.getLog(loggerName) : null);
        super.setWarnLogCategory(loggerName);
    }

    @Override
    protected void logException(@NonNull Exception ex, @NonNull HttpServletRequest request) {
        if (this.warnLoggerCopy != null && this.warnLoggerCopy.isWarnEnabled()) {
            this.warnLoggerCopy.warn(buildLogMessage(ex, request), ex);
        }
    }
}
