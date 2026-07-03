package com.authify.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;
	
	@Value("${spring.mail.properties.mail.smtp.from}")
	private String fromEmail;
	
	public void sendWelcomeEmail(String toEmail, String name) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject("Welcome to Our Platform");
		message.setText("Hello "+name+",\n\n Thanks for registering with us!\n\nRegards,\nAuthify Team");
		mailSender.send(message);
	}
	
	public void sendResetOtpEmail(String toEmail, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject("Password Reset OTP");
		message.setText("Your otp for reset "+otp+". Use this OTP to proceed with resetting yout password.");
	    mailSender.send(message);
	}
	
	public void sendOtpEmail(String toEmail, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject("Account Verification OTP");
		message.setText("Your OTP is  "+otp+". Verify your account using this password.");
	    mailSender.send(message);
	}
	
}
