package site.kimnow.toy.common.mail.sender;

public interface EmailSender {
    void send(String to, String subject, String content);
}
