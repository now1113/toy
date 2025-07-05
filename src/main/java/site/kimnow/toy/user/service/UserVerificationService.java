package site.kimnow.toy.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.kimnow.toy.common.constant.Constants;
import site.kimnow.toy.common.properties.SmtpMailProperties;
import site.kimnow.toy.common.mail.sender.EmailSender;
import site.kimnow.toy.user.domain.UserVerification;
import site.kimnow.toy.user.event.UserJoinedEvent;
import site.kimnow.toy.user.repository.UserVerificationRepositoryAdapter;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserVerificationService {

    private final UserVerificationRepositoryAdapter userVerificationRepositoryAdapter;
    private final SmtpMailProperties smtpMailProperties;
    private final EmailSender emailSender;

    @Transactional
    public void save(UserJoinedEvent event) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expireAt = LocalDateTime.now().plusHours(24);

        UserVerification userVerification = UserVerification.of(event.getEmail(), token, expireAt);
        userVerificationRepositoryAdapter.save(userVerification);

        sendVerificationEmail(userVerification.getEmail(), token);
    }

    private void sendVerificationEmail(String email, String token) {
        String link = smtpMailProperties.getVerificationUrlPrefix() + token;
        String content = String.format("이메일 인증을 위해 아래 링크를 클릭해주세요:%n%n%s", link);

        emailSender.send(email, Constants.EMAIL_TITLE, content);
    }

    @Transactional(readOnly = true)
    public UserVerification findByToken(String token) {
        UserVerification userVerification = userVerificationRepositoryAdapter.findByToken(token);
        userVerification.validateNotExpired();

        return userVerification;
    }

    @Transactional
    public void delete(UserVerification userVerification) {

    }
}
