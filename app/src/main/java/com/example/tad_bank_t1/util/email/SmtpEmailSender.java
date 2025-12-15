package com.example.tad_bank_t1.util.email;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SmtpEmailSender {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface Callback {
        void onSuccess();
        void onError(String error);
    }

    public void sendHtmlAsync(
            String smtpHost,
            int smtpPort,
            String smtpUser,
            String smtpPass,
            String fromName,
            String toEmail,
            String subject,
            String html,
            Callback cb
    ) {
        executor.execute(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true"); // TLS (587)
                props.put("mail.smtp.host", smtpHost);
                props.put("mail.smtp.port", String.valueOf(smtpPort));
                props.put("mail.smtp.ssl.trust", smtpHost);

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpUser, smtpPass);
                    }
                });

                MimeMessage msg = new MimeMessage(session);

                // From (hiển thị tên)
                msg.setFrom(new InternetAddress(smtpUser, fromName, "UTF-8"));

                // To
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

                // Subject UTF-8
                msg.setSubject(subject, "UTF-8");

                // HTML body UTF-8
                msg.setContent(html, "text/html; charset=UTF-8");

                Transport.send(msg);

                if (cb != null) cb.onSuccess();
            } catch (Exception e) {
                if (cb != null) cb.onError(e.getMessage() != null ? e.getMessage() : e.toString());
            }
        });
    }
}
