package laptoprecommendation;

import SearchFrequency.SearchFreq;
import spellcheckingusingtrie.SpellCheckingMainClass;
import Wordcompletion.wordcompletionTries;

import java.io.IOException;
import java.util.*;

import Crawler.HTMLtoTextConverter;
import Crawler.RegexExtractor;
import Crawler.UWCrawler;
import pageRanking.PageRankerMainClass;
import pageRanking.Laptop;
import FrequencyFinder.FrequencyFinder;
import FrequencyFinder.FrequencyFinder.MatchRecord;
import InvertedIndex.InvertedIndexCSV;

public class Features {

    private static final String DATA_FILE = "all_laptops_data.csv";
    private static final SearchFreq sf = new SearchFreq();

    public static void main(String[] args) {
        Map<String, String> result = crawlAndExtract("https://www.hp.com/ca-en/shop/offer.aspx?p=contact-hp-store");

        System.out.println("----- Final Extracted Data -----");
        result.forEach((key, value) -> System.out.println(key + ": " + value));
    }

    public static Map<String, Integer> addSearchedWordCount(String word) {
        return sf.addSearchedWordCount(word);
    }

    public static List<Map.Entry<String, Integer>> getTop5SearchedWords() {
        return sf.getTop5SearchedWords();
    }

    public static List<String> WordCompletion(String prefix) {
        wordcompletionTries wordCompletionTries = new wordcompletionTries(DATA_FILE);
        return wordCompletionTries.wordCompletion(prefix);
    }

    public static List<String> SpellCheck(String word) {
        SpellCheckingMainClass spc = new SpellCheckingMainClass(DATA_FILE);
        return spc.SpellCheckingUsingTrie(word);
    }

    public static List<Laptop> SearchProduct(String word) {
        try {
            Set<Integer> rowsToRank = InvertedIndexCSV.InvertedIndexing(word, DATA_FILE);

            if (rowsToRank.isEmpty()) {
                return Collections.emptyList();
            }

            return PageRankerMainClass.pageRanking(word, rowsToRank);
        } catch (IOException e) {
            System.out.println("Error in SearchProduct: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static Map<String, Object> FrequencySearch(String keyword) {
        List<MatchRecord> matches = FrequencyFinder.findMatches(keyword, DATA_FILE);
        int totalCount = matches.size();
        Map<String, Object> result = new HashMap<>();
        result.put("word", keyword);
        result.put("occurrence", totalCount);
        return result;
    }

    // -------------------- Crawler Features --------------------
    public static Map<String, String> crawlAndExtract(String url) {
        runWebCrawler(url);

        String domain = getDomainName(url); // extract domain like "uwaterloo.ca"

        runHtmlToTextConverter(domain);     // only process HTMLs from this domain
        return runRegexExtractor(domain);   // only extract from domain-specific text
    }

    public static void runWebCrawler(String startUrl) {
        UWCrawler crawler = new UWCrawler();
        crawler.crawl(startUrl);
    }

    public static void runHtmlToTextConverter(String domain) {
        HTMLtoTextConverter.convertDomain(domain);
    }

    public static Map<String, String> runRegexExtractor(String domain) {
        return RegexExtractor.extractMatchesForDomain(domain);
    }

    private static String getDomainName(String url) {
        try {
            return new java.net.URI(url).getHost().replaceFirst("^www\\.", "");
        } catch (Exception e) {
            throw new RuntimeException("Invalid URL: " + url);
        }
    }
}
