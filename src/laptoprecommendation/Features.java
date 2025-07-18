package laptoprecommendation;

import java.util.List;
import java.util.Map;

import SearchFrequency.SearchFreq;
import spellcheckingusingtrie.SpellCheckingMainClass;
import Wordcompletion.wordcompletionTries;

public class Features {

    private static final String DATA_FILE = "all_laptops_data.csv";
    private static final SearchFreq sf = new SearchFreq();

    public static void main(String[] args) {
//        addSearchedWordCount("java");
//        addSearchedWordCount("python");
//        addSearchedWordCount("java");
//        addSearchedWordCount("kotlin");
//        addSearchedWordCount("go");
//        addSearchedWordCount("java");
//
//        System.out.println("Top 5 searched words:");
//        for (Map.Entry<String, Integer> entry : getTop5SearchedWords()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }
    }

    // Correct return type based on SearchFreq
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
}
