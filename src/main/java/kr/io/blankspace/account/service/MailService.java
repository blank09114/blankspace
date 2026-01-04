package kr.io.blankspace.account.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:}")
    private String from;

    // 회원가입 메일
    public void sendJoinVerifyMail(String to, String verifyLink) {
        String html = renderTemplate
        ("templates/mail/join.html", Map.of("{{VERIFY_LINK}}", verifyLink));
        sendHtml(to, "[BLANKSPACE] 이메일 인증을 완료해주세요", html);
    }

    // 비밀번호 재설정 메일
    public void sendPasswordResetMail(String to, String userId, String tempPassword, String applyLink) {
        String html = renderTemplate(
            "templates/mail/reset.html",
            Map.of("{{USER_ID}}", userId, "{{TEMP_PASSWORD}}", tempPassword, "{{RESET_APPLY_LINK}}", applyLink)
        );
        sendHtml(to, "[BLANKSPACE] 비밀번호 재설정 안내", html);
    }

    // 탈퇴 메일
    public void sendWithdrawMail(String to, String withdrawLink) {
        String html = renderTemplate(
            "templates/mail/withdraw.html",
            Map.of("{{WITHDRAW_LINK}}", withdrawLink)
        );
        sendHtml(to, "[BLANKSPACE] 회원 탈퇴 확인", html);
    }

    // 공통 유틸
    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            if (from != null && !from.isBlank()) helper.setFrom(from);

            mailSender.send(message);
        } catch (Exception e) { throw new RuntimeException("메일 발송 실패", e); }
    }

    private String renderTemplate(String classpathLocation, Map<String, String> vars) {
        String html = loadTemplate(classpathLocation);
        for (Map.Entry<String, String> e : vars.entrySet()) { html = html.replace(e.getKey(), e.getValue()); }
        return html;
    }

    private String loadTemplate(String classpathLocation) {
        try {
            ClassPathResource res = new ClassPathResource(classpathLocation);
            byte[] bytes = res.getInputStream().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
        catch (Exception e) { throw new RuntimeException("메일 템플릿 로드 실패: " + classpathLocation, e); }
    }
}