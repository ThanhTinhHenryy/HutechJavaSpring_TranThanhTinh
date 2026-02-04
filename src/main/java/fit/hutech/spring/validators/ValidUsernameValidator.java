package fit.hutech.spring.validators;

import fit.hutech.spring.services.UserService;
import fit.hutech.spring.validators.annotations.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidUsernameValidator implements
        ConstraintValidator<ValidUsername, String> {
    public ValidUsernameValidator() {}
    @Autowired
    private UserService userService;
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) return true;
        String normalized = username.trim();
        if (userService == null) return true;
        try {
            return !userService.existsByUsername(normalized);
        } catch (Exception ignored) {
            return true;
        }
    }
}
