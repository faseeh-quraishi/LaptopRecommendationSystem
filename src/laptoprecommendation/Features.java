package laptoprecommendation;

import SearchFrequency.SearchFreq;
import spellcheckingusingtrie.SpellCheckingMainClass;
import Wordcompletion.wordcompletionTries;

import java.io.IOException;
import java.util.*;


import AssignmentCodes.InvertedIndexCSV;
import pageRanking.PageRankerMainClass;
import pageRanking.Laptop;
import FrequencyFinder.FrequencyFinder;
import FrequencyFinder.FrequencyFinder.MatchRecord;

public class Features {

    private static final String DATA_FILE = "all_laptops_data.csv";
    private static final SearchFreq sf = new SearchFreq();

    public static void main(String[] args) {
        // Sample test for search frequency
//        sf.addSearchedWordCount("java");
//        sf.addSearchedWordCount("python");
//        sf.addSearchedWordCount("java");
//        sf.addSearchedWordCount("kotlin");
//        sf.addSearchedWordCount("go");
//        sf.addSearchedWordCount("java");
//
//        System.out.println("Top 5 searched words:");
//        for (Map.Entry<String, Integer> entry : sf.getTop5SearchedWords()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }
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

    public static List<String> FrequencySearch(String keyword) {
        List<MatchRecord> matches = FrequencyFinder.findMatches(keyword, DATA_FILE);
        List<String> output = new ArrayList<>();
        for (MatchRecord match : matches) {
            output.add("Found at line " + match.lineNumber + ", index " + match.position);
        }
        return output;
    }
}
