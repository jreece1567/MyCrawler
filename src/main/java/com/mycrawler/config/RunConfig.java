/**
 *
 */
package com.mycrawler.config;

import java.util.List;

/**
 * @author jreece@us.westfield.com
 *
 */
public class RunConfig {

    String includeHost;
    List<String> includeUrls;
    List<String> excludeUrls;
    Integer numberOfCrawlers;
    String seedUrl;
    String outputFolder;

    /**
     * @return the includeHost
     */
    public String getIncludeHost() {
        return includeHost;
    }

    /**
     * @param includeHost
     *            the includeHost to set
     */
    public void setIncludeHost(final String includeHost) {
        this.includeHost = includeHost;
    }

    /**
     * @return the includeUrls
     */
    public List<String> getIncludeUrls() {
        return includeUrls;
    }

    /**
     * @param includeUrls
     *            the includeUrls to set
     */
    public void setIncludeUrls(final List<String> includeUrls) {
        this.includeUrls = includeUrls;
    }

    /**
     * @return the excludeUrls
     */
    public List<String> getExcludeUrls() {
        return excludeUrls;
    }

    /**
     * @param excludeUrls
     *            the excludeUrls to set
     */
    public void setExcludeUrls(final List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    /**
     * @return the numberOfCrawlers
     */
    public Integer getNumberOfCrawlers() {
        return numberOfCrawlers;
    }

    /**
     * @param numberOfCrawlers
     *            the numberOfCrawlers to set
     */
    public void setNumberOfCrawlers(final Integer numberOfCrawlers) {
        this.numberOfCrawlers = numberOfCrawlers;
    }

    /**
     * @return the seedUrl
     */
    public String getSeedUrl() {
        return seedUrl;
    }

    /**
     * @param seedUrl
     *            the seedUrl to set
     */
    public void setSeedUrl(final String seedUrl) {
        this.seedUrl = seedUrl;
    }

    /**
     * @return the outputFolder
     */
    public String getOutputFolder() {
        return outputFolder;
    }

    /**
     * @param outputFolder
     *            the outputFolder to set
     */
    public void setOutputFolder(final String outputFolder) {
        this.outputFolder = outputFolder;
    }

}
