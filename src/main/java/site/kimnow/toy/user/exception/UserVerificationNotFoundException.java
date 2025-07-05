package site.kimnow.toy.user.exception;

import site.kimnow.toy.common.exception.BusinessException;

public class UserVerificationNotFoundException extends BusinessException {
    public UserVerificationNotFoundException() {
        super(UserErrorCode.USER_VERIFICATION_NOT_FOUND);
    }
}
