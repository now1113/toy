package site.kimnow.toy.common.validate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import site.kimnow.toy.common.validate.annotation.Password;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#\\$%^&*(),.?\":{}|<>]");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        boolean containsSpecialChar = SPECIAL_CHAR_PATTERN.matcher(value).find();
        boolean withinLength = value.length() <= 15;

        return containsSpecialChar && withinLength;
    }
}
