package ar.edu.itba.paw.webapp.validators.interfaces;


import ar.edu.itba.paw.webapp.validators.FieldsMustMatchConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FieldsMustMatchConstraintValidator.class)
public @interface FieldsMustMatchConstraint {
    String message() default"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String first();
    String second();
}
