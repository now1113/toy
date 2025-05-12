package site.kimnow.toy.common.validate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import site.kimnow.toy.common.validate.annotation.PasswordMatch;
import site.kimnow.toy.user.dto.request.UserJoinRequest;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserJoinRequest> {

    @Override
    public boolean isValid(UserJoinRequest request, ConstraintValidatorContext context) {
        if (request.getPassword() == null || request.getConfirmPassword() == null) {
            return false;
        }

        return request.getPassword().equals(request.getConfirmPassword());
    }
}
