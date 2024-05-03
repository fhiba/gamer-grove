package ar.edu.itba.paw.webapp.validators.interfaces;


import ar.edu.itba.paw.webapp.validators.UsernameMustExistConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UsernameMustExistConstraintValidator.class)
public @interface UsernameMustExistConstraint {
    String message() default"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
