package com.example.tad_bank_t1.util.report;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class FileEmail {

    // ✅ Cấu hình Gmail đúng
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 465; // SSL port (hoặc 587 nếu dùng STARTTLS)
    private static final String USERNAME = "dang0582366729@gmail.com";
    private static final String PASSWORD = "ywxamaapaxaisynv"; // App Password 16 ký tự

    public interface Callback {
        void onSuccess();
        void onError(Exception e);
    }

    // Gửi OTP đơn giản
    public static void sendOtpEmail(String recipientEmail, String otpCode) throws Exception {
        String subject = "Mã xác thực OTP";
        String body = "Kính chào quý khách!\n\n"
                + "Mã OTP của bạn là: <b>" + otpCode + "</b>\n"
                + "Mã có hiệu lực trong 5 phút.\n\n"
                + "Vui lòng không chia sẻ mã này với bất kỳ ai.\n"
                + "Trân trọng,\nTAD Bank";

        sendEmail(recipientEmail, subject, body, null, null);
    }

    // Gửi email text thường
    public static void sendEmail(String recipientEmail, String subject, String body) throws Exception {
        sendEmail(recipientEmail, subject, body, null, null);
    }

    // Gửi email kèm file đính kèm (Excel, PDF, CSV...)
    public static void sendEmailWithAttachment(
            String recipientEmail,
            String subject,
            String body,
            File attachment,
            String attachmentMimeType) throws Exception {

        sendEmail(recipientEmail, subject, body, attachment, attachmentMimeType);
    }

    // Hàm chính xử lý gửi email (có/không attachment)
    private static void sendEmail(
            String recipientEmail,
            String subject,
            String body,
            File attachment,
            String attachmentMimeType) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true"); // SSL cho port 465
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        // Tạo message
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject, "UTF-8");

        // Phần nội dung
        Multipart multipart = new MimeMultipart();

        // Text body
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body != null ? body : "", "UTF-8");
        multipart.addBodyPart(textPart);

        // Đính kèm file (nếu có)
        if (attachment != null && attachment.exists() && attachment.length() > 0) {
            MimeBodyPart attachmentPart = new MimeBodyPart();

            // Dùng FileDataSource → tốt hơn ByteArrayDataSource (hỗ trợ file lớn, không load hết RAM)
            DataSource source = new FileDataSource(attachment);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(attachment.getName());

            // Nếu biết MIME type (ví dụ: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet cho .xlsx)
            if (attachmentMimeType != null && !attachmentMimeType.isEmpty()) {
                attachmentPart.setHeader("Content-Type", attachmentMimeType);
            }

            multipart.addBodyPart(attachmentPart);
        }

        message.setContent(multipart);
        Transport.send(message);
    }

    // Gửi async (không block UI thread Android)
    public static void sendEmailWithAttachmentAsync(
            String recipientEmail,
            String subject,
            String body,
            File attachment,
            String attachmentMimeType,
            Callback callback) {

        new Thread(() -> {
            try {
                sendEmail(recipientEmail, subject, body, attachment, attachmentMimeType);
                if (callback != null) callback.onSuccess();
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onError(e);
            }
        }).start();
    }
}
