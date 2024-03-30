package ar.edu.itba.paw.webapp.validators.interfaces;


import ar.edu.itba.paw.webapp.validators.UniqueEmailConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UniqueEmailConstraintValidator.class)
public @interface UniqueEmailConstraint {
    String message() default"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}