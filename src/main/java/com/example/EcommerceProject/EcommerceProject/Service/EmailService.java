package com.example.EcommerceProject.EcommerceProject.Service;

import com.example.EcommerceProject.EcommerceProject.Entity.Product.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        logger.info("Sending email to: {}", to);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.setFrom("divyanshi.sarraf@tothenew.com");
        mailSender.send(message);
        logger.info("Email successfully sent to: {}", to);
    }
    @Async
    public void sendProductActivateToAdmin(Product product){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("divyanshi.sarraf@tothenew.com");
        message.setSubject("New Product Awaiting Approval");
        message.setText("A new product has been added by seller: "+"\n\n" +
                "Product Name: " + product.getName() + "\n" +
                "Brand: " + product.getBrand() + "\n" +
                "Please review and activate the product.");
        mailSender.send(message);
    }
    public void sendProductActivationUpdateToSeller(Product product) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(product.getSeller().getEmail());
        message.setSubject("Product Activation Status Update");



        message.setText("Hello " + product.getSeller().getFirstName() + ",\n\n" +
                "Your product has been " + "Activated" + ".\n\n" +
                "Product Details:\n" +
                "Name: " + product.getName() + "\n" +
                "Brand: " + product.getBrand() + "\n" +
                "Category: " + product.getCategory().getName() + "\n" +
                "Status: " + "Activated" + "\n\n"
               );

        mailSender.send(message);
    }
    public void sendProductDeActivationUpdateToSeller(Product product) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(product.getSeller().getEmail());
        message.setSubject("Product DeActivation Status Update");



        message.setText("Hello " + product.getSeller().getFirstName() + ",\n\n" +
                "Your product has been " + "DeActivated" + ".\n\n" +
                "Product Details:\n" +
                "Name: " + product.getName() + "\n" +
                "Brand: " + product.getBrand() + "\n" +
                "Category: " + product.getCategory().getName() + "\n" +
                "Status: " + "DeActivated" + "\n\n"
        );

        mailSender.send(message);
    }
}

