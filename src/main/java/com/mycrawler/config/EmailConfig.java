/**
 *
 */
package com.mycrawler.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jreece@us.westfield.com
 *
 */
public class EmailConfig {

    String emailFrom;
    String smtpHost;
    Integer smtpPort;
    Boolean smtpAuth;
    Boolean tlsEnable;
    List<String> emailRecipients = new ArrayList<String>();

    /**
     * @return the emailFrom
     */
    public String getEmailFrom() {
        return emailFrom;
    }

    /**
     * @param emailFrom
     *            the emailFrom to set
     */
    public void setEmailFrom(final String emailFrom) {
        this.emailFrom = emailFrom;
    }

    /**
     * @return the smtpHost
     */
    public String getSmtpHost() {
        return smtpHost;
    }

    /**
     * @param smtpHost
     *            the smtpHost to set
     */
    public void setSmtpHost(final String smtpHost) {
        this.smtpHost = smtpHost;
    }

    /**
     * @return the smtpPort
     */
    public Integer getSmtpPort() {
        return smtpPort;
    }

    /**
     * @param smtpPort
     *            the smtpPort to set
     */
    public void setSmtpPort(final Integer smtpPort) {
        this.smtpPort = smtpPort;
    }

    /**
     * @return the smtpAuth
     */
    public Boolean getSmtpAuth() {
        return smtpAuth;
    }

    /**
     * @param smtpAuth
     *            the smtpAuth to set
     */
    public void setSmtpAuth(final Boolean smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    /**
     * @return the tlsEnable
     */
    public Boolean getTlsEnable() {
        return tlsEnable;
    }

    /**
     * @param tlsEnable
     *            the tlsEnable to set
     */
    public void setTlsEnable(final Boolean tlsEnable) {
        this.tlsEnable = tlsEnable;
    }

    /**
     * @return the emailRecipients
     */
    public List<String> getEmailRecipients() {
        return emailRecipients;
    }

    /**
     * @param emailRecipients
     *            the emailRecipients to set
     */
    public void setEmailRecipients(final List<String> emailRecipients) {
        this.emailRecipients = emailRecipients;
    }

}
