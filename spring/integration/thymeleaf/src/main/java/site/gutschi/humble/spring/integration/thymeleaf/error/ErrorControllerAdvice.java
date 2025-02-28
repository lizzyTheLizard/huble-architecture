package site.gutschi.humble.spring.integration.thymeleaf.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.exceptions.TemplateInputException;
import site.gutschi.humble.spring.common.exception.InvalidInputException;
import site.gutschi.humble.spring.common.exception.NotAllowedException;
import site.gutschi.humble.spring.common.exception.NotFoundException;

import java.util.Arrays;

@ControllerAdvice
@Controller
@SuppressWarnings("SameReturnValue")
public class ErrorControllerAdvice {

    @Value("${site.gutschi.humble.spring.integration.thymeleaf.stacktrace;:false}")
    private boolean stacktrace;

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException exception, Model model) {
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", exception.getPublicMessage());
        model.addAttribute("trace", getStackTrace(exception));
        return "error";
    }

    @ExceptionHandler({NotAllowedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleNotAllowedException(NotAllowedException exception, Model model) {
        model.addAttribute("status", HttpStatus.FORBIDDEN.value());
        model.addAttribute("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        model.addAttribute("message", exception.getPublicMessage());
        model.addAttribute("trace", getStackTrace(exception));
        return "error";
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException exception, Model model) {
        final var violations = exception.getConstraintViolations();
        final var messageBuilder = new StringBuilder();
        messageBuilder.append("There was an error with your input, and it could not be parsed");
        if (violations.isEmpty()) {
            messageBuilder.append(".");
        } else {
            messageBuilder.append(": ");
            violations.forEach(v -> messageBuilder.append(v.getMessage()).append(", "));
        }
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        model.addAttribute("message", messageBuilder.toString());
        model.addAttribute("trace", getStackTrace(exception));
        return "error";
    }

    @ExceptionHandler({InvalidInputException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidInputException(InvalidInputException exception, Model model) {
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        model.addAttribute("message", exception.getPublicMessage());
        model.addAttribute("trace", getStackTrace(exception));
        return "error";
    }

    @ExceptionHandler({TemplateInputException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleTemplateInputException(TemplateInputException exception, Model model) {
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        model.addAttribute("message", "There was a general error while trying to answer your request");
        model.addAttribute("trace", getStackTrace(exception));
        return "error";
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception exception, Model model) {
        if (exception instanceof ErrorResponse errorResponse) {
            final var code = errorResponse.getStatusCode().value();
            final var httpStatus = HttpStatus.resolve(code);
            final var error = httpStatus != null ? httpStatus.getReasonPhrase() : "Error";
            model.addAttribute("status", code);
            model.addAttribute("error", error);
            model.addAttribute("message", exception.getMessage());
        } else {
            model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            model.addAttribute("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            model.addAttribute("message", "There was a general error while trying to answer your request");
        }
        model.addAttribute("trace", getStackTrace(exception));
        return "error";
    }

    private String getStackTrace(Exception exception) {
        if (stacktrace) {
            final var sb = new StringBuilder();
            sb.append(exception.toString()).append("\n");
            Arrays.stream(exception.getStackTrace())
                    .map(StackTraceElement::toString)
                    .map(s -> "    " + s + "\n")
                    .forEach(sb::append);
            return sb.toString();
        }
        return null;
    }
}
