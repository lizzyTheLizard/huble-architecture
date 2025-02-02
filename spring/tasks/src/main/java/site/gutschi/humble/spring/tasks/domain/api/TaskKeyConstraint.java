package site.gutschi.humble.spring.tasks.domain.api;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = TaskKeyConstraint.TaskKeyValidator.class)
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface TaskKeyConstraint {
    String message() default "Invalid task key '${validatedValue}'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class TaskKeyValidator implements ConstraintValidator<TaskKeyConstraint, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return false;
            }
            final var split = value.split("-");
            if (split.length != 2) {
                return false;
            }

            try {
                Integer.parseInt(split[1]);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}
