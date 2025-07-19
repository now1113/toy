package site.kimnow.toy.user.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import site.kimnow.toy.user.event.UserJoinedEvent;
import site.kimnow.toy.user.service.UserVerificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserVerificationService userVerificationService;

    @Async
    @TransactionalEventListener
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000)
    )
    public void handleUserJoinedEvent(UserJoinedEvent event) {
        log.info("User joined event email:{}", event.getEmail());
        userVerificationService.save(event);
    }

    @Recover
    public void recover(Exception e, UserJoinedEvent event) {
        log.error("Failed to send verification email after multiple retries. email: {}, error: {}",
                event.getEmail(), e.getMessage());
    }
}
