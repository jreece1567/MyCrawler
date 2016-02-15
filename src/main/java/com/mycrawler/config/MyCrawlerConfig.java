/**
 *
 */
package com.mycrawler.config;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;

/**
 * @author jreece@us.westfield.com
 *
 */
public class MyCrawlerConfig {

    RunConfig runConfig;
    CrawlConfig crawlConfig;
    RobotstxtConfig robotstxtConfig;
    EmailConfig emailConfig;

    /**
     * @return the runConfig
     */
    public RunConfig getRunConfig() {
        return runConfig;
    }

    /**
     * @param runConfig
     *            the runConfig to set
     */
    public void setRunConfig(final RunConfig runConfig) {
        this.runConfig = runConfig;
    }

    /**
     * @return the crawlConfig
     */
    public CrawlConfig getCrawlConfig() {
        return crawlConfig;
    }

    /**
     * @param crawlConfig
     *            the crawlConfig to set
     */
    public void setCrawlConfig(final CrawlConfig crawlConfig) {
        this.crawlConfig = crawlConfig;
    }

    /**
     * @return the robotstxtConfig
     */
    public RobotstxtConfig getRobotstxtConfig() {
        return robotstxtConfig;
    }

    /**
     * @param robotstxtConfig
     *            the robotstxtConfig to set
     */
    public void setRobotstxtConfig(final RobotstxtConfig robotstxtConfig) {
        this.robotstxtConfig = robotstxtConfig;
    }

    /**
     * @return the emailConfig
     */
    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

    /**
     * @param emailConfig
     *            the emailConfig to set
     */
    public void setEmailConfig(final EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

}
