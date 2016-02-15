package com.mycrawler;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycrawler.config.EmailConfig;
import com.mycrawler.config.MyCrawlerConfig;

/**
 * @author jnatalini@us.westfield.com
 *
 */
public class SendEmail {

    /**
     * A Gson instance
     */
    private final Gson gson = new GsonBuilder().serializeNulls()
            .setPrettyPrinting().create();

    private final EmailConfig emailConfig;
    private final String filename;

    /**
     * @param config
     *            the crawler configuration, which contains the email
     *            configuration
     * @param filename
     *            the name of the file to be attached to the email message
     */
    public SendEmail(final MyCrawlerConfig config, final String filename) {
        this.emailConfig = config.getEmailConfig();
        this.filename = filename;
    }

    /**
     * @param config
     *            the crawler configuration, which contains the email
     *            configuration
     */
    public SendEmail(final MyCrawlerConfig config) {
        this.emailConfig = config.getEmailConfig();
        this.filename = null;
    }

    /**
     * Send email to recipients using configuration settings for sender,
     * recipients, etc.
     */
    public void doSendEmail() {

        // if there is no 'from' address, there is nothing for us to do
        if ((emailConfig.getEmailFrom() == null)
                || (emailConfig.getEmailFrom().trim().length() < 1)) {
            System.err
                    .println("No 'from' email address supplied, no email sent.");
            return;
        }

        // if there are no 'to' addresses, there is nothing for us to do
        if ((emailConfig.getEmailRecipients() == null)
                || (emailConfig.getEmailRecipients().size() < 1)) {
            System.err
                    .println("No 'to' email addresses supplied, no email sent.");
            return;
        }

        // if there is no smtp host specified, there is nothing for us to do
        if ((emailConfig.getSmtpHost() == null)
                || (emailConfig.getSmtpHost().trim().length() < 1)) {
            System.err.println("No SMTP 'host' supplied, no email sent.");
            return;
        }

        // if there is no smtp port specified, there is nothing for us to do
        if (emailConfig.getSmtpPort() == null) {
            System.err.println("No SMTP 'port' supplied, no email sent.");
            return;
        }

        // Setup mail server
        final Properties props = System.getProperties();

        props.setProperty("mail.smtp.host", emailConfig.getSmtpHost());

        props.setProperty("mail.smtp.port",
                String.valueOf(emailConfig.getSmtpPort()));

        if (emailConfig.getSmtpAuth() != null) {
            props.put("mail.smtp.auth", emailConfig.getSmtpAuth());
        }
        if (emailConfig.getTlsEnable() != null) {
            props.put("mail.smtp.starttls.enable", emailConfig.getTlsEnable());
        }

        // Get the default Session object, configured with our property values.
        final Session session = Session.getDefaultInstance(props);

        try {
            // Create a default MimeMessage object.
            final MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(emailConfig.getEmailFrom()));

            for (final String recipient : emailConfig.getEmailRecipients()) {
                message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(recipient));
            }
            // Set Subject: header field
            message.setSubject("Westfield WebCrawler result on " + filename);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart
                    .setText("Please see attached file with results. Thank you for using the Westfield WebCrawler!");
            if (filename != null) {

                // Create a multipart message
                final Multipart multipart = new MimeMultipart();

                // part one is the text message part
                multipart.addBodyPart(messageBodyPart);

                // Part two is the attachment (if present)
                if (filename != null) {
                    messageBodyPart = new MimeBodyPart();

                    final DataSource source = new FileDataSource(filename);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(filename);

                    multipart.addBodyPart(messageBodyPart);
                }

                // Send the complete message parts
                message.setContent(multipart);
            }

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");

        } catch (final MessagingException mex) {

            System.out.println("\nConfiguration:\n" + gson.toJson(emailConfig)
                    + "\n-------------------------\n");
            mex.printStackTrace();
        }
    }

}
