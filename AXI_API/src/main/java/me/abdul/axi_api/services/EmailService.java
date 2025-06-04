package me.abdul.axi_api.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import me.abdul.axi_api.entities.User;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context; // Corrected import

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendEmail(User userSender, User userReceiver) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("michiel@kinderfabriek.nl", "Anchr");
            helper.setTo(userReceiver.getEmail());
            helper.setSubject("Feedback Ontvangen van " + userSender.getFirstName() + " " + userSender.getLastName());

            // Prepare Thymeleaf model with dynamic values
            Context model = new Context(); // Thymeleaf's Context
            model.setVariable("senderFirstName", userSender.getFirstName());
            model.setVariable("senderLastName", userSender.getLastName());
            model.setVariable("receiverFirstName", userReceiver.getFirstName());

            // Process the template with Thymeleaf
            String emailBody = templateEngine.process("email-template", model);
            helper.setText(emailBody, true);

            // Send the email
            javaMailSender.send(message);
            System.out.println("Email succesvol verzonden.");

        } catch (MailException e) {
            e.printStackTrace();
            System.out.println("E-mail verzenden mislukt.");
        } catch (MessagingException e) {
            throw new RuntimeException("Er is een fout opgetreden tijdens het voorbereiden van de e-mail.", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
