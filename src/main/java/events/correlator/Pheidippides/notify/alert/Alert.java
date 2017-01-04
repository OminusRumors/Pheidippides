package events.correlator.Pheidippides.notify.alert;

import javax.mail.*;
import javax.mail.internet.*;

import java.util.Map;
import java.util.Properties;

import javax.activation.*;

public class Alert {

	public static void sendEmail(String[] recipients, Map<String, String> msg) {
		final String username = "ominusrumors@gmail.com";
		final String password = "wolfenstein";

		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Address[] recipientsArray= new Address[recipients.length];
			for (int i=0;i<recipients.length;i++){
				recipientsArray[i]= new InternetAddress(recipients[i]);
			}
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("ominusrumors@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, recipientsArray);
			message.setSubject(msg.get("title"));
			message.setText(msg.toString());

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

}
