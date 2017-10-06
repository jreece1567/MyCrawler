/**
 *
 */
package com.mycrawler;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author jreece@us.westfield.com
 *
 */
public class MyCrawler extends WebCrawler {

    /**
     * the number of pages crawled
     */
    static Integer totalPages;

    /**
     * the number of pages that returned non-2xx HTTP codes when crawled
     */
    static Integer totalErrors;

    /**
     * the host that all crawled urls must belong to (eg. 'www.westfield.com')
     */
    static String includeHost;

    /**
     * the list of urls we have visited so far
     */
    static List<JsonObject> visitedUrls;

    /**
     * the list of urls to include in the crawl (if empty, all eligible urls
     * will be crawled)
     */
    static List<String> includeUrls;

    /**
     * the list of urls to exclude from the crawl (if empty, all eligible urls
     * will be crawled)
     */
    static List<String> excludeUrls;

    /**
     * Regexp to match file-types that should not be crawled
     */
    private final static Pattern FILETYPE_FILTERS = Pattern
            .compile("([^\\s]+(\\.(?i)("
                    + "css|js|bmp|gif|jpe?g|png|tiff?|ico|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz|jar|woff"
                    + "))[^\\s]*$)");

    /**
     * Date-format for zoned ISO8601-style date-times.
     */
    private final static SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'XXX");

    /**
     * The time that the HTTP request was made
     */
    private Long startTime;

    /**
     * @return the host to be included in the crawl
     */
    public static String getIncludeHost() {
        return includeHost;
    }

    /**
     * Set the host that all crawled urls must belong to (eg.
     * 'www.westfield.com')
     *
     * @param includeHost
     *            the host to be included
     */
    public static void setIncludeHost(final String includeHost) {
        MyCrawler.includeHost = includeHost;
    }

    /**
     * @return the number of pages crawled
     */
    public static Integer getTotalPages() {
        return totalPages;
    }

    /**
     * @param totalPages
     *            the number of pages crawled
     */
    public static void setTotalPages(final Integer totalPages) {
        MyCrawler.totalPages = totalPages;
    }

    /**
     * @return the number of pages that returned non-2xx HTTP codes when crawled
     */
    public static Integer getTotalErrors() {
        return totalErrors;
    }

    /**
     * @param totalErrors
     *            the number of pages that returned non-2xx HTTP codes when
     *            crawled
     */
    public static void setTotalErrors(final Integer totalErrors) {
        MyCrawler.totalErrors = totalErrors;
    }

    /**
     * @return the map of urls we have visited so far
     */
    public static List<JsonObject> getVisitedUrls() {
        return visitedUrls;
    }

    /**
     * @param visited
     *            the map of urls we have visited so far
     */
    public static void setVisitedUrls(final List<JsonObject> visited) {
        MyCrawler.visitedUrls = visited;
    }

    /**
     * @return the includeUrls
     */
    public static List<String> getIncludeUrls() {
        return includeUrls;
    }

    /**
     * @param includeUrls
     *            the includeUrls to set
     */
    public static void setIncludeUrls(final List<String> includeUrls) {
        MyCrawler.includeUrls = includeUrls;
    }

    /**
     * @return the excludeUrls
     */
    public static List<String> getExcludeUrls() {
        return excludeUrls;
    }

    /**
     * @param excludeUrls
     *            the excludeUrls to set
     */
    public static void setExcludeUrls(final List<String> excludeUrls) {
        MyCrawler.excludeUrls = excludeUrls;
    }

    /**
     * @param url
     *            the url to be recorded
     */
    private void recordVisit(final WebURL url, final int statusCode,
            final String statusDescription, final long elapsed) {

        synchronized (visitedUrls) {

            final JsonObject json = new JsonObject();
            json.add("url", new JsonPrimitive(url.getURL()));
            json.add("datetime", new JsonPrimitive(sdf.format(new Date())));
            json.add("elapsed", new JsonPrimitive(elapsed));
            json.add("status", new JsonPrimitive(statusCode));
            json.add("description", new JsonPrimitive(statusDescription));

            visitedUrls.add(json);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onBeforeExit() {
        super.onBeforeExit();
    }

    @Override
    public boolean shouldVisit(final Page referringPage, final WebURL url) {

        // Don't visit urls that are not in the specified host
        if (!url.getURL().contains(includeHost)) {
            return false;
        }

        // Don't visit urls that reference 'excluded' filetypes
        if (FILETYPE_FILTERS.matcher(url.getURL().trim()).matches()) {
            return false;
        }

        if (!includeUrls.isEmpty() && !includeUrls.contains(url.getURL())) {
            return false;
        }

        if (!excludeUrls.isEmpty() && excludeUrls.contains(url.getURL())) {
            return false;
        }

        System.out.println(url.getURL());
        return true;
    }

    @Override
    public void visit(final Page page) {
    }

    @Override
    protected WebURL handleUrlBeforeProcess(final WebURL curURL) {

        this.startTime = new Date().getTime();

        return super.handleUrlBeforeProcess(curURL);
    }

    @Override
    protected void handlePageStatusCode(final WebURL webUrl,
            final int statusCode, final String statusDescription) {

        final Long elapsed = new Date().getTime() - this.startTime;

        recordVisit(webUrl, statusCode, statusDescription, elapsed);

        synchronized (MyCrawler.totalPages) {
            setTotalPages(getTotalPages() + 1);
        }

        if ((statusCode < 200) || (statusCode > 399)) {
            // some code other than 'success'

            synchronized (MyCrawler.totalErrors) {
                setTotalErrors(getTotalErrors() + 1);
            }
        }

    }

    @Override
    protected void onPageBiggerThanMaxSize(final String urlStr,
            final long pageSize) {

        super.onPageBiggerThanMaxSize(urlStr, pageSize);
    }

    @Override
    protected void onUnexpectedStatusCode(final String urlStr,
            final int statusCode, final String contentType,
            final String description) {

        super.onUnexpectedStatusCode(urlStr, statusCode, contentType,
                description);
    }

    @Override
    protected void onContentFetchError(final WebURL webUrl) {

        super.onContentFetchError(webUrl);
    }

    @Override
    protected void onUnhandledException(final WebURL webUrl, final Throwable e) {

        if (e instanceof SocketTimeoutException) {
            handlePageStatusCode(webUrl, 500, e.getLocalizedMessage());
        }
        super.onUnhandledException(webUrl, e);
    }

    @Override
    protected void onParseError(final WebURL webUrl) {

        super.onParseError(webUrl);
    }
}
