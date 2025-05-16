package site.kimnow.toy.user.exception;

import site.kimnow.toy.common.exception.BusinessException;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String email) {
        super(UserErrorCode.DUPLICATE_EMAIL, String.format("email=%s", email));
    }
}
