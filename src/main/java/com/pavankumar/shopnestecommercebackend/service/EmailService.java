package com.pavankumar.shopnestecommercebackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    @Async
    public void sendOrderConfirmation
            (String email, String userName, Long orderId, BigDecimal totalAmount){
        try{
            SimpleMailMessage mailMessage=new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject("ShopNest — Order Confirmed #" + orderId);
            mailMessage.setText(
                    "Hello " + userName + ",\n\n" +
                            "Your order #" + orderId + " has been placed successfully!\n" +
                            "Total Amount: ₹" + totalAmount + "\n\n" +
                            "We will update you when your order is shipped.\n\n" +
                            "Thank you for shopping with ShopNest!"
            );
            mailSender.send(mailMessage);
        }
        catch (Exception e){
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
    @Async
    public void sendOrderCancellation(String email,String userName,Long orderId){
        try{
            SimpleMailMessage mailMessage=new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject("ShopNest — Order Cancelled # " + orderId);
            mailMessage.setText(
                    "Hello " + userName + ",\n\n" +
                            "Your order #" + orderId + " has been cancelled.\n" +
                            "Your stock has been restored.\n\n" +
                            "ShopNest Team"
            );
            mailSender.send(mailMessage);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
