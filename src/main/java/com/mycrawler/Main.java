/**
 *
 */
package com.mycrawler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import org.apache.http.message.BasicHeader;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mycrawler.config.MyCrawlerConfig;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * @author jreece@us.westfield.com
 *
 */
public class Main {

    /**
     * A Gson instance
     */
    private final Gson gson = new GsonBuilder().serializeNulls()
            .setPrettyPrinting().create();

    /**
     * Date-format for yyyymmddhhmmsss-style date-times.
     */
    private final static SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyyMMddHHmmssSSS");

    /**
     * The timestamp of the start of this crawl
     */
    private static String fileTimestamp = "";

    /**
     * The timestamp of the start of the crawl
     */
    private static Long crawlStartTime = null;

    /**
     * The timestamp of the end of the crawl
     */
    private static Long crawlEndTime = null;

    /**
     * Construct a CrawlController
     *
     * @param runConfig
     *            the configuration parameters
     * @return the configured CrawlController
     *
     * @see edu.uci.ics.crawler4j.crawler.CrawlController
     * @see edu.uci.ics.crawler4j.crawler.CrawlConfig
     * @see edu.uci.ics.crawler4j.fetcher.PageFetcher
     * @see edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
     * @see edu.uci.ics.crawler4j.robotstxt.RobotstxtServer
     */
    private CrawlController getController(final MyCrawlerConfig runConfig) {

        // Configure the crawer
        final CrawlConfig config = new CrawlConfig();
        config.setAuthInfos(runConfig.getCrawlConfig().getAuthInfos());
        config.setConnectionTimeout(runConfig.getCrawlConfig()
                .getConnectionTimeout());
        config.setCrawlStorageFolder(runConfig.getCrawlConfig()
                .getCrawlStorageFolder());
        config.setDefaultHeaders(new HashSet<BasicHeader>());
        config.setFollowRedirects(runConfig.getCrawlConfig()
                .isFollowRedirects());
        config.setIncludeBinaryContentInCrawling(runConfig.getCrawlConfig()
                .isIncludeBinaryContentInCrawling());
        config.setIncludeHttpsPages(runConfig.getCrawlConfig()
                .isIncludeHttpsPages());
        config.setMaxConnectionsPerHost(runConfig.getCrawlConfig()
                .getMaxConnectionsPerHost());
        config.setMaxDepthOfCrawling(runConfig.getCrawlConfig()
                .getMaxDepthOfCrawling());
        config.setMaxDownloadSize(runConfig.getCrawlConfig()
                .getMaxDownloadSize());
        config.setMaxOutgoingLinksToFollow(runConfig.getCrawlConfig()
                .getMaxOutgoingLinksToFollow());
        config.setMaxPagesToFetch(runConfig.getCrawlConfig()
                .getMaxPagesToFetch());
        config.setMaxTotalConnections(runConfig.getCrawlConfig()
                .getMaxTotalConnections());
        config.setOnlineTldListUpdate(runConfig.getCrawlConfig()
                .isOnlineTldListUpdate());
        config.setPolitenessDelay(runConfig.getCrawlConfig()
                .getPolitenessDelay());
        config.setProcessBinaryContentInCrawling(runConfig.getCrawlConfig()
                .isProcessBinaryContentInCrawling());
        config.setProxyHost(runConfig.getCrawlConfig().getProxyHost());
        config.setProxyPort(runConfig.getCrawlConfig().getProxyPort());
        config.setProxyUsername(runConfig.getCrawlConfig().getProxyUsername());
        config.setProxyPassword(runConfig.getCrawlConfig().getProxyPassword());
        config.setSocketTimeout(runConfig.getCrawlConfig().getSocketTimeout());
        config.setShutdownOnEmptyQueue(runConfig.getCrawlConfig()
                .isShutdownOnEmptyQueue());

        // Configure the controller for this crawl.
        final PageFetcher pageFetcher = new PageFetcher(config);

        final RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setCacheSize(runConfig.getRobotstxtConfig()
                .getCacheSize());
        robotstxtConfig.setEnabled(runConfig.getRobotstxtConfig().isEnabled());
        robotstxtConfig.setUserAgentName(runConfig.getRobotstxtConfig()
                .getUserAgentName());

        final RobotstxtServer robotstxtServer = new RobotstxtServer(
                robotstxtConfig, pageFetcher);

        try {
            // Instantiate the controller for this crawl
            final CrawlController controller = new CrawlController(config,
                    pageFetcher, robotstxtServer);

            return controller;

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Start the crawler.
     *
     * @param config
     *            the configuration
     */
    private void runCrawler(final MyCrawlerConfig config) {

        // set empty arrays if needed
        if (config.getRunConfig().getIncludeUrls() == null) {
            config.getRunConfig().setIncludeUrls(new ArrayList<String>());
        }
        if (config.getRunConfig().getExcludeUrls() == null) {
            config.getRunConfig().setExcludeUrls(new ArrayList<String>());
        }

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        fileTimestamp = sdf.format(new Date());

        // update the output folder-name to ensure uniqueness
        config.getRunConfig().setOutputFolder(
                config.getRunConfig().getOutputFolder() + "/" + fileTimestamp);

        config.getCrawlConfig().setCrawlStorageFolder(
                config.getCrawlConfig().getCrawlStorageFolder() + "/"
                        + fileTimestamp);

        // set up log4j/slf4j
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.ERROR);

        // get the crawl-controller and run the crawl
        final CrawlController controller = getController(config);
        if (controller != null) {

            // configure the crawler with custom-config settings
            MyCrawler.setIncludeHost(config.getRunConfig().getIncludeHost());
            MyCrawler.setIncludeUrls(config.getRunConfig().getIncludeUrls());
            MyCrawler.setExcludeUrls(config.getRunConfig().getExcludeUrls());
            MyCrawler.setTotalErrors(0);
            MyCrawler.setTotalPages(0);
            MyCrawler.setVisitedUrls(new ArrayList<JsonObject>());

            // set the starting url for the crawl
            controller.addSeed(config.getRunConfig().getSeedUrl());

            // start the clock
            crawlStartTime = System.currentTimeMillis();
            System.out.println("Start: " + new Date(crawlStartTime));

            // start the crawl, wait for it to finish
            controller.start(MyCrawler.class, config.getRunConfig()
                    .getNumberOfCrawlers());

            // shut down the crawler threads
            controller.shutdown();

            // stop the clock
            crawlEndTime = System.currentTimeMillis();
        }
    }

    /**
     * Write the JSON-format crawl-results to a file, and send email if email is
     * configured.
     *
     * @param config
     *            the configuration
     */
    private void summarizeCrawl(final MyCrawlerConfig config) {

        if (crawlEndTime == null) {
            crawlEndTime = System.currentTimeMillis();
        }

        // print summary stats
        System.out.println("  Start: " + new Date(crawlStartTime));
        System.out.println("   Stop: " + new Date(crawlEndTime));
        System.out
                .println("Elapsed: " + (crawlEndTime - crawlStartTime) + "ms");

        System.out.println("Total pages: " + MyCrawler.getTotalPages());
        System.out.println("Total errors: " + MyCrawler.getTotalErrors());

        final String filename = config.getRunConfig().getOutputFolder() + "/"
                + "crawled_" + fileTimestamp + ".json";

        // write the crawl-results for each url to a file in JSON format
        FileUtils.writeFileFromString(filename,
                gson.toJson(MyCrawler.getVisitedUrls()));

        // send the crawl-results out via email
        new SendEmail(config, filename).doSendEmail();

    }

    /**
     * @param args
     *            command-line arguments
     */
    private void instanceMain(final String[] args) {

        if (args.length > 0) {

            // first argument is the YML config filename
            try {

                final InputStream in = Files.newInputStream(Paths.get(args[0]));
                final MyCrawlerConfig config = new Yaml().loadAs(in,
                        MyCrawlerConfig.class);

                System.out.println("Configuration:\n" + gson.toJson(config)
                        + "\n-----------------------------------");

                // write the data we have when the program ends
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        summarizeCrawl(config);
                    }
                });

                // run with configured values
                runCrawler(config);

            } catch (final NoSuchFileException nsfe) {

                System.err.println("Configuration file [" + args[0]
                        + "] was not found.");

            } catch (final IOException e) {

                e.printStackTrace();
            }

        } else {

            System.err.println("Usage: Main <YML-config-file-path-and-name>");
        }
    }

    /**
     * @param args
     *            command-line arguments
     */
    public static void main(final String[] args) {

        final Main main = new Main();
        main.instanceMain(args);
    }

}
