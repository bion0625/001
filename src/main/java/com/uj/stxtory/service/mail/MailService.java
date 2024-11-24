package com.uj.stxtory.service.mail;

import com.uj.stxtory.domain.dto.deal.DealItem;
import com.uj.stxtory.domain.entity.GmailToken;
import com.uj.stxtory.domain.entity.Stock;
import com.uj.stxtory.domain.entity.TargetMail;
import com.uj.stxtory.repository.TargetEailRepository;
import com.uj.stxtory.service.token.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Transactional
@Service
@Slf4j
public class MailService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TargetEailRepository targetEailRepository;

    @Autowired
    private TemplateEngine templateEngine;

    public void gmailTaretEmailSave(String target) {
        targetEailRepository.save(new TargetMail(target));
    }

    public List<TargetMail> getTargets() {
        return targetEailRepository.findAllByDeletedAtIsNull();
    }

    public void sendGmail(String title, String content) {
        GmailToken gmailToken = tokenService.getGmailToken();
        // 등록된 토큰 없으면 메일 발송 정지
        if (gmailToken == null) return;

        String from = gmailToken.getFromEmail();
        String password = gmailToken.getGmailToken();
        List<TargetMail> targets = getTargets();
        for (TargetMail to : targets) {
            sendMailByGoogle(from, to.getEmail(), password, title, content);
        }
    }

    public void sendMailByGoogle(String from, String to, String password, String title, String content) {
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(title);
            msg.setText(content);

            Transport.send(msg);
        } catch (Exception e) {
            log.info("sendMailByGoogle error");
        }
    }

    public boolean treeDaysMailSend (List<Stock> all, String title) {
        StringBuilder msg = new StringBuilder();
        try {
            for (Stock info : all) {
                String content = String.format("%s\t%s\tcnt:%d\n", info.getCode(), info.getName(), info.getRenewalCnt());
                System.out.print(content);
                msg.append(content);
            }
            sendGmail(LocalDateTime.now().toString() + title, msg.toString());
            return true;
        }catch (Exception e){
            log.info("threeDaysMailSend error \n1) msg: " + msg + "\n2) error: " + e.getMessage());
            return false;
        }
    }

    public boolean gmailTaretEmailDelete(String target) {
        TargetMail email = targetEailRepository.findByEmail(target).orElse(null);

        if (email == null) return false;

        email.setDeletedAt(LocalDateTime.now());
        return true;
    }

    private final Map<String, String> titleDescriptions =
            Map.of("select", " - 선택 종목" , "delete", " - 매도 종목");
    private final String SELECT = "select";
    private final String DELETE = "delete";

    public void noticeSelect(List<DealItem> select, String title) {
        if (select.size() == 0) return;
        notice(select, title + titleDescriptions.get(SELECT));
    }

    public void noticeDelete(List<DealItem> deleted, String title) {
        if (deleted.size() == 0) return;
        notice(deleted, title + titleDescriptions.get(DELETE));
    }

    private void notice (List<DealItem> all, String title) {
        try {
            Context context = new Context();
            context.setVariable("items", all);
            context.setVariable("subject", title);

            String htmlContent = templateEngine.process("/gmail", context);

            sendGmailWithHtml(title + ": " + LocalDateTime.now(), htmlContent);
        }catch (Exception e){
            log.info("notice error \n1) title: " + title + "\n2) error: " + e.getMessage());
        }
    }

    public void sendGmailWithHtml(String title, String htmlContent) throws MessagingException {
        GmailToken gmailToken = tokenService.getGmailToken();
        // 등록된 토큰 없으면 메일 발송 정지
        if (gmailToken == null) return;

        String from = gmailToken.getFromEmail();
        String password = gmailToken.getGmailToken();
        List<TargetMail> targets = getTargets();
        for (TargetMail to : targets) {
            // 메일 서버 설정
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(Integer.parseInt("587"));
            mailSender.setUsername(from);
            mailSender.setPassword(password);

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            // MimeMessage 생성
            MimeMessage message = mailSender.createMimeMessage();

            // MimeMessageHelper 사용하여 이메일 설정
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // true: HTML 형식으로 전송
            helper.setFrom(from);
            helper.setTo(to.getEmail());
            helper.setSubject(title);
            helper.setText(htmlContent, true); // HTML 콘텐츠로 설정

            // 메일 전송
            mailSender.send(message);
        }
    }
}
