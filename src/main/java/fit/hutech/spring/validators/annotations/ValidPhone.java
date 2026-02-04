package fit.hutech.spring.validators.annotations;

import fit.hutech.spring.validators.ValidPhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidPhoneValidator.class)
public @interface ValidPhone {
    String message() default "Phone already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
