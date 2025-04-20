package com.tutions.student.paymentreceipt.email;
 
import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
 
/**
 * This is a simple mail service that one can send an email.
 * 
 * The send method handles both simple message and message with attachment.
 * 
 * @author rkapa
 * 
 * @since 1.0.0
 */
public class StudentMailService {

	private static final Logger LOG = Logger
			.getLogger(StudentMailService.class);

	/**
	 * Smtp send mail settings for gmail.
	 */
	private static final String SMTP_HOST = "mail.smtp.host";
	private static final String SMTP_HOST_VALUE = "smtp.mail.yahoo.com";

	private static final String SMTP_PORT = "mail.smtp.port";
	private static final String SMTP_PORT_VALUE = "465";

	private static final String SMTP_TLS_ENABLE = "mail.smtp.starttls.enable";
	private static final String SMTP_TLS_ENABLE_VALUE = "true";

	private static final String SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
	private static final String SMTP_SSL_ENABLE_VALUE = "true";

	private static final String SMTP_AUTH = "mail.smtp.auth";
	private static final String SMTP_AUTH_VALUE = "true"

	private static StudentMailService studentMailService;

	public static StudentMailService getInstance() {

		if (studentMailService == null) {
			studentMailService = new StudentMailService();
		}

		return studentMailService;
	}

	private MimeMessage createMultipartMessage(
			Session mailSession, InternetAddress toAddress,
			InternetAddress fromAddress, String subject, String messageText,
			File... fileAttachments)
			throws MessagingException {
		MimeMessage message = new MimeMessage(mailSession);
		message.setRecipient(RecipientType.TO, toAddress);
		message.setFrom(fromAddress);
		message.setSubject(subject);

		Multipart multipart = new MimeMultipart();

		MimeBodyPart messageBodyPart = new MimeBodyPart();

		// fill message
		messageBodyPart.setText(messageText);
		multipart.addBodyPart(messageBodyPart);

		// Part two is attachment
		for (File fileAttachment : fileAttachments) {
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(fileAttachment);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(fileAttachment.getName());

			multipart.addBodyPart(messageBodyPart);
		}

		message.setContent(multipart);

		return message;
	}

	private MimeMessage createSimpleMessage(Session mailSession,
			InternetAddress toAddress, InternetAddress fromAddress,
			String subject, String messageText) throws MessagingException {

		MimeMessage simpleMessage = new MimeMessage(mailSession);
		simpleMessage.setRecipient(RecipientType.TO, toAddress);
		simpleMessage.setFrom(fromAddress);
		simpleMessage.setSubject(subject);
		simpleMessage.setText(messageText);

		return simpleMessage;

	}

	private Properties configureSendMail() {
		Properties props = new Properties();
		
		props.put(SMTP_HOST, SMTP_HOST_VALUE);
		props.put(SMTP_PORT, SMTP_PORT_VALUE);
		props.put(SMTP_AUTH, SMTP_AUTH_VALUE);
		props.put(SMTP_TLS_ENABLE, SMTP_TLS_ENABLE_VALUE);
		props.put(SMTP_SSL_ENABLE, SMTP_SSL_ENABLE_VALUE);
		
		return props;
	}

	public void send(String mailTo, String subject, String text,
			File... attachments) {

		Properties properties = configureSendMail();

		Session mailSession = Session.getDefaultInstance(properties,

		new javax.mail.Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
			}
		});
		
		InternetAddress fromAddress = null;
		InternetAddress toAddress = null;

		try {
			fromAddress = new InternetAddress(SMTP_USER);
			toAddress = new InternetAddress(mailTo);
		} catch (AddressException addressException) {
			LOG.error("Error creating mail address", addressException);
		}

		try {

			MimeMessage message = null;
			if (attachments != null && attachments.length < 1) {
				message = createSimpleMessage(mailSession, toAddress,
					fromAddress, subject, text);
			} else {
				message = createMultipartMessage(mailSession, toAddress,
						fromAddress, subject, text, attachments);
			}
			Transport.send(message);
		} catch (MessagingException messagingException) {
			LOG.error("Error sending email", messagingException);
		}
	}

	public static void main(String... args) {

		StudentMailService mailService = StudentMailService.getInstance();
		mailService
				.send("rkapa2006@yahoo.com", "Test Message", "This is Test.");
	}
}
