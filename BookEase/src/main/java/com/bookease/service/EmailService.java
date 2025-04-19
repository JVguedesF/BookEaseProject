package com.bookease.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    /*
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReactivationEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reative sua conta");
        message.setText("""
            Detectamos que você tentou se registrar, mas sua conta já existe e está desativada.
            
            Clique no link abaixo para reativá-la:
            
            https://bookease.com/reactivate?email=%s""".formatted(email));

        mailSender.send(message);
    }
    */
}
