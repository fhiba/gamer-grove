package ar.edu.itba.paw.webapp.validators.interfaces;



import ar.edu.itba.paw.webapp.validators.UniqueUsernameConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UniqueUsernameConstraintValidator.class)
public @interface UniqueUsernameConstraint {
    String message() default"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}