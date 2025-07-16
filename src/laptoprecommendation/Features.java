package laptoprecommendation;

import java.util.List;
import spellcheckingusingtrie.SpellCheckingMainClass;
import Wordcompletion.wordcompletionTries;
import Wordcompletion.Trie;

public class Features {

    private static final String DATA_FILE = "all_laptops_data.csv";

    public static void main(String[] args) {
        List<String> suggestions = WordCompletion("hp");

        System.out.println("🔍 Word Completion Suggestions:");
        for (String suggestion : suggestions) {
            System.out.println(suggestion);
        }
    }

    public static List<String> WordCompletion(String prefix) {
    	wordcompletionTries WordcompletionTries = new wordcompletionTries(DATA_FILE);
        return WordcompletionTries.wordCompletion(prefix);
    }

    public static List<String> SpellCheck(String word) {
        SpellCheckingMainClass spc = new SpellCheckingMainClass(DATA_FILE);
        return spc.SpellCheckingUsingTrie(word);
    }
}
