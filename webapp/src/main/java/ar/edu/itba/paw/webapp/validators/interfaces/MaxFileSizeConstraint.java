package ar.edu.itba.paw.webapp.validators.interfaces;

import ar.edu.itba.paw.webapp.validators.FileMustBeImageConstraintValidator;
import ar.edu.itba.paw.webapp.validators.MaxFileSizeConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MaxFileSizeConstraintValidator.class)
public @interface MaxFileSizeConstraint {
    String message() default"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
