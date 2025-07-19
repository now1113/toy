package site.kimnow.toy.user.repository.verification;

import site.kimnow.toy.user.domain.UserVerification;

public interface UserVerificationRepository {

    UserVerification findByToken(String token);
}
