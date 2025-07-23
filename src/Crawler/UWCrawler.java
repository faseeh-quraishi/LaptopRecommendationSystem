package Crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class UWCrawler {

    private static final String SAVE_DIRECTORY = "saved_pages";
    private static final int MAX_PAGES = 100;

    private Set<String> visitedPages = new HashSet<>();
    private Queue<String> pagesToVisit = new LinkedList<>();
    private String startUrl;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java UWCrawler <start-url>");
            return;
        }

        String startUrl = args[0];
        UWCrawler crawler = new UWCrawler();
        crawler.startUrl = startUrl;
        crawler.crawl(startUrl);
    }

    public void crawl(String startUrl) {
        pagesToVisit.add(startUrl);

        while (!pagesToVisit.isEmpty() && visitedPages.size() < MAX_PAGES) {
            String currentUrl = pagesToVisit.poll();

            if (currentUrl != null && !visitedPages.contains(currentUrl)) {
                visitPage(currentUrl);
            }
        }
    }

    private void visitPage(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            System.out.println("Visiting: " + url);

            visitedPages.add(url);
            saveHtmlToFile(document, url);

            Elements links = document.select("a[href]");
            for (Element link : links) {
                String href = link.absUrl("href");

                if (!href.isEmpty() && isSameDomain(href, startUrl) && !visitedPages.contains(href)) {
                    pagesToVisit.add(href);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to access: " + url + " - " + e.getMessage());
        }
    }

    private void saveHtmlToFile(Document doc, String url) {
        try {
            File directory = new File(SAVE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdir();
            }

            String safeFileName = Paths.get(
                    url.replace("https://", "")
                       .replace("http://", "")
                       .replaceAll("[^a-zA-Z0-9.-]", "_")
            ).getFileName().toString();

            File outputFile = new File(directory, safeFileName + ".html");

            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(doc.html());
            }

            System.out.println("Page saved to: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error writing file for URL: " + url + " - " + e.getMessage());
        }
    }

    private boolean isSameDomain(String url, String baseUrl) {
        String urlDomain = getDomainName(url);
        String baseDomain = getDomainName(baseUrl);
        if (urlDomain == null || baseDomain == null) {
            return false;
        }
        return urlDomain.equalsIgnoreCase(baseDomain);
    }

    private String getDomainName(String url) {
        if (url == null || url.isEmpty()) return null;
        try {
            URI uri = new URI(url.trim());
            String host = uri.getHost();
            return (host != null && host.startsWith("www.")) ? host.substring(4) : host;
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
