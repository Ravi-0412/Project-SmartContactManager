package com.smart.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	public boolean sendEmail(String subject, String message, String to) {

		boolean f = false;

		String from = "ravicodingmadeeasy@gmail.com";

		// variable for gmail host. gmail work on smtp
		String host = "smtp.gmail.com";

		// get the system properties to able configuration of host protocol i.e smtp
		Properties properties = System.getProperties();
		System.out.println("PROPERTIES: " + properties);

		// setting important information to properties object
		// data is passed inside properties in form of key value pair
		// 1) host set
		properties.put("mail.smtp.host", host);

		// 2) port set
		// google is port pe sahi kam karta h , aise bhut sare port h(search on google)
		properties.put("mail.smtp.port", "465");

		// 3) enable ssl(Secure Sockets Layer) for security purpose
		properties.put("mail.smtp.ssl.enable", "true");

		// 4) for authorization
		properties.put("mail.smtp.auth", "true");

		// steps to send the mail
		// a) to get the session object

		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication("ravicodingmadeeasy@gmail.com", "eqabdjiuacruixef"); 
//				username and passord jisse	bhejna h. password ke liye 'app password' use karo google ka inside 2-Step Authentication																	// 
																									
			}
		});

		session.setDebug(true); // to get info on console

		// b) compose the message [text/multimedia]
		MimeMessage mimeMessage = new MimeMessage(session);

		// setting the things for sending the message

		try {

			// i) from email
			mimeMessage.setFrom(from);

			// ii) adding recepients(jin sbko bhejna h)
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// iii) adding subject to message
			mimeMessage.setSubject(subject);

			// iv )ading text to message
//			mimeMessage.setText(message);
			mimeMessage.setContent(message, "text/html");  // 2nd one ki kis form me bhejna h

			// c) now can send the message using transport class
			Transport.send(mimeMessage);

			System.out.println("sent successfully......");
			f = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

}
