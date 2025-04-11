package com.techacademy.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailSenderService {

    private final JavaMailSender javaMailSender;

    private static final String LOCAL_HOST = "http://localhost:8080/";

    public MailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendResetTokenEmail(String email, String token) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setFrom("udonishito0506@gmail.com");
            messageHelper.setTo(email);
            messageHelper.setSubject("【プライスカード作成アプリ】パスワードの再設定");
            messageHelper.setText("\n" +
                                  "アカウントのパスワードをリセットするには、次のリンクをクリックしてください。 \n" +
                                  "\n" +
                                  LOCAL_HOST + "password/reset?token=" + token + "\n" +
                                  "\n" +
                                  "パスワードのリセットを依頼していない場合は、このメールを無視してください。\n" +
                                  "\n" +
                                  "プライスカード作成アプリ 管理部より");
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("メッセージの送信に失敗しました", e);
        }
    }

}
