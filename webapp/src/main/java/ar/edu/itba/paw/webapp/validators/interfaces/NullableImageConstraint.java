package ar.edu.itba.paw.webapp.validators.interfaces;

import ar.edu.itba.paw.webapp.validators.NullableImageConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NullableImageConstraintValidator.class)
public @interface NullableImageConstraint {
    String message() default"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
