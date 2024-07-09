package com.uj.stxtory.service.mail;

import com.uj.stxtory.domain.dto.stock.StockInfo;
import com.uj.stxtory.domain.entity.GmailToken;
import com.uj.stxtory.domain.entity.TargetMail;
import com.uj.stxtory.repository.TargetEailRepository;
import com.uj.stxtory.service.stock.TreeDayPriceService;
import com.uj.stxtory.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    TokenService tokenService;

    private final TargetEailRepository targetEailRepository;

    @Autowired
    TreeDayPriceService treeDayPriceService;

    public MailService(TargetEailRepository targetEailRepository) {
        this.targetEailRepository = targetEailRepository;
    }

    public void gmailTaretEmailSave(String target) {
        targetEailRepository.save(new TargetMail(target));
    }

    public List<TargetMail> getTargets() {
        return targetEailRepository.findAll();
    }

    public void sendGmail(String title, String content) {
        GmailToken gmailToken = tokenService.getGmailToken();
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
            e.printStackTrace();
        }
    }

    public boolean treeDaysMailSend () {
        StringBuilder msg = new StringBuilder();
        try {
            List<StockInfo> stockInfos = treeDayPriceService.start();
            for (StockInfo info : stockInfos) {
                String content = String.format("%s\t%s\n", info.getCode(), info.getName());
                System.out.print(content);
                msg.append(content);
            }
            sendGmail(new Date() + " - 종목", msg.toString());
            return true;
        }catch (Exception e){
            System.out.println("error! to threeDaysMailSend, msg is " + msg.toString());
            return false;
        }
    }
}
