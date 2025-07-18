package laptoprecommendation;

import SearchFrequency.SearchFreq;
import spellcheckingusingtrie.SpellCheckingMainClass;
import Wordcompletion.wordcompletionTries;
import java.util.*;
import java.util.stream.Collectors;

import spellcheckingusingtrie.SpellCheckingMainClass;
import Wordcompletion.wordcompletionTries;
import Wordcompletion.Trie;
import AssignmentCodes.InvertedIndexCSV;

public class Features {

    private static final String DATA_FILE = "all_laptops_data.csv";
    private static final SearchFreq sf = new SearchFreq();

    public static void main(String[] args) {
        Set<Integer> suggestions = InvertedIndexCSV.InvertedIndexing("laptop", DATA_FILE);
        System.out.println("🔍 Word Completion Suggestions:");
        for (int suggestion : suggestions) {
            System.out.println(suggestion);
        }
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

    public static List<String> SearchProduct(String word) {
        //USE THE FOLLOWING FUNCTION TO FETCH THE CSV INDEXES.
        // For Jill
        // InvertedIndexCSV.InvertedIndexing(word, DATA_FILE)
        List<Integer> result = new ArrayList<>(InvertedIndexCSV.InvertedIndexing(word, DATA_FILE));
        return result.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

}
