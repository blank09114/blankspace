package kr.io.blankspace.service.account;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:}")
    private String from;

    public void sendJoinVerifyMail(String to, String verifyLink) {
        String html = loadTemplate("templates/mail/join.html")
                .replace("{{VERIFY_LINK}}", verifyLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject("[BLANKSPACE] 이메일 인증을 완료해주세요");
            helper.setText(html, true);

            if (from != null && !from.isBlank()) helper.setFrom(from);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("메일 발송 실패", e);
        }
    }

    private String loadTemplate(String classpathLocation) {
        try {
            ClassPathResource res = new ClassPathResource(classpathLocation);
            byte[] bytes = res.getInputStream().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("메일 템플릿 로드 실패: " + classpathLocation, e);
        }
    }
}
