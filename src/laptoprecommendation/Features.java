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
        // Testing Laptop Recommendation System Features
        Map<String, Object> output = FrequencySearch("m1");
        System.out.println(output);

        // Testing Crawler Features
        runWebCrawler("https://uwaterloo.ca");
        runHtmlToTextConverter();
        runRegexExtractor();
    }

    // -------------------- Laptop Recommendation Features --------------------
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
    public static void runWebCrawler(String startUrl) {
        UWCrawler crawler = new UWCrawler();
        crawler.crawl(startUrl);
    }

    public static void runHtmlToTextConverter() {
        HTMLtoTextConverter.main(new String[]{});
    }

    public static void runRegexExtractor() {
        RegexExtractor.main(new String[]{});
    }
}
