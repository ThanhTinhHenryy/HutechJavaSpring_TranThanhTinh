package fit.hutech.spring.validators;

import fit.hutech.spring.services.UserService;
import fit.hutech.spring.validators.annotations.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidEmailValidator implements
        ConstraintValidator<ValidEmail, String> {
    public ValidEmailValidator() {}
    @Autowired
    private UserService userService;
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) return true;
        String normalized = email.trim().toLowerCase();
        if (userService == null) return true;
        try {
            return !userService.existsByEmail(normalized);
        } catch (Exception ignored) {
            return true;
        }
    }
}
