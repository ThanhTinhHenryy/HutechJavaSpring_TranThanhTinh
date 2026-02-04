package fit.hutech.spring.validators;

import fit.hutech.spring.services.UserService;
import fit.hutech.spring.validators.annotations.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidPhoneValidator implements
        ConstraintValidator<ValidPhone, String> {
    public ValidPhoneValidator() {}
    @Autowired
    private UserService userService;
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) return true;
        String normalized = phone.trim();
        if (userService == null) return true;
        try {
            return !userService.existsByPhone(normalized);
        } catch (Exception ignored) {
            return true;
        }
    }
}
