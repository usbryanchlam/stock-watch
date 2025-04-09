package dev.bryanlam.stockwatch.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import dev.bryanlam.stockwatch.model.StockAlert;
import dev.bryanlam.stockwatch.model.StockData;
import dev.bryanlam.stockwatch.model.User;
import dev.bryanlam.stockwatch.repository.UserRepository;
import dev.bryanlam.stockwatch.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
    
    private JavaMailSender mailSender;
    
    private UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;
    
    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }
    
    @Override
    public void sendAlertNotification(StockAlert alert, StockData stockData) {
        User user = userRepository.findById(alert.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        String subject = "Stock Alert: " + stockData.getSymbol() + " has reached your target price!";
        
        String condition = alert.getCondition().toString().toLowerCase();
        
        String content = "Hello " + user.getName() + ",<br><br>" +
                         "Your stock alert for " + stockData.getSymbol() + " (" + stockData.getCompanyName() + ") " +
                         "has been triggered!<br><br>" +
                         "Target condition: Price " + condition + " $" + alert.getTargetPrice() + "<br>" +
                         "Current price: $" + stockData.getCurrentPrice() + "<br><br>" +
                         "Thank you for using Stock Watch!";
                         
        sendEmail(user.getEmail(), subject, content);
    }
    
    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // Set to true for HTML content
            helper.setFrom(senderEmail);
            
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
