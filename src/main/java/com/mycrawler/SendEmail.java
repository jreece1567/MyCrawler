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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycrawler.config.EmailConfig;

/**
 * @author jnatalini@us.westfield.com
 *
 */
public class SendEmail {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory
            .getLogger(SendEmail.class);

    /**
     * A Gson instance
     */
    private final Gson gson = new GsonBuilder().serializeNulls()
            .setPrettyPrinting().create();

    private final EmailConfig config;
    private final String filename;

    /**
     * @param config
     *            the crawler configuration, which contains the email
     *            configuration
     * @param filename
     *            the name of the file to be attached to the email message
     */
    public SendEmail(final EmailConfig config, final String filename) {
        this.config = config;
        this.filename = filename;
    }

    /**
     * @param config
     *            the crawler configuration, which contains the email
     *            configuration
     */
    public SendEmail(final EmailConfig config) {
        this.config = config;
        this.filename = null;
    }

    /**
     * Send email to recipients using configuration settings for sender,
     * recipients, etc.
     */
    public void doSendEmail() {

        // if there is no 'from' address, there is nothing for us to do
        if ((config.getEmailFrom() == null)
                || (config.getEmailFrom().trim().length() < 1)) {
            logger.warn("No 'from' email address supplied, no email sent.");
            return;
        }

        // if there are no 'to' addresses, there is nothing for us to do
        if ((config.getEmailRecipients() == null)
                || (config.getEmailRecipients().size() < 1)) {
            logger.warn("No 'to' email addresses supplied, no email sent.");
            return;
        }

        // if there is no smtp host specified, there is nothing for us to do
        if ((config.getSmtpHost() == null)
                || (config.getSmtpHost().trim().length() < 1)) {
            logger.warn("No SMTP 'host' supplied, no email sent.");
            return;
        }

        // if there is no smtp port specified, there is nothing for us to do
        if (config.getSmtpPort() == null) {
            logger.warn("No SMTP 'port' supplied, no email sent.");
            return;
        }

        // Setup mail server
        final Properties props = System.getProperties();

        props.setProperty("mail.smtp.host", config.getSmtpHost());

        props.setProperty("mail.smtp.port",
                String.valueOf(config.getSmtpPort()));

        if (config.getSmtpAuth() != null) {
            props.put("mail.smtp.auth", config.getSmtpAuth());
        }
        if (config.getTlsEnable() != null) {
            props.put("mail.smtp.starttls.enable", config.getTlsEnable());
        }

        // Get the default Session object, configured with our property values.
        final Session session = Session.getDefaultInstance(props);

        try {
            // Create a default MimeMessage object.
            final MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(config.getEmailFrom()));

            for (final String recipient : config.getEmailRecipients()) {
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
            logger.info("Sent message successfully....");

        } catch (final MessagingException mex) {

            logger.error(mex.getMessage());
            logger.error("\nConfiguration:\n" + gson.toJson(config)
                    + "\n-------------------------\n");
            mex.printStackTrace();
        }
    }

}
