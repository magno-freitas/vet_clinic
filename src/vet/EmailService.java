package vet;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.logging.Logger;
import vet.config.AppConfig;
import vet.util.LoggerUtil;
import vet.util.ValidationUtil;
import vet.exception.VetClinicException;

public class EmailService {
    private static final Logger logger = LoggerUtil.getLogger();

    public static void sendEmail(String to, String subject, String text) {
        ValidationUtil.validateEmail(to);
        ValidationUtil.validateNotEmpty(subject, "subject");
        ValidationUtil.validateNotEmpty(text, "message text");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", AppConfig.getSmtpHost());
        props.put("mail.smtp.port", AppConfig.getSmtpPort());

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    AppConfig.getEmailUsername(),
                    AppConfig.getEmailPassword());
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(AppConfig.getEmailUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            LoggerUtil.logInfo("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            LoggerUtil.logError("Failed to send email to: " + to, e);
            throw new VetClinicException("Failed to send email", e);
        }
    }
}